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

package com.tairanchina.csp.dew.core.cluster;

import com.tairanchina.csp.dew.core.cluster.spi.redis.*;
import com.tairanchina.csp.dew.core.cluster.test.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootApplication
@SpringBootTest
public class ClusterTest {

    @Autowired
    private RedisClusterCache redisClusterCache;
    @Autowired
    private RedisClusterMQ redisClusterMQ;
    @Autowired
    private RedisClusterMapWrap redisClusterMapWrap;
    @Autowired
    private RedisClusterLockWrap redisClusterLockWrap;
    @Autowired
    private RedisClusterElectionWrap redisClusterElectionWrap;

    @Test
    public void testMQ() throws InterruptedException {
        new ClusterMQTest().test(redisClusterMQ);
    }

    @Test
    public void testCache() throws InterruptedException {
        new ClusterCacheTest().test(redisClusterCache);
    }

    @Test
    public void testLock() throws InterruptedException {
        new ClusterLockTest().test(redisClusterLockWrap.instance("test"));
    }

    @Test
    public void testMap() throws InterruptedException {
        new ClusterMapTest().test(redisClusterMapWrap.instance("test", ClusterMapTest.TestMapObj.class));
    }

    @Test
    public void testElection() throws InterruptedException {
        new ClusterElectionTest().test(redisClusterElectionWrap.instance("test"));
    }

}
