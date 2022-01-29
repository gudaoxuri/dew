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
import org.testcontainers.containers.DockerComposeContainer;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public class RocketMQExtension implements BeforeAllCallback {

    private static final Logger logger = LoggerFactory.getLogger(RocketMQExtension.class);

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
            "    command: sh -c 'echo \"brokerIP1 = 127.0.0.1\" > ../conf/broker.conf && ./mqbroker -n namesrv:9876 -c ../conf/broker" +
            ".conf'\n" +
            "    depends_on:\n" +
            "      - namesrv";

    private static final Path tempFile;

    static {
        try {
            tempFile = Files.createTempFile(null, null);
            Files.write(tempFile, DOCKER_COMPOSE.getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static DockerComposeContainer rocketmqContainer =
            new DockerComposeContainer(tempFile.toFile())
                    .withExposedService("namesrv_1", 9876);
//                    .withExposedService("broker_1", 10911)
//                    .withExposedService("broker_1", 10909)
//                    .withExposedService("broker_1", 10912);

    @Override
    public void beforeAll(ExtensionContext extensionContext) {
        rocketmqContainer.start();
        String nameSrvUrl = rocketmqContainer.getServiceHost("namesrv_1", 9876)
                + ":" +
                rocketmqContainer.getServicePort("namesrv_1", 9876);
        logger.info("Test RocketMQ name server url: " + nameSrvUrl);
    }

    public static class Initializer
            implements ApplicationContextInitializer<ConfigurableApplicationContext> {
        public void initialize(ConfigurableApplicationContext configurableApplicationContext) {
            TestPropertyValues.of(
                    "rocketmq.name-server=" + rocketmqContainer.getServiceHost("namesrv_1", 9876)
                            + ":" +
                            rocketmqContainer.getServicePort("namesrv_1", 9876),
                    "rocketmq.producer.group=rocketmq-producer-group",
                    "rocketmq.consumer.group=rocketmq-consumer-group"
            ).applyTo(configurableApplicationContext.getEnvironment());
        }
    }

}
