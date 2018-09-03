package com.tairanchina.csp.dew.core.cluster;

import com.tairanchina.csp.dew.core.cluster.spi.redis.RedisClusterMQ;
import com.tairanchina.csp.dew.core.cluster.test.ClusterMQTest;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootApplication
public class ClusterTest {

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Test
    public void testMQ() throws InterruptedException {
        new ClusterMQTest().test(new RedisClusterMQ(redisTemplate));
    }


    @Test
    public void testCache() {

    }

    @Test
    public void testLock() {

    }

    @Test
    public void testMap() {

    }

    @Test
    public void testElection() {

    }

}
