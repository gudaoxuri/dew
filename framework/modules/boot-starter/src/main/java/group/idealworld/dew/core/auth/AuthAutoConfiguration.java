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

package group.idealworld.dew.core.auth;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

/**
 * Auth auto configuration.
 *
 * @author gudaoxuri
 */
@Configuration
public class AuthAutoConfiguration {

    private static final Logger logger = LoggerFactory.getLogger(AuthAutoConfiguration.class);

    /**
     * Basic auth adapter.
     *
     * @return the basic auth adapter
     */
    @Bean
    public BasicAuthAdapter basicAuthAdapter() {
        return new BasicAuthAdapter();
    }

    /**
     * Init.
     */
    @PostConstruct
    public void init() {
        logger.info("Load Auto Configuration : {}", this.getClass().getName());
    }

}
