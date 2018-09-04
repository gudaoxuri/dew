package com.tairanchina.csp.dew.core.auth;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

@Configuration
public class AuthAutoConfiguration {

    private static final Logger logger = LoggerFactory.getLogger(AuthAutoConfiguration.class);

    @Bean
    public BasicAuthAdapter basicAuthAdapter() {
        return new BasicAuthAdapter();
    }

    @PostConstruct
    public void init() {
        logger.info("Load Auto Configuration : {}", this.getClass().getName());
    }

}
