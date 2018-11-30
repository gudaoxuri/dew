package com.tairanchina.csp.dew.core.cluster;

import com.tairanchina.csp.dew.core.cluster.spi.eureka.EurekaClusterElectionWrap;
import com.tairanchina.csp.dew.core.cluster.test.ClusterElectionTest;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.concurrent.CountDownLatch;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = MockEurekaApplication.class, webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@Ignore
public class ClusterTest {

    @Autowired
    private EurekaClusterElectionWrap eurekaClusterElectionWrap;

    @Test
    public void testElection() throws InterruptedException {
        CountDownLatch waiting = new CountDownLatch(1);
        new Thread(() -> {
            try {
                new ClusterElectionTest().test(eurekaClusterElectionWrap.instance());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            waiting.countDown();
        }).start();
        waiting.await();
    }

}
