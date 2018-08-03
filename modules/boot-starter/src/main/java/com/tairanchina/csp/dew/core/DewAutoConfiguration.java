package com.tairanchina.csp.dew.core;

import com.tairanchina.csp.dew.Dew;
import com.tairanchina.csp.dew.core.cluster.Cluster;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.jackson.JacksonProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.sql.SQLException;

/**
 * desription:
 * Created by ding on 2018/2/2.
 */
@Configuration
@EnableConfigurationProperties(DewConfig.class)
public class DewAutoConfiguration {
    private static final Logger logger = LoggerFactory.getLogger(DewStartup.class);

    @Value("${spring.application.name}")
    private String applicationName;

    @PostConstruct
    public void init() throws SQLException {
        Cluster.initH2Database(null, null, null);
        logger.info("Load Auto Configuration : {}", this.getClass().getName());
    }

    @Bean
    public Dew dew(DewConfig dewConfig, ApplicationContext applicationContext, @Autowired(required = false) JacksonProperties jacksonProperties) throws IOException, ClassNotFoundException {
        return new Dew(applicationName, dewConfig, jacksonProperties, applicationContext);
    }

}

