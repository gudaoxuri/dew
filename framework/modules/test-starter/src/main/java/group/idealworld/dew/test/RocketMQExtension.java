package group.idealworld.dew.test;

import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.testcontainers.containers.DockerComposeContainer;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public class RocketMQExtension implements BeforeAllCallback {

    private static final Logger LOGGER = LoggerFactory.getLogger(RocketMQExtension.class);

    private static final String DOCKER_COMPOSE = "version: '2'\n" +
            "services:\n" +
            "  namesrv:\n" +
            "    image: apache/rocketmq:4.9.2\n" +
            "    command: sh mqnamesrv\n" +
            "  broker:\n" +
            "    image: apache/rocketmq:4.9.2\n" +
            "    ports:\n" +
            "      - 10909:10909\n" +
            "      - 10911:10911\n" +
            "      - 10912:10912\n" +
            "    command: sh -c 'echo \"brokerIP1 = 127.0.0.1\" > ../conf/broker.conf && ./mqbroker -n namesrv:9876 -c ../conf/broker"
            +
            ".conf'\n" +
            "    depends_on:\n" +
            "      - namesrv";

    private static final Path TEMP_FILE;

    static {
        try {
            TEMP_FILE = Files.createTempFile(null, null);
            Files.write(TEMP_FILE, DOCKER_COMPOSE.getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Initializer.
     */
    public static DockerComposeContainer rocketmqContainer = new DockerComposeContainer(TEMP_FILE.toFile())
            .withExposedService("namesrv_1", 9876);

    @Override
    public void beforeAll(ExtensionContext extensionContext) {
        rocketmqContainer.start();
        String nameSrvUrl = rocketmqContainer.getServiceHost("namesrv_1", 9876)
                + ":" +
                rocketmqContainer.getServicePort("namesrv_1", 9876);
        LOGGER.info("Test RocketMQ name server url: " + nameSrvUrl);
    }

    /**
     * Initializer.
     */
    public static class Initializer
            implements ApplicationContextInitializer<ConfigurableApplicationContext> {
        /**
         * Initialize.
         *
         * @param configurableApplicationContext the application context
         */
        public void initialize(ConfigurableApplicationContext configurableApplicationContext) {
            TestPropertyValues.of(
                    "rocketmq.name-server=" + rocketmqContainer.getServiceHost("namesrv_1", 9876)
                            + ":" +
                            rocketmqContainer.getServicePort("namesrv_1", 9876),
                    "rocketmq.producer.group=rocketmq-producer-group",
                    "rocketmq.consumer.group=rocketmq-consumer-group")
                    .applyTo(configurableApplicationContext.getEnvironment());
        }
    }

}
