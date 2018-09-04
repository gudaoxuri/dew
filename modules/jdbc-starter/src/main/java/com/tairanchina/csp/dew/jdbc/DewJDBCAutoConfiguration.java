package com.tairanchina.csp.dew.jdbc;

import com.tairanchina.csp.dew.Dew;
import com.tairanchina.csp.dew.core.basic.loading.DewLoadImmediately;
import com.tairanchina.csp.dew.jdbc.config.DewJDBCConfig;
import com.tairanchina.csp.dew.jdbc.entity.EntityContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.GenericApplicationContext;

import javax.annotation.PostConstruct;

@Configuration
@EnableConfigurationProperties(DewJDBCConfig.class)
@DewLoadImmediately
public class DewJDBCAutoConfiguration {

    private static final Logger logger = LoggerFactory.getLogger(DewJDBCAutoConfiguration.class);

    private DewJDBCConfig dewJDBCConfig;

    public DewJDBCAutoConfiguration(DewJDBCConfig dewJDBCConfig) {
        this.dewJDBCConfig = dewJDBCConfig;
    }

    @PostConstruct
    private void init() {
        logger.info("Load Auto Configuration : {}", this.getClass().getName());

        Dew.applicationContext.containsBean(EntityContainer.class.getSimpleName());
        // JDBC Scan
        if (!dewJDBCConfig.getJdbc().getBasePackages().isEmpty()) {
            ClassPathScanner scanner = new ClassPathScanner((BeanDefinitionRegistry) ((GenericApplicationContext) Dew.applicationContext).getBeanFactory());
            scanner.setResourceLoader(Dew.applicationContext);
            scanner.registerFilters();
            scanner.scan(dewJDBCConfig.getJdbc().getBasePackages().toArray(new String[]{}));
        }
    }


}