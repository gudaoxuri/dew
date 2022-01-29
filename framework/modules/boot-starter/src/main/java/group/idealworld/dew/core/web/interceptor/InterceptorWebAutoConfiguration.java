/*
 * Copyright 2022. the original author or authors
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

package group.idealworld.dew.core.web.interceptor;

import group.idealworld.dew.Dew;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.annotation.PostConstruct;

/**
 * Interceptor web auto configuration.
 *
 * @author gudaoxuri
 */
@Configuration
@ConditionalOnWebApplication
@Order(20000)
public class InterceptorWebAutoConfiguration implements WebMvcConfigurer {

    private static final Logger logger = LoggerFactory.getLogger(InterceptorWebAutoConfiguration.class);

    /**
     * Init.
     */
    @PostConstruct
    public void init() {
        logger.info("Load Auto Configuration : {}", this.getClass().getName());
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        if (Dew.dewConfig == null) {
            // 未启用web的情况下，Dew加载滞后，忽略
            return;
        }
        if (!Dew.dewConfig.getSecurity().isAuthEnabled()) {
            return;
        }
        if (Dew.dewConfig.getSecurity().isIdentInfoEnabled()) {
            registry.addInterceptor(new IdentInfoHandlerInterceptor()).excludePathPatterns("/error/**");
        } else {
            registry.addInterceptor(new TokenHandlerInterceptor()).excludePathPatterns("/error/**");
        }
        if (Dew.dewConfig.getSecurity().getRouter().isEnabled()) {
            registry.addInterceptor(new RouterHandlerInterceptor()).excludePathPatterns("/error/**");
        }
    }

    @Override
    public void configurePathMatch(PathMatchConfigurer configurer) {
        configurer.setUseTrailingSlashMatch(true);
    }
}
