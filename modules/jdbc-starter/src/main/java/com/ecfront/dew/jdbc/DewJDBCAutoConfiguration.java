package com.ecfront.dew.jdbc;

import com.ecfront.dew.Dew;
import com.ecfront.dew.jdbc.config.DewJDBCConfig;
import com.ecfront.dew.jdbc.entity.EntityContainer;
import com.ecfront.dew.Dew;
import com.ecfront.dew.core.loding.DewLoadImmediately;
import com.ecfront.dew.jdbc.config.DewJDBCConfig;
import com.ecfront.dew.jdbc.entity.EntityContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.GenericApplicationContext;

import javax.annotation.PostConstruct;

@Configuration
@DewLoadImmediately
public class DewJDBCAutoConfiguration {

    private static final Logger logger = LoggerFactory.getLogger(DewJDBCAutoConfiguration.class);

    @Autowired
    private DewJDBCConfig dewJDBCConfig;

    @PostConstruct
    private void init() {
        logger.info("Enabled Dew JDBC");
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