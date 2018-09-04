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
        new ClusterLockTest().test(redisClusterLockWrap.lock("test"));
    }

    @Test
    public void testMap() throws InterruptedException {
        new ClusterMapTest().test(redisClusterMapWrap.map("test", ClusterMapTest.TestMapObj.class));
    }

    @Test
    public void testElection() throws InterruptedException {
        new ClusterElectionTest().test(redisClusterElectionWrap.election("test"));
    }

}
