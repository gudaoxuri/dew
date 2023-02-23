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

package group.idealworld.dew.core.cluster.test;

import group.idealworld.dew.core.cluster.ClusterLock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CountDownLatch;

/**
 * Cluster lock test.
 *
 * @author gudaoxuri
 */
public class ClusterLockTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(ClusterLockTest.class);

    /**
     * Test.
     *
     * @param lock the lock
     * @throws InterruptedException the interrupted exception
     */
    public void test(ClusterLock lock) throws InterruptedException {
        CountDownLatch waiting = new CountDownLatch(3);
        lock.delete();
        new Thread(() -> {
            try {
                assert lock.tryLock();
                LOGGER.info("Locked1 > " + Thread.currentThread().getId());
                Thread.sleep(2300);
            } catch (Exception e) {
                LOGGER.error(e.getMessage());
            } finally {
                assert lock.unLock();
                LOGGER.info("UnLock1 > " + Thread.currentThread().getId());
                waiting.countDown();
            }
        }).start();
        Thread.sleep(500);
        new Thread(() -> {
            int hitTimes = 0;
            try {
                while (!lock.tryLock()) {
                    LOGGER.info("waiting 1 unlock");
                    hitTimes++;
                    Thread.sleep(100);
                }
                LOGGER.info("Locked2 > " + Thread.currentThread().getId());
            } catch (Exception e) {
                LOGGER.error(e.getMessage());
            } finally {
                assert lock.unLock();
                LOGGER.info("UnLock2 > " + Thread.currentThread().getId());
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
                    LOGGER.info("waiting 2 unlock");
                    hitTimes++;
                }
                LOGGER.info("Locked3 > " + Thread.currentThread().getId());
            } catch (Exception e) {
                LOGGER.error(e.getMessage());
            } finally {
                assert lock.unLock();
                LOGGER.info("UnLock3 > " + Thread.currentThread().getId());
                if (hitTimes != 1) {
                    throw new RuntimeException("Waiting times must equals 1");
                }
                waiting.countDown();
            }
        }).start();

        waiting.await();
        LOGGER.info("start test auto release instance");
        assert !lock.isLocked();
        assert lock.tryLock(0, 300);
        assert lock.isLocked();
        Thread.sleep(2000);
        assert !lock.isLocked();
        LOGGER.info("finish test auto release instance");
    }

}
