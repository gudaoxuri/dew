package com.ecfront.dew.core.jdbc;

import com.alibaba.druid.pool.DruidDataSource;
import com.ecfront.dew.core.Dew;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
@ConditionalOnClass(JdbcTemplate.class)
public class DSManager {

    private final Pattern LINE_TO_CAMEL_REGEX = Pattern.compile("-[a-z]{1}");

    @Autowired
    private DSConfig dsConfig;

    @Autowired
    private JdbcTemplate primaryJdbcTemplate;

    @Autowired
    private DataSource dataSource;

    @Value("${spring.datasource.url}")
    private String primaryJdbcUrl;

    private DefaultListableBeanFactory beanFactory;

    @PostConstruct
    private void init() throws NoSuchFieldException {
        // Register TransactionManager
        beanFactory = (DefaultListableBeanFactory) ((ConfigurableApplicationContext) Dew.applicationContext).getBeanFactory();
        AbstractBeanDefinition transactionManager = BeanDefinitionBuilder.rootBeanDefinition(DataSourceTransactionManager.class)
                .addConstructorArgValue(dataSource).getBeanDefinition();
        transactionManager.setScope("singleton");
        transactionManager.setPrimary(true);
        beanFactory.registerBeanDefinition("transactionManager", transactionManager);
        // Register primary DS
        AbstractBeanDefinition dsBean = BeanDefinitionBuilder.rootBeanDefinition(DS.class)
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
                        case "maxIdle":
                            ds.setMaxIdle(Integer.valueOf(v));
                            break;
                        case "maxWait":
                            ds.setMaxWait(Integer.valueOf(v));
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
        AbstractBeanDefinition dsBean = BeanDefinitionBuilder.rootBeanDefinition(DS.class)
                .addPropertyReference("jdbcTemplate", dsName + "JdbcTemplate")
                .addPropertyValue("jdbcUrl", jdbcUrl)
                .setInitMethodName("init").getBeanDefinition();
        dsBean.setScope("singleton");
        dsBean.setPrimary(false);
        beanFactory.registerBeanDefinition(dsName + "DS", dsBean);
    }

    public static DS select(String dsName) {
        if (dsName == null) {
            dsName = "";
        }
        if (dsName.isEmpty()) {
            return (DS) Dew.applicationContext.getBean("ds");
        } else {
            return (DS) Dew.applicationContext.getBean(dsName + "DS");
        }
    }

}

