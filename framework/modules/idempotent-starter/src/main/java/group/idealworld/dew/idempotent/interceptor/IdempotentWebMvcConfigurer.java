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

package group.idealworld.dew.idempotent.interceptor;

import group.idealworld.dew.idempotent.DewIdempotentConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

/**
 * Idempotent web mvc configurer.
 *
 * @author gudaoxuri
 */
@Configuration
@ConditionalOnWebApplication
@EnableConfigurationProperties(DewIdempotentConfig.class)
@Order(10000)
public class IdempotentWebMvcConfigurer extends WebMvcConfigurerAdapter {

    private static final Logger logger = LoggerFactory.getLogger(IdempotentWebMvcConfigurer.class);

    private DewIdempotentConfig dewIdempotentConfig;


    /**
     * Instantiates a new Idempotent web mvc configurer.
     *
     * @param dewIdempotentConfig the dew idempotent config
     */
    public IdempotentWebMvcConfigurer(DewIdempotentConfig dewIdempotentConfig) {
        logger.info("Load Auto Configuration : {}", this.getClass().getName());
        this.dewIdempotentConfig = dewIdempotentConfig;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new IdempotentHandlerInterceptor(dewIdempotentConfig)).excludePathPatterns("/error/**");
        super.addInterceptors(registry);
    }

}
