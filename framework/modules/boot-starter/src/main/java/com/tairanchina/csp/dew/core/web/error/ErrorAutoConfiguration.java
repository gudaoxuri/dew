package com.tairanchina.csp.dew.core.web.error;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.web.servlet.error.ErrorMvcAutoConfiguration;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.DispatcherServlet;

import javax.annotation.PostConstruct;
import javax.servlet.Servlet;

@Configuration
@ConditionalOnWebApplication
@ConditionalOnClass({Servlet.class, DispatcherServlet.class})
@AutoConfigureBefore(ErrorMvcAutoConfiguration.class)
@ConditionalOnProperty(prefix = "dew.basic.format", name = "useUnityError", havingValue = "true", matchIfMissing = true)
public class ErrorAutoConfiguration {

    private static final Logger logger = LoggerFactory.getLogger(ErrorAutoConfiguration.class);

    @PostConstruct
    public void init() {
        logger.info("Load Auto Configuration : {}", this.getClass().getName());
    }

    @Bean
    @SuppressWarnings("SpringJavaAutowiringInspection")
    public ErrorController errorController(ErrorAttributes errorAttributes) {
        return new ErrorController(errorAttributes);
    }
}
