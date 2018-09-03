package com.tairanchina.csp.dew.core.cluster.test;

import com.tairanchina.csp.dew.core.cluster.Cluster;
import com.tairanchina.csp.dew.core.cluster.ClusterMQ;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

public class ClusterMQTest {

    private static final Logger logger = LoggerFactory.getLogger(ClusterMQ.class);

    public void test(ClusterMQ mq) throws InterruptedException {
        testPubSub(mq);
        testReqResp(mq);
        testHA(mq);
    }

    private void testPubSub(ClusterMQ mq) throws InterruptedException {
        CountDownLatch waiting = new CountDownLatch(40);
        new Thread(() -> mq.subscribe("test_pub_sub", message -> {
            assert message.contains("msg");
            logger.info("subscribe instance 1: pub_sub>>" + message);
            waiting.countDown();
        })).start();
        new Thread(() -> mq.subscribe("test_pub_sub", message -> {
            assert message.contains("msg");
            logger.info("subscribe instance 2: pub_sub>>" + message);
            waiting.countDown();
        })).start();
        Thread.sleep(1000);
        for (int i = 0; i < 10; i++) {
            mq.publish("test_pub_sub", "msgA" + i);
            mq.publish("test_pub_sub", "msgB" + i);
        }
        waiting.await();
    }

    private void testReqResp(ClusterMQ mq) throws InterruptedException {
        CountDownLatch waiting = new CountDownLatch(20);
        List<String> conflictFlag = new ArrayList<>();
        new Thread(() -> mq.response("test_rep_resp", message -> {
            if (conflictFlag.contains(message)) {
                assert 1 == 2;
            } else {
                conflictFlag.add(message);
                logger.info("response instance 1: req_resp>>" + message);
                waiting.countDown();
            }
        })).start();
        new Thread(() -> mq.response("test_rep_resp", message -> {
            if (conflictFlag.contains(message)) {
                assert 1 == 2;
            } else {
                conflictFlag.add(message);
                logger.info("response instance 2: req_resp>>" + message);
                waiting.countDown();
            }
        })).start();
        Thread.sleep(1000);
        for (int i = 0; i < 10; i++) {
            mq.request("test_rep_resp", "msgA" + i);
            mq.request("test_rep_resp", "msgB" + i);
        }
        waiting.await();
    }

    private void testHA(ClusterMQ mq) throws InterruptedException {
        Cluster.ha();
        CountDownLatch waitingOccurError = new CountDownLatch(1);
       Thread mockErrorThread= new Thread(() -> mq.subscribe("test_ha", message -> {
            logger.info("subscribe instance: pub_sub_ha>>" + message);
            waitingOccurError.countDown();
            if (waitingOccurError.getCount() == 0) {
                throw new RuntimeException("Mock Some Error");
            }
        }));
        mockErrorThread.start();
        Thread.sleep(1000);
        mq.publish("test_ha", "ha_msgA");
        waitingOccurError.await();
        mockErrorThread.stop();
        // restart subscribe
        CountDownLatch waiting = new CountDownLatch(2);
        new Thread(() -> {
            mq.subscribe("test_ha", message -> {
                logger.info("subscribe new instance: pub_sub_ha>>" + message);
                waiting.countDown();
            });
        }).start();
        Thread.sleep(1000);
        mq.publish("test_ha", "ha_msgB");
        waiting.await();
    }

}
