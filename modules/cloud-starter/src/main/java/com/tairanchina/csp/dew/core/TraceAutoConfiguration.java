package com.tairanchina.csp.dew.core;

import ch.qos.logback.classic.Level;
import com.tairanchina.csp.dew.core.logger.DewLoggerWebMvcConfigurer;
import com.tairanchina.csp.dew.core.logger.DewTraceLogWrap;
import com.tairanchina.csp.dew.core.logger.DewTraceRestTemplateInterceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;

@Configuration
@EnableConfigurationProperties(DewCloudConfig.class)
public class TraceAutoConfiguration {

    private static final Logger logger = LoggerFactory.getLogger(TraceAutoConfiguration.class);

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private DewCloudConfig dewCloudConfig;

    @Autowired(required = false)
    private DewLoggerWebMvcConfigurer dewLoggerWebMvcConfigurer;


    public TraceAutoConfiguration(DewCloudConfig dewCloudConfig) {
        this.dewCloudConfig = dewCloudConfig;
    }

    @PostConstruct
    public void init() {
        logger.info("Load Auto Configuration : {}", this.getClass().getName());

        if (dewCloudConfig.getTraceLog().isEnabled()) {
            logger.info("Enabled Trace Log");
            restTemplate.getInterceptors().add(new DewTraceRestTemplateInterceptor());
            ch.qos.logback.classic.Logger root = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(DewTraceLogWrap.class);
            root.setLevel(Level.TRACE);
        }
    }

}