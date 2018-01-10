package com.ecfront.dew.idempotent.interceptor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@Configuration
@ConditionalOnWebApplication
@Order(10000)
public class DewIdempotentWebMvcConfigurer extends WebMvcConfigurerAdapter {

    @Autowired
    private DewIdempotentHandlerInterceptor dewIdempotentHandlerInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(dewIdempotentHandlerInterceptor).excludePathPatterns("/error/**");
        super.addInterceptors(registry);
    }

}
