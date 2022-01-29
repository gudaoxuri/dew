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

import group.idealworld.dew.core.cluster.spi.redis.*;
import group.idealworld.dew.core.cluster.test.*;
import group.idealworld.dew.test.RedisExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.testcontainers.junit.jupiter.Testcontainers;

/**
 * Cluster test.
 *
 * @author gudaoxuri
 */
@ExtendWith({SpringExtension.class, RedisExtension.class})
@ContextConfiguration(initializers = RedisExtension.Initializer.class)
@SpringBootApplication
@SpringBootTest
@Testcontainers
public class ClusterTest {

    @Autowired
    private RedisClusterCacheWrap redisClusterCacheWrap;
    @Autowired
    private RedisClusterMQ redisClusterMQ;
    @Autowired
    private RedisClusterMapWrap redisClusterMapWrap;
    @Autowired
    private RedisClusterLockWrap redisClusterLockWrap;
    @Autowired
    private RedisClusterElectionWrap redisClusterElectionWrap;

    /**
     * Test mq.
     *
     * @throws InterruptedException the interrupted exception
     */
    @Test
    public void testMQ() throws InterruptedException {
        new ClusterMQTest().test(redisClusterMQ);
    }

    /**
     * Test cache.
     *
     * @throws InterruptedException the interrupted exception
     */
    @Test
    public void testCache() throws InterruptedException {
        new ClusterCacheTest().test(redisClusterCacheWrap.instance(), redisClusterCacheWrap.instance("other"));
    }

    /**
     * Test lock.
     *
     * @throws InterruptedException the interrupted exception
     */
    @Test
    public void testLock() throws InterruptedException {
        new ClusterLockTest().test(redisClusterLockWrap.instance("test"));
    }

    /**
     * Test map.
     *
     * @throws InterruptedException the interrupted exception
     */
    @Test
    public void testMap() throws InterruptedException {
        new ClusterMapTest().test(redisClusterMapWrap.instance("test", ClusterMapTest.TestMapObj.class));
    }

    /**
     * Test election.
     *
     * @throws InterruptedException the interrupted exception
     */
    @Test
    public void testElection() throws InterruptedException {
        new ClusterElectionTest().test(redisClusterElectionWrap.instance("test"));
    }

}
