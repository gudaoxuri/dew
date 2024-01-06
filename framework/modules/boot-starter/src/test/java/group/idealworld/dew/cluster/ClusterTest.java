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

package group.idealworld.dew.cluster;

import group.idealworld.dew.Dew;
import group.idealworld.dew.core.cluster.test.ClusterCacheTest;
import group.idealworld.dew.core.cluster.test.ClusterElectionTest;
import group.idealworld.dew.core.cluster.test.ClusterLockTest;
import group.idealworld.dew.core.cluster.test.ClusterMapTest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Cluster test.
 *
 * @author gudaoxuri
 */
@Component
public class ClusterTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(ClusterTest.class);

    /**
     * Test all.
     *
     * @throws InterruptedException the interrupted exception
     */
    public void testAll() throws InterruptedException {
        LOGGER.info("Testing Cache");
        new ClusterCacheTest().test(Dew.cluster.cache, null);
        LOGGER.info("Testing Lock");
        new ClusterLockTest().test(Dew.cluster.lock.instance("test"));
        LOGGER.info("Testing Map");
        new ClusterMapTest().test(Dew.cluster.map.instance("test", ClusterMapTest.TestMapObj.class));
//        LOGGER.info("Testing MQ");
//        new ClusterMQTest().test(Dew.cluster.mq);
        LOGGER.info("Testing Election");
        new ClusterElectionTest().test(Dew.cluster.election.instance("test"));
    }

}
