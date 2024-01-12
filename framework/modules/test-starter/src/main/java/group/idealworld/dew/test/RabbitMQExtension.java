package group.idealworld.dew.test;

import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.testcontainers.containers.RabbitMQContainer;
import org.testcontainers.utility.DockerImageName;

public class RabbitMQExtension implements BeforeAllCallback {

    private static final Logger LOGGER = LoggerFactory.getLogger(RabbitMQExtension.class);

    private static final RabbitMQContainer RABBIT_MQ_CONTAINER = new RabbitMQContainer(
            DockerImageName.parse("rabbitmq:3.7.25-management-alpine"));

    @Override
    public void beforeAll(ExtensionContext extensionContext) {
        RABBIT_MQ_CONTAINER.start();
        LOGGER.info("Test Rabbit port: " + RABBIT_MQ_CONTAINER.getAmqpPort());
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
                    "spring.rabbitmq.host=" + RABBIT_MQ_CONTAINER.getHost(),
                    "spring.rabbitmq.port=" + RABBIT_MQ_CONTAINER.getAmqpPort())
                    .applyTo(configurableApplicationContext.getEnvironment());
        }
    }

}
