package com.tairanchina.csp.dew.core.utils.convert;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

/**
 * desription:
 * Created by ding on 2018/1/25.
 */
@Configuration
public class ConvertAutoConfiguration {

    private static final Logger logger = LoggerFactory.getLogger(ConvertAutoConfiguration.class);

    @PostConstruct
    public void init() {
        logger.info("Load Auto Configuration : {}", this.getClass().getName());
    }

    @Bean
    @ConditionalOnMissingBean
    public InstantConvert instantConvert() {
        return new InstantConvert();
    }

    @Bean
    @ConditionalOnMissingBean
    public LocalDateConverter localDateConverter() {
        return new LocalDateConverter();
    }

    @Bean
    @ConditionalOnMissingBean
    public LocalTimeConverter localTimeConverter() {
        return new LocalTimeConverter();
    }


    @Bean
    @ConditionalOnMissingBean
    public LocalDateTimeConverter localDateTimeConverter() {
        return new LocalDateTimeConverter();
    }

}
