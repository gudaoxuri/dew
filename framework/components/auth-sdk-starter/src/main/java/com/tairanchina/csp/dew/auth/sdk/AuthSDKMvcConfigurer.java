/*
 * Copyright 2019. the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.tairanchina.csp.dew.auth.sdk;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@Configuration
@ConditionalOnWebApplication
@Order(30000)
public class AuthSDKMvcConfigurer implements WebMvcConfigurer {

    @Autowired
    private AuthSDKHandlerInterceptor authSDKHandlerInterceptor;

    @Autowired
    private AuthSDKConfig authSDKConfig;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        if (authSDKConfig.getServerUrl() != null && !authSDKConfig.getServerUrl().isEmpty()) {
            registry.addInterceptor(authSDKHandlerInterceptor).excludePathPatterns("/error/**");
        }
    }

}
