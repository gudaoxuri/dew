package com.tairanchina.csp.dew.core.logger;

import feign.Client;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import javax.annotation.PostConstruct;

@Configuration
@ConditionalOnWebApplication
public class DewLoggerWebMvcConfigurer extends WebMvcConfigurerAdapter {

    private static final Logger logger = LoggerFactory.getLogger(DewLoggerWebMvcConfigurer.class);

    @PostConstruct
    private void init(){
        logger.info("Load Auto Configuration : {}", this.getClass().getName());
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        DewLoggerHandlerInterceptor dewLoggerHandlerInterceptor = new DewLoggerHandlerInterceptor();
        registry.addInterceptor(dewLoggerHandlerInterceptor).excludePathPatterns("/error/**");
        super.addInterceptors(registry);
    }

    @Override
    public void configurePathMatch(PathMatchConfigurer configurer) {
        configurer.setUseTrailingSlashMatch(true);
    }

    @Bean
    @LoadBalanced
    @ConditionalOnMissingBean
    protected RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    Client client(BeanFactory beanFactory){
        return new TraceLogFeignClient(beanFactory); //对Client进行重新包装
    }
}
