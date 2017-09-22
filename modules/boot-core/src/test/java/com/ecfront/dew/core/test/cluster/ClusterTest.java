package com.ecfront.dew.core.test.cluster;


import com.ecfront.dew.common.$;
import com.ecfront.dew.core.Dew;
import com.ecfront.dew.core.cluster.ClusterDistLock;
import com.ecfront.dew.core.cluster.ClusterDistMap;
import com.ecfront.dew.core.cluster.VoidProcessFun;
import com.ecfront.dew.core.cluster.spi.rabbit.RabbitClusterMQ;
import com.ecfront.dew.core.test.cluster.entity.TestMapObj;
import org.junit.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;

@Component
public class ClusterTest {

    private Logger logger = LoggerFactory.getLogger(ClusterTest.class);

    public void testAll() throws Exception {
        // Cache Test
        testCache();
        // Dist Test
        testDistMapExp();
        testDistLockWithFun();
        testDistLock();
        testDifferentTreadLock();
        testUnLock();
        testConnection();
        testDistMap();
        // MQ Test
        testMQReq();
        testMQTopic();
    }

    /**
     * redis测试通过
     *
     * @throws InterruptedException
     */
    private void testCache() throws InterruptedException {
        Assert.assertTrue(true);
        Dew.cluster.cache.flushdb();
        Dew.cluster.cache.set("n_test", "{\"name\":\"jzy\"}", 1);
        Assert.assertTrue(Dew.cluster.cache.exists("n_test"));
        Dew.cluster.cache.del("n_test");
        Assert.assertTrue(!Dew.cluster.cache.exists("n_test"));
        Dew.cluster.cache.set("n_test", "{\"name\":\"jzy\"}", 1);
        Assert.assertTrue(Dew.cluster.cache.exists("n_test"));
        Assert.assertEquals("jzy", $.json.toJson(Dew.cluster.cache.get("n_test")).get("name").asText());
        Thread.sleep(1000);
        Assert.assertTrue(!Dew.cluster.cache.exists("n_test"));
        Assert.assertEquals(null, Dew.cluster.cache.get("n_test"));

        Dew.cluster.cache.del("hash_test");
        Dew.cluster.cache.hmset("hash_test", new HashMap<String, String>() {{
            put("f1", "v1");
            put("f2", "v2");
        }});
        Dew.cluster.cache.hset("hash_test", "f3", "v3");
        Assert.assertEquals("v3", Dew.cluster.cache.hget("hash_test", "f3"));
        Assert.assertEquals("v2", Dew.cluster.cache.hget("hash_test", "f2"));
        Assert.assertEquals(null, Dew.cluster.cache.hget("hash_test", "notexist"));
        Assert.assertTrue(Dew.cluster.cache.hexists("hash_test", "f3"));
        Map<String, String> hashVals = Dew.cluster.cache.hgetAll("hash_test");
        Assert.assertTrue(hashVals.size() == 3
                && hashVals.get("f1").equals("v1")
                && hashVals.get("f2").equals("v2")
                && hashVals.get("f3").equals("v3"));
        Dew.cluster.cache.hdel("hash_test", "f3");
        Assert.assertTrue(!Dew.cluster.cache.hexists("hash_test", "f3"));
        Dew.cluster.cache.del("hash_test");
        Assert.assertTrue(!Dew.cluster.cache.exists("hash_test"));

        Dew.cluster.cache.del("list_test");
        Dew.cluster.cache.lmset("list_test", new ArrayList<String>() {{
            add("v1");
            add("v2");
        }});
        Dew.cluster.cache.lpush("list_test", "v0");
        Assert.assertEquals(3, Dew.cluster.cache.llen("list_test"));
        Assert.assertEquals("v0", Dew.cluster.cache.lpop("list_test"));
        Assert.assertEquals(2, Dew.cluster.cache.llen("list_test"));
        List<String> listVals = Dew.cluster.cache.lget("list_test");
        Assert.assertTrue(listVals.size() == 2 && listVals.stream().findAny().get().equals("v2"));

        Dew.cluster.cache.del("int_test");
        Assert.assertEquals(0, Dew.cluster.cache.incrBy("int_test", 0));
        Dew.cluster.cache.set("int_test", "10");
        Assert.assertEquals("10", Dew.cluster.cache.get("int_test"));
        Dew.cluster.cache.incrBy("int_test", 10);
        Assert.assertEquals("20", Dew.cluster.cache.get("int_test"));
        Dew.cluster.cache.incrBy("int_test", 0);
        Assert.assertEquals("20", Dew.cluster.cache.get("int_test"));
        Dew.cluster.cache.incrBy("int_test", 10);
        Assert.assertEquals("30", Dew.cluster.cache.get("int_test"));
        Dew.cluster.cache.decrBy("int_test", 4);
        Dew.cluster.cache.decrBy("int_test", 2);
        Assert.assertEquals("24", Dew.cluster.cache.get("int_test"));
        Dew.cluster.cache.expire("int_test", 1);
        Assert.assertEquals("24", Dew.cluster.cache.get("int_test"));
        Thread.sleep(1100);
        Assert.assertEquals(null, Dew.cluster.cache.get("int_test"));
    }


    /**
     * redis测试通过
     *
     * @throws InterruptedException
     */
    private void testDistMap() throws InterruptedException {
        // map
        ClusterDistMap<TestMapObj> mapObj = Dew.cluster.dist.map("test_obj_map", TestMapObj.class);
        mapObj.regEntryAddedEvent(entryEvent ->
                logger.info("Event : Add key:" + entryEvent.getKey() + ",value:" + entryEvent.getValue().getA()));
        mapObj.regEntryRemovedEvent(entryEvent ->
                logger.info("Event : Remove key:" + entryEvent.getKey() + ",value:null,old value:" + entryEvent.getOldValue().getA()));
        mapObj.regEntryUpdatedEvent(entryEvent ->
                logger.info("Event : Update key:" + entryEvent.getKey() + ",value:" + entryEvent.getValue().getA() + ",old value:" + entryEvent.getOldValue().getA()));
        mapObj.regMapClearedEvent(() -> logger.info("Event : Clear"));
        mapObj.clear();
        TestMapObj obj = new TestMapObj();
        obj.setA("测试");
        mapObj.put("a", obj);
        mapObj.put("b", obj);
        obj.setA("测试2");
        mapObj.put("b", obj);
        mapObj.remove("b");
        Assert.assertEquals(mapObj.get("a").getA(), "测试");
        mapObj.clear();
        ClusterDistMap<Long> map = Dew.cluster.dist.map("test_map", Long.class);
        map.clear();
        Map<String, Long> checkMap = new ConcurrentHashMap<>();
        CountDownLatch cdl = new CountDownLatch(1);
        Dew.Timer.periodic(1, () -> {
            long t = System.currentTimeMillis();
            map.put("a" + t, t);
            checkMap.put("a" + t, t);
        });
        Dew.Timer.periodic(5, () -> {
            Map<String, Long> m = map.getAll();
            Assert.assertEquals(checkMap.size(), m.size());
            Assert.assertFalse(checkMap.entrySet().stream().anyMatch(entry -> !m.get(entry.getKey()).equals(entry.getValue())));
            cdl.countDown();
        });
        cdl.await();
    }

    /**
     * 另起线程测试
     *
     * @throws InterruptedException
     */
    private void testDistMapExp() throws InterruptedException {
        ClusterDistMap<TestMapObj> mapObj = Dew.cluster.dist.map("test_obj_map_exp", TestMapObj.class);
        TestMapObj obj = new TestMapObj();
        obj.setA("测试");
        mapObj.putAsync("a", obj);
        mapObj.putAsync("b", obj);
        Thread.sleep(100);
        obj.setA("测试2");
        mapObj.putAsync("b", obj);
        Thread.sleep(100);
        Assert.assertTrue(mapObj.containsKey("b"));
        Assert.assertEquals("测试2", mapObj.get("b").getA());
        mapObj.removeAsync("b");
        Thread.sleep(100);
        Assert.assertFalse(mapObj.containsKey("b"));
    }

    /**
     * redis测试通过
     *
     * @throws InterruptedException
     */
    private void testDistLock() throws InterruptedException {
        //lock
        ClusterDistLock lock = Dew.cluster.dist.lock("test_lock");
        lock.delete();
        Thread t1 = new Thread(() -> {
            lock.lock();
            logger.info("Lock1 > " + Thread.currentThread().getId());
            try {
                Thread.sleep(500);
            } catch (Exception e) {
                logger.error(e.getMessage());
                Assert.assertTrue(false);
            } finally {
                logger.info("UnLock1 > " + Thread.currentThread().getId());
                lock.unLock();
            }
        });
        t1.start();
        t1.join();
        Thread t2 = new Thread(() -> {
            ClusterDistLock lockLocal = Dew.cluster.dist.lock("test_lock");
            try {
                Assert.assertTrue(lockLocal.tryLock());
                logger.info("Lock2 > " + Thread.currentThread().getId());
                Thread.sleep(2000);
            } catch (Exception e) {
                logger.error(e.getMessage());
                Assert.assertTrue(false);
            } finally {
                lockLocal.unLock();
                logger.info("UnLock2 > " + Thread.currentThread().getId());
            }
        });
        t2.start();
        Thread.sleep(100);
        Thread t3 = new Thread(() -> {
            ClusterDistLock lockLocal = Dew.cluster.dist.lock("test_lock");
            int hitTimes = 0;
            try {
                while (!lockLocal.tryLock()) {
                    logger.info("waiting 1 unlock");
                    hitTimes++;
                    Thread.sleep(100);
                }
                logger.info("Lock3 > " + Thread.currentThread().getId());
            } catch (Exception e) {
                logger.error(e.getMessage());
                Assert.assertTrue(false);
            } finally {
                logger.info("UnLock3 > " + Thread.currentThread().getId());
                lockLocal.unLock();
                if (hitTimes < 15) {
                    Assert.assertTrue(false);
                }
            }
        });
        t3.start();
        Thread t4 = new Thread(() -> {
            ClusterDistLock lockLocal = Dew.cluster.dist.lock("test_lock");
            long start = 1;
            int hitTimes = 0;
            try {
                while (!lockLocal.tryLock(1000, 2000)) {
                    logger.info("waiting 2 unlock");
                    hitTimes++;
                }
                start = System.currentTimeMillis();
                logger.info("Lock4 > " + System.currentTimeMillis() + "********");
                logger.info("Lock4 > " + Thread.currentThread().getId());
            } catch (Exception e) {
                logger.error(e.getMessage());
                Assert.assertTrue(false);
            } finally {
                logger.info("UnLock4 > " + System.currentTimeMillis() + "********");
                logger.info("Lock4持续时间" + (System.currentTimeMillis() - start));
                lockLocal.unLock();
                if (hitTimes != 1) {
                    Assert.assertTrue(false);
                }
            }
        });
        t4.start();
        t2.join();
        t3.join();
        t4.join();
    }

    private void testDistLockWithFun() throws Exception {

        ClusterDistLock clusterDistLock = Dew.cluster.dist.lock("test_lock_fun");
//        clusterDistLock.delete();
        boolean flag = clusterDistLock.tryLock();
        Assert.assertTrue(flag);
        boolean flag2 = clusterDistLock.tryLock();
        // 可重入
        Assert.assertTrue(flag2);
        new Thread(() -> {
            Assert.assertFalse(clusterDistLock.tryLock());
            Assert.assertFalse(clusterDistLock.unLock());
        }).start();
        VoidProcessFun voidProcessFun = () -> {
            try {
                Thread.sleep(200);
                Assert.assertTrue(clusterDistLock.tryLock());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        };
        clusterDistLock.lockWithFun(voidProcessFun);
        Assert.assertTrue(clusterDistLock.tryLock());
        clusterDistLock.unLock();
        clusterDistLock.tryLockWithFun(voidProcessFun);
        clusterDistLock.tryLockWithFun(100, 1000, voidProcessFun);
        clusterDistLock.tryLockWithFun(800, voidProcessFun);
    }

    /**
     * redis测试通过
     * rabbit测试通过
     *
     * @throws InterruptedException
     */
    private void testMQTopic() throws InterruptedException {
        CountDownLatch countDownLatch = new CountDownLatch(2);
        // pub-sub
        new Thread(() -> {
            Dew.cluster.mq.subscribe("test_pub_sub", message -> {
                Assert.assertTrue(message.contains("msg"));
                logger.info("1 pub_sub>>" + message);
            });
            logger.info("另起线程");
            countDownLatch.countDown();
        }).start();
        new Thread(() -> {
            Dew.cluster.mq.subscribe("test_pub_sub", message -> {
                Assert.assertTrue(message.contains("msg"));
                logger.info("2 pub_sub>>" + message);
            });
            countDownLatch.countDown();
        }).start();
        Thread.sleep(2000);
        logger.info("count   " + countDownLatch.getCount());
        countDownLatch.await();
        logger.info("测试1     " + Thread.activeCount());
        Thread.sleep(3000);
        logger.info("测试2     " + Thread.activeCount());
        for (int i = 0; i < 10; i++) {
            logger.info(Thread.activeCount() + "");
            logger.info("单位开始");
            Thread.sleep(1000);
            Dew.cluster.mq.publish("test_pub_sub", "msgA");
            Dew.cluster.mq.publish("test_pub_sub", "msgB");
            Thread.sleep(1000);
            logger.info("单位结束");
        }
    }

    /**
     * redis测试通过
     * rabbit测试通过
     *
     * @throws InterruptedException
     */
    private void testMQReq() throws InterruptedException {
        // req-resp
        List<String> conflictFlag = new ArrayList<>();
        new Thread(() -> {
            Dew.cluster.mq.response("test_rep_resp", message -> {
                if (conflictFlag.contains(message)) {
                    Assert.assertTrue(1 == 2);
                } else {
                    conflictFlag.add(message);
                    logger.info("1 req_resp>>" + message);
                }
            });
            try {
                new CountDownLatch(1).await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
        new Thread(() -> {
            Dew.cluster.mq.response("test_rep_resp", message -> {
                if (conflictFlag.contains(message)) {
                    Assert.assertTrue(1 == 2);
                } else {
                    conflictFlag.add(message);
                    logger.info("2 req_resp>>" + message);
                }
            });
            try {
                new CountDownLatch(1).await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
        new Thread(() -> {
            Dew.cluster.mq.response("test_rep_resp/a", message -> {
                Assert.assertTrue(1 == 2);
            });
            try {
                new CountDownLatch(1).await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
        Thread.sleep(1000);
        Dew.cluster.mq.request("test_rep_resp", "msg1");
        Dew.cluster.mq.request("test_rep_resp", "msg2");
        Thread.sleep(1000);
        // rabbit confirm
        if (Dew.cluster.mq instanceof RabbitClusterMQ) {
            boolean success = ((RabbitClusterMQ) Dew.cluster.mq).publish("test_pub_sub", "confirm message", true);
            Assert.assertTrue(success);
            success = ((RabbitClusterMQ) Dew.cluster.mq).request("test_rep_resp", "confirm message", true);
            Assert.assertTrue(success);
        }

    }

    /**
     * 测试不同线程能否锁住
     *
     * @throws InterruptedException
     */
    private void testDifferentTreadLock() throws InterruptedException {
        ClusterDistLock lock = Dew.cluster.dist.lock("test_lock_B");
        Boolean temp = lock.tryLock(0, 100000);
        logger.info("*********" + temp);
        Assert.assertTrue(temp);
        Thread thread = new Thread(() -> {
            try {
                ClusterDistLock lockChild = Dew.cluster.dist.lock("test_lock_B");
                Boolean tempTest = lockChild.tryLock(0, 100000);
                logger.info("*********" + tempTest);
                Assert.assertFalse(tempTest);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        thread.start();
        thread.join();
    }

    /**
     * testDiffentJVMLockA
     * testDiffentJVMLockB
     * 模拟两个虚拟机。
     * 先调用testDiffentJVMLockA锁住，等方法执行结束。
     * 再执行testDiffentJVMLockB，再去获得锁，正常结果第二次调用不能获取锁
     * Tip: 不能用内嵌redis服务测试
     * @throws InterruptedException
     */
    /*@Test
    public void testDiffentJVMLockA() throws InterruptedException {
        ClusterDistLock lock = Dew.cluster.dist.lock("test_lock_C");
        Boolean temp = lock.tryLock(0, 200000);
        Assert.assertTrue(temp);
    }

    @Test
    public void testDiffentJVMLockB() throws InterruptedException {
        ClusterDistLock lock = Dew.cluster.dist.lock("test_lock_C");
        Boolean temp = lock.tryLock(0, 200000);
        Assert.assertFalse(temp);
    }*/


    /**
     * 测试释放锁
     *
     * @throws InterruptedException
     */
    private void testUnLock() throws InterruptedException {
        ClusterDistLock lock = Dew.cluster.dist.lock("test_lock_D");
        //测试还没有加锁前去解锁
        Boolean temp = lock.unLock();
        Assert.assertFalse(temp);
        //加锁
        temp = lock.tryLock(0, 2000);
        Assert.assertTrue(temp);
        Thread thread = new Thread(() -> {
            ClusterDistLock lockChild = Dew.cluster.dist.lock("test_lock_D");
            //测试不同的线程去解锁
            Boolean tempTest = lockChild.unLock();
            Assert.assertFalse(tempTest);
        });
        thread.start();
        thread.join();
        //测试同一个线程去解锁
        temp = lock.unLock();
        Assert.assertTrue(temp);
    }

    /**
     * 测试连接是否被关闭，连接池默认设置最大连接数1，设置两次值
     *
     * @throws InterruptedException
     */
    private void testConnection() throws InterruptedException {
        ClusterDistLock lock = Dew.cluster.dist.lock("test_lock_DA");
        Boolean temp = lock.tryLock(0, 1000);
        Assert.assertTrue(temp);
        lock = Dew.cluster.dist.lock("test_lock_DE");
        temp = lock.tryLock(0, 1000);
        Assert.assertTrue(temp);
    }

}

