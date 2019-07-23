/*
 * Copyright 2019. the original author or authors.
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

package ms.dew.core.cluster;

import ms.dew.core.cluster.spi.rabbit.RabbitClusterMQ;
import ms.dew.core.cluster.test.ClusterMQTest;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Cluster test.
 *
 * @author gudaoxuri
 */
@RunWith(SpringRunner.class)
@SpringBootApplication
@SpringBootTest
@Ignore("Need start rabbit server e.g. docker run -it -p 15672:15672 -p 25672:25672 -p 5671:5671 -p 5672:5672 -p 4369:4369 rabbitmq:management")
public class ClusterTest {

    @Autowired
    private RabbitClusterMQ rabbitClusterMQ;

    /**
     * Test mq.
     *
     * @throws InterruptedException the interrupted exception
     */
    @Test
    public void testMQ() throws InterruptedException {
        new ClusterMQTest().test(rabbitClusterMQ);
    }

}
