package com.tairanchina.csp.dew.jdbc;

import com.alibaba.druid.pool.DruidDataSource;
import com.tairanchina.csp.dew.Dew;
import com.tairanchina.csp.dew.core.basic.loading.DewLoadImmediately;
import com.tairanchina.csp.dew.core.jdbc.DSManager;
import com.tairanchina.csp.dew.jdbc.config.DewMultiDSConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.jdbc.JdbcTemplateAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


@Configuration
@EnableConfigurationProperties(DewMultiDSConfig.class)
@AutoConfigureAfter(JdbcTemplateAutoConfiguration.class)
@DewLoadImmediately
public class DewDSAutoConfiguration implements DSManager {

    private static final Logger logger = LoggerFactory.getLogger(DewDSAutoConfiguration.class);

    private static final Pattern LINE_TO_CAMEL_REGEX = Pattern.compile("-[a-z]{1}");

    private DewMultiDSConfig dsConfig;

    @Autowired
    private JdbcTemplate primaryJdbcTemplate;

    @Autowired
    private DataSource dataSource;

    private DefaultListableBeanFactory beanFactory;

    @Value("${spring.datasource.url}")
    private String primaryJdbcUrl;

    public DewDSAutoConfiguration(DewMultiDSConfig dsConfig) {
        this.dsConfig = dsConfig;
    }

    @PostConstruct
    private void init() throws SQLException {
        logger.info("Load Auto Configuration : {}", this.getClass().getName());
        // Register TransactionManager
        beanFactory = (DefaultListableBeanFactory) ((ConfigurableApplicationContext) Dew.applicationContext).getBeanFactory();
        AbstractBeanDefinition transactionManager = BeanDefinitionBuilder.rootBeanDefinition(DataSourceTransactionManager.class)
                .addConstructorArgValue(dataSource).getBeanDefinition();
        transactionManager.setScope("singleton");
        transactionManager.setPrimary(true);
        beanFactory.registerBeanDefinition("transactionManager", transactionManager);
        // Register primary DS
        AbstractBeanDefinition dsBean = BeanDefinitionBuilder.rootBeanDefinition(DewDS.class)
                .addPropertyValue("jdbcTemplate", primaryJdbcTemplate)
                .addPropertyValue("jdbcUrl", primaryJdbcUrl)
                .setInitMethodName("init").getBeanDefinition();
        dsBean.setScope("singleton");
        dsBean.setPrimary(true);
        beanFactory.registerBeanDefinition("ds", dsBean);
        // Register others DS
        if (dsConfig.getMultiDatasources() != null && !dsConfig.getMultiDatasources().isEmpty()) {
            for (Map.Entry<String, Map<String, String>> entry : dsConfig.getMultiDatasources().entrySet()) {
                String dsName = entry.getKey();
                DruidDataSource ds = new DruidDataSource();
                Properties properties = new Properties();
                entry.getValue().forEach((k, v) -> {
                    Matcher m = LINE_TO_CAMEL_REGEX.matcher(k);
                    while (m.find()) {
                        String str = m.group();
                        k = k.replace(str, str.substring(1).toUpperCase());
                    }
                    switch (k) {
                        case "maxActive":
                            ds.setMaxActive(Integer.valueOf(v));
                            break;
                        case "minIdle":
                            ds.setMinIdle(Integer.valueOf(v));
                            break;
                        case "maxWait":
                            ds.setMaxWait(Integer.valueOf(v));
                            break;
                        default:
                            break;
                    }
                    properties.put("druid." + k, v);
                });
                ds.setConnectProperties(properties);
                register(dsName, entry.getValue().get("url"), ds);
            }
        }
    }

    private void register(String dsName, String jdbcUrl, DataSource dataSource) {
        // Package TransactionManager
        AbstractBeanDefinition transactionManager = BeanDefinitionBuilder.rootBeanDefinition(DataSourceTransactionManager.class)
                .addConstructorArgValue(dataSource).getBeanDefinition();
        transactionManager.setScope("singleton");
        transactionManager.setPrimary(false);
        beanFactory.registerBeanDefinition(dsName + "TransactionManager", transactionManager);
        // Register JdbcTemplate
        AbstractBeanDefinition jdbcTemplate = BeanDefinitionBuilder.rootBeanDefinition(JdbcTemplate.class)
                .addConstructorArgValue(dataSource).getBeanDefinition();
        jdbcTemplate.setScope("singleton");
        jdbcTemplate.setPrimary(false);
        beanFactory.registerBeanDefinition(dsName + "JdbcTemplate", jdbcTemplate);
        // Register DS
        AbstractBeanDefinition dsBean = BeanDefinitionBuilder.rootBeanDefinition(DewDS.class)
                .addPropertyReference("jdbcTemplate", dsName + "JdbcTemplate")
                .addPropertyValue("jdbcUrl", jdbcUrl)
                .setInitMethodName("init").getBeanDefinition();
        dsBean.setScope("singleton");
        dsBean.setPrimary(false);
        beanFactory.registerBeanDefinition(dsName + "DS", dsBean);
    }

}

