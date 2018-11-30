package com.tairanchina.csp.dew.core.cluster;

import com.tairanchina.csp.dew.core.cluster.spi.rabbit.RabbitClusterMQ;
import com.tairanchina.csp.dew.core.cluster.test.ClusterMQTest;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootApplication
@SpringBootTest
@Ignore("Need start rabbit server")
public class ClusterTest {

    @BeforeClass
    public static void init() {

    }

    @Autowired
    private RabbitClusterMQ rabbitClusterMQ;

    @Test
    public void testMQ() throws InterruptedException {
        new ClusterMQTest().test(rabbitClusterMQ);
    }

}
