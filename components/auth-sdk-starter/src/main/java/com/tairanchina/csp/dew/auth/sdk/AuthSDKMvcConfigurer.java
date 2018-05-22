package com.tairanchina.csp.dew.auth.sdk;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@Configuration
@ConditionalOnWebApplication
@Order(30000)
public class AuthSDKMvcConfigurer extends WebMvcConfigurerAdapter {

    @Autowired
    private AuthSDKHandlerInterceptor authSDKHandlerInterceptor;

    @Autowired
    private AuthSDKConfig authSDKConfig;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        if (authSDKConfig.getServerUrl() != null && !authSDKConfig.getServerUrl().isEmpty()) {
            registry.addInterceptor(authSDKHandlerInterceptor).excludePathPatterns("/error/**");
        }
        super.addInterceptors(registry);
    }

}
