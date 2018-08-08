package com.tairanchina.csp.dew.core.logger;

import com.tairanchina.csp.dew.core.DewCloudConfig;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@Configuration
@ConditionalOnWebApplication
@ConditionalOnProperty(value = "dew.cloud.traceLog.enabled", matchIfMissing = true)
@EnableConfigurationProperties(DewCloudConfig.class)
public class TraceLoggerConfigurer extends WebMvcConfigurerAdapter {

    @Bean
    public TraceLogBeanPostProcessor traceLogBeanPostProcessor(BeanFactory beanFactory){
        return new TraceLogBeanPostProcessor(beanFactory); //对Client进行重新包装
    }
}
