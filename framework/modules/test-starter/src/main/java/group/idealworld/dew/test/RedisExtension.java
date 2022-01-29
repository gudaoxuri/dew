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

    private static final Logger logger = LoggerFactory.getLogger(RedisExtension.class);

    private static final GenericContainer redisContainer = new GenericContainer("redis:6-alpine")
            .withExposedPorts(6379);

    @Override
    public void beforeAll(ExtensionContext extensionContext) {
        redisContainer.start();
        redisContainer.waitingFor((new LogMessageWaitStrategy()).withRegEx("Ready to accept connections").withTimes(1))
                .withStartupTimeout(Duration.ofSeconds(60L));
        logger.info("Test Redis port: " + redisContainer.getFirstMappedPort());
    }

    public static class Initializer
            implements ApplicationContextInitializer<ConfigurableApplicationContext> {
        public void initialize(ConfigurableApplicationContext configurableApplicationContext) {
            TestPropertyValues.of(
                    "spring.redis.host=" + redisContainer.getHost(),
                    "spring.redis.port=" + redisContainer.getFirstMappedPort(),
                    "spring.redis.password=" +
                            "spring.redis.multi.other:host=" + redisContainer.getHost(),
                    "spring.redis.multi.other:port=" + redisContainer.getFirstMappedPort(),
                    "spring.redis.multi.other:database=1",
                    "spring.redis.multi.other:password="
            ).applyTo(configurableApplicationContext.getEnvironment());
        }
    }

}
