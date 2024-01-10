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

package group.idealworld.dew.test;

import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.LogMessageWaitStrategy;

import java.time.Duration;

public class RedisExtension implements BeforeAllCallback {

    private static final Logger LOGGER = LoggerFactory.getLogger(RedisExtension.class);

    private static final GenericContainer REDIS_CONTAINER = new GenericContainer("redis:6-alpine")
            .withExposedPorts(6379);

    @Override
    public void beforeAll(ExtensionContext extensionContext) {
        REDIS_CONTAINER.start();
        REDIS_CONTAINER.waitingFor((new LogMessageWaitStrategy()).withRegEx("Ready to accept connections").withTimes(1))
                .withStartupTimeout(Duration.ofSeconds(60L));
        LOGGER.info("Test Redis port: " + REDIS_CONTAINER.getFirstMappedPort());
    }

    /**
     * Initializer.
     */
    public static class Initializer
            implements ApplicationContextInitializer<ConfigurableApplicationContext> {
        /**
         * Initialize.
         *
         * @param configurableApplicationContext the configurable application context
         */
        public void initialize(ConfigurableApplicationContext configurableApplicationContext) {
            TestPropertyValues.of(
                    "spring.data.redis.host=" + REDIS_CONTAINER.getHost(),
                    "spring.data.redis.port=" + REDIS_CONTAINER.getFirstMappedPort(),
                    "spring.data.redis.password=",
                    "spring.data.redis.multi.other:host=" + REDIS_CONTAINER.getHost(),
                    "spring.data.redis.multi.other:port=" + REDIS_CONTAINER.getFirstMappedPort(),
                    "spring.data.redis.multi.other:database=1",
                    "spring.data.redis.multi.other:password="
            ).applyTo(configurableApplicationContext.getEnvironment());
        }
    }

}
