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
import org.testcontainers.containers.BindMode;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.LogMessageWaitStrategy;

import java.time.Duration;

public class MqttExtension implements BeforeAllCallback {

    private static final Logger logger = LoggerFactory.getLogger(MqttExtension.class);

    private static final GenericContainer mqttContainer = new GenericContainer("eclipse-mosquitto")
            .withClasspathResourceMapping("mosquitto.conf", "/mosquitto/config/mosquitto.conf", BindMode.READ_ONLY)
            .withExposedPorts(1883);

    @Override
    public void beforeAll(ExtensionContext extensionContext) {
        mqttContainer.start();
        mqttContainer.waitingFor((new LogMessageWaitStrategy()).withRegEx("mosquitto version 2\\.0\\.11 running").withTimes(1))
                .withStartupTimeout(Duration.ofSeconds(60L));
        logger.info("Test MQTT port: " + mqttContainer.getFirstMappedPort());
    }

    public static class Initializer
            implements ApplicationContextInitializer<ConfigurableApplicationContext> {
        public void initialize(ConfigurableApplicationContext configurableApplicationContext) {
            TestPropertyValues.of(
                    "dew.mw.mqtt.broker=tcp://127.0.0.1:" + mqttContainer.getFirstMappedPort(),
                    "dew.mw.mqtt.persistence=memory"
            ).applyTo(configurableApplicationContext.getEnvironment());
        }
    }

}
