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

package group.idealworld.dew.core.basic.utils.convert;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

/**
 * Convert auto configuration.
 *
 * @author gudaoxuri
 */
@Configuration
public class ConvertAutoConfiguration {

    private static final Logger logger = LoggerFactory.getLogger(ConvertAutoConfiguration.class);

    /**
     * Init.
     */
    @PostConstruct
    public void init() {
        logger.info("Load Auto Configuration : {}", this.getClass().getName());
    }

    /**
     * Instant convert instant convert.
     *
     * @return the instant convert
     */
    @Bean
    @ConditionalOnMissingBean
    public InstantConvert instantConvert() {
        return new InstantConvert();
    }

    /**
     * Local date converter local date converter.
     *
     * @return the local date converter
     */
    @Bean
    @ConditionalOnMissingBean
    public LocalDateConverter localDateConverter() {
        return new LocalDateConverter();
    }

    /**
     * Local time converter local time converter.
     *
     * @return the local time converter
     */
    @Bean
    @ConditionalOnMissingBean
    public LocalTimeConverter localTimeConverter() {
        return new LocalTimeConverter();
    }


    /**
     * Local date time converter local date time converter.
     *
     * @return the local date time converter
     */
    @Bean
    @ConditionalOnMissingBean
    public LocalDateTimeConverter localDateTimeConverter() {
        return new LocalDateTimeConverter();
    }

}
