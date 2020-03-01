/*
 * Copyright 2020. the original author or authors.
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

import group.idealworld.dew.core.cluster.test.ClusterMQTest;
import group.idealworld.dew.core.cluster.spi.mqtt.MqttClusterMQ;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashMap;
import java.util.concurrent.CountDownLatch;

/**
 * Cluster test.
 *
 * @author gudaoxuri
 */
@RunWith(SpringRunner.class)
@SpringBootApplication
@SpringBootTest
@Ignore("Need start mqtt server, e.g. docker run -it -p 1883:1883 -p 9001:9001 eclipse-mosquitto")
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
        for (int i = 0; i < 10; i++) {
            mq.publish("test_pub_sub", "msgA" + i, new HashMap<String, Object>() {
                {
                    put("h", "001");
                }
            });
            mq.publish("test_pub_sub", "msgB" + i);
        }
    }

    /**
     * Start sub 1.
     *
     * @throws InterruptedException the interrupted exception
     */
    @Test
    public void startSub1() throws InterruptedException {
        CountDownLatch waiting = new CountDownLatch(20);
        new Thread(() -> mq.subscribe("test_pub_sub", message -> {
            assert message.getBody().contains("msg");
            if (message.getBody().contains("msgA") && mq.supportHeader()) {
                assert message.getHeader().get().containsKey("h");
            }
            logger.info("subscribe instance 1: pub_sub>>" + message);
            waiting.countDown();
        })).start();
        waiting.await();
    }

    /**
     * Start sub 2.
     *
     * @throws InterruptedException the interrupted exception
     */
    @Test
    public void startSub2() throws InterruptedException {
        CountDownLatch waiting = new CountDownLatch(20);
        new Thread(() -> mq.subscribe("test_pub_sub", message -> {
            assert message.getBody().contains("msg");
            if (message.getBody().contains("msgA") && mq.supportHeader()) {
                assert message.getHeader().get().containsKey("h");
            }
            logger.info("subscribe instance 2: pub_sub>>" + message);
            waiting.countDown();
        })).start();
        waiting.await();
    }

}
