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

        private static final Logger LOGGER = LoggerFactory.getLogger(MqttExtension.class);

        private static final GenericContainer MQTT_CONTAINER = new GenericContainer("eclipse-mosquitto")
                        .withClasspathResourceMapping("mosquitto.conf", "/mosquitto/config/mosquitto.conf",
                                        BindMode.READ_ONLY)
                        .withExposedPorts(1883);

        @Override
        public void beforeAll(ExtensionContext extensionContext) {
                MQTT_CONTAINER.start();
                MQTT_CONTAINER
                                .waitingFor(
                                                (new LogMessageWaitStrategy())
                                                                .withRegEx("mosquitto version 2\\.0\\.11 running")
                                                                .withTimes(1))
                                .withStartupTimeout(Duration.ofSeconds(60L));
                LOGGER.info("Test MQTT port: " + MQTT_CONTAINER.getFirstMappedPort());
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
                                        "dew.mw.mqtt.broker=tcp://127.0.0.1:" + MQTT_CONTAINER.getFirstMappedPort(),
                                        "dew.mw.mqtt.persistence=memory")
                                        .applyTo(configurableApplicationContext.getEnvironment());
                }
        }

}
