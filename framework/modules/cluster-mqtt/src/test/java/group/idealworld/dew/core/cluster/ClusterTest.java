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

package group.idealworld.dew.core.cluster;

import group.idealworld.dew.core.cluster.spi.mqtt.MqttClusterMQ;
import group.idealworld.dew.core.cluster.test.ClusterMQTest;
import group.idealworld.dew.test.MqttExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.concurrent.CountDownLatch;

/**
 * Cluster test.
 *
 * @author gudaoxuri
 */
@ExtendWith({SpringExtension.class, MqttExtension.class})
@ContextConfiguration(initializers = MqttExtension.Initializer.class)
@SpringBootApplication
@SpringBootTest
@Testcontainers
public class ClusterTest {

    private static final Logger logger = LoggerFactory.getLogger(ClusterMQTest.class);

    @Autowired
    private MqttClusterMQ mq;

    /**
     * Test mq.
     *
     * @throws InterruptedException the interrupted exception
     */
    @Test
    public void testMQ() throws InterruptedException {
        CountDownLatch waiting = new CountDownLatch(20);
        new Thread(() -> mq.subscribe("test_pub_sub", message -> {
            assert message.getBody().contains("msg");
            logger.info("subscribe instance 1: pub_sub>>" + message);
            waiting.countDown();
        })).start();
        Thread.sleep(100);
        for (int i = 0; i < 10; i++) {
            mq.publish("test_pub_sub", "msgA" + i);
            mq.publish("test_pub_sub", "msgB" + i);
        }
        waiting.await();
    }

}
