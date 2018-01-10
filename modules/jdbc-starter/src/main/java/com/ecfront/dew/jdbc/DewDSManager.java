package com.ecfront.dew.jdbc;

import com.alibaba.druid.pool.DruidDataSource;
import com.ecfront.dew.Dew;
import com.ecfront.dew.jdbc.config.DewMultiDSConfig;
import com.ecfront.dew.jdbc.sharding.ShardingConfiguration;
import com.ecfront.dew.Dew;
import com.ecfront.dew.core.jdbc.DSManager;
import com.ecfront.dew.core.loding.DewLoadImmediately;
import com.ecfront.dew.jdbc.config.DewMultiDSConfig;
import com.ecfront.dew.jdbc.sharding.ShardingConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
@DewLoadImmediately
public class DewDSManager implements DSManager {

    private static final Pattern LINE_TO_CAMEL_REGEX = Pattern.compile("-[a-z]{1}");

    @Autowired
    private DewMultiDSConfig dsConfig;

    @Autowired
    private JdbcTemplate primaryJdbcTemplate;

    @Autowired
    private DataSource dataSource;

    @Autowired(required = false)
    private ShardingConfiguration shardingConfiguration;

    @Value("${spring.datasource.url}")
    private String primaryJdbcUrl;

    private DefaultListableBeanFactory beanFactory;

    @PostConstruct
    private void init() throws SQLException {
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
        // Register sharding ds
        if (shardingConfiguration != null) {
            register("sharding", shardingConfiguration.getJdbcUrls().iterator().next(), shardingConfiguration.dataSource());
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

