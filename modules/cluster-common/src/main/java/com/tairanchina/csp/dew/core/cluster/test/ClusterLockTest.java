package com.tairanchina.csp.dew.core.cluster.test;

import com.tairanchina.csp.dew.core.cluster.ClusterDistLock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CountDownLatch;

public class ClusterLockTest {

    private static final Logger logger = LoggerFactory.getLogger(ClusterLockTest.class);

    public void test(ClusterDistLock lock) throws InterruptedException {
        CountDownLatch waiting = new CountDownLatch(3);
        lock.delete();
        new Thread(() -> {
            try {
                assert lock.tryLock();
                logger.info("Locked1 > " + Thread.currentThread().getId());
                Thread.sleep(2000);
            } catch (Exception e) {
                logger.error(e.getMessage());
            } finally {
                assert lock.unLock();
                logger.info("UnLock1 > " + Thread.currentThread().getId());
                waiting.countDown();
            }
        }).start();
        Thread.sleep(500);
        new Thread(() -> {
            int hitTimes = 0;
            try {
                while (!lock.tryLock()) {
                    logger.info("waiting 1 unlock");
                    hitTimes++;
                    Thread.sleep(100);
                }
                logger.info("Locked2 > " + Thread.currentThread().getId());
            } catch (Exception e) {
                logger.error(e.getMessage());
            } finally {
                assert lock.unLock();
                logger.info("UnLock2 > " + Thread.currentThread().getId());
                if (hitTimes < 15) {
                    throw new RuntimeException("Waiting times are less than 15");
                }
                waiting.countDown();
            }
        }).start();
        new Thread(() -> {
            int hitTimes = 0;
            try {
                while (!lock.tryLock(1000, 2000)) {
                    logger.info("waiting 2 unlock");
                    hitTimes++;
                }
                logger.info("Locked3 > " + Thread.currentThread().getId());
            } catch (Exception e) {
                logger.error(e.getMessage());
            } finally {
                assert lock.unLock();
                logger.info("UnLock3 > " + Thread.currentThread().getId());
                if (hitTimes != 1) {
                    throw new RuntimeException("Waiting times must equals 1");
                }
                waiting.countDown();
            }
        }).start();

        waiting.await();
        // test auto release lock
        assert lock.tryLock(0, 500);
        assert lock.isLocked();
        Thread.sleep(510);
        assert !lock.isLocked();
    }

}
