package com.trc.test.cluster;


import com.ecfront.dew.common.$;
import com.tairanchina.csp.dew.Dew;
import com.tairanchina.csp.dew.core.cluster.ClusterDistLock;
import com.tairanchina.csp.dew.core.cluster.ClusterDistMap;
import com.tairanchina.csp.dew.core.cluster.VoidProcessFun;
import com.tairanchina.csp.dew.core.cluster.spi.rabbit.RabbitClusterMQ;
import com.trc.test.cluster.entity.TestMapObj;
import org.junit.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;

@Component
public class TestCluster {

    private Logger logger = LoggerFactory.getLogger(TestCluster.class);

    private String valueTest = "value_test";

    private String hashTest = "hash_test";

    private String listTest = "list_test";

    private String intTest = "int_test";

    private String hashIntTest = "hash_int_test";

    private String setTest = "set_test";

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
        testMQTopic();
        testMQReq();
    }

    /**
     * redis测试通过
     *
     * @throws InterruptedException
     */
    private void testCache() throws InterruptedException {
        Assert.assertTrue(true);
        Dew.cluster.cache.flushdb();
        Dew.cluster.cache.setex(valueTest, "{\"name\":\"jzy\"}", 1L);
        Assert.assertTrue(Dew.cluster.cache.exists(valueTest));
        Dew.cluster.cache.del(valueTest);
        Assert.assertTrue(!Dew.cluster.cache.exists(valueTest));
        Dew.cluster.cache.setex(valueTest, "{\"name\":\"jzy\"}", 2L);
        Assert.assertTrue(Dew.cluster.cache.exists(valueTest));
        Assert.assertEquals("jzy", $.json.toJson(Dew.cluster.cache.get(valueTest)).get("name").asText());
        Thread.sleep(1000);
        long timeLeft = Dew.cluster.cache.ttl(valueTest);
        Assert.assertTrue(timeLeft == 1); // 返回剩余时间
        Thread.sleep(1500);
        Assert.assertTrue(!Dew.cluster.cache.exists(valueTest));
        Dew.cluster.cache.setex(valueTest, "{\"name\":\"jzy\"}", 1L);
        Assert.assertTrue(Dew.cluster.cache.exists(valueTest));
        Assert.assertTrue(!Dew.cluster.cache.setnx(valueTest, "存入失败", 1L));
        String val = Dew.cluster.cache.getSet(valueTest, "{\"name\":\"dzf\"}");
        Assert.assertEquals("jzy", $.json.toJson(val).get("name").asText());
        Thread.sleep(1000);
        Assert.assertTrue(Dew.cluster.cache.exists(valueTest));
        Dew.cluster.cache.expire(valueTest, 1L);
        Thread.sleep(1500);
        Assert.assertTrue(!Dew.cluster.cache.exists(valueTest));
        Dew.cluster.cache.del(hashTest);
        Dew.cluster.cache.hmset(hashTest, new HashMap<String, String>() {{
            put("f1", "v1");
            put("f2", "v2");
        }});
        Set<String> set = Dew.cluster.cache.hkeys(hashTest);
        Assert.assertEquals(2, set.size());
        Assert.assertTrue(set.containsAll(new ArrayList<String>() {{
            add("f1");
            add("f2");
        }}));
        Assert.assertTrue(Dew.cluster.cache.hvalues(hashTest).contains("v1"));
        Dew.cluster.cache.hset(hashTest, "f3", "v3");
        Assert.assertEquals("v3", Dew.cluster.cache.hget(hashTest, "f3"));
        Assert.assertEquals("v2", Dew.cluster.cache.hget(hashTest, "f2"));
        Assert.assertEquals(null, Dew.cluster.cache.hget(hashTest, "notexist"));
        Assert.assertTrue(Dew.cluster.cache.hexists(hashTest, "f3"));
        Assert.assertEquals(3, Dew.cluster.cache.hlen(hashTest));
        Map<String, String> hashVals = Dew.cluster.cache.hgetAll(hashTest);
        Assert.assertTrue(hashVals.size() == 3
                && hashVals.get("f1").equals("v1")
                && hashVals.get("f2").equals("v2")
                && hashVals.get("f3").equals("v3"));
        Dew.cluster.cache.hdel(hashTest, "f3");
        Assert.assertTrue(!Dew.cluster.cache.hexists(hashTest, "f3"));
        Dew.cluster.cache.del(hashTest);
        Assert.assertTrue(!Dew.cluster.cache.exists(hashTest));

        Dew.cluster.cache.del(listTest);
        Dew.cluster.cache.lmset(listTest, new ArrayList<String>() {{
            add("v1");
            add("v2");
        }});
        Dew.cluster.cache.lpush(listTest, "v0");
        Assert.assertEquals(3, Dew.cluster.cache.llen(listTest));
        Assert.assertEquals("v0", Dew.cluster.cache.lpop(listTest));
        Assert.assertEquals(2, Dew.cluster.cache.llen(listTest));
        List<String> listVals = Dew.cluster.cache.lget(listTest);
        Assert.assertTrue(listVals.size() == 2 && listVals.stream().findAny().get().equals("v2"));

        Dew.cluster.cache.del(intTest);
        Assert.assertEquals(0, Dew.cluster.cache.incrBy(intTest, 0));
        Dew.cluster.cache.set(intTest, "10");
        Assert.assertEquals("10", Dew.cluster.cache.get(intTest));
        Dew.cluster.cache.incrBy(intTest, 10);
        Assert.assertEquals("20", Dew.cluster.cache.get(intTest));
        Dew.cluster.cache.incrBy(intTest, 0);
        Assert.assertEquals("20", Dew.cluster.cache.get(intTest));
        Dew.cluster.cache.incrBy(intTest, 10);
        Assert.assertEquals("30", Dew.cluster.cache.get(intTest));
        Dew.cluster.cache.decrBy(intTest, 4);
        Dew.cluster.cache.decrBy(intTest, 2);
        Assert.assertEquals("24", Dew.cluster.cache.get(intTest));
        Dew.cluster.cache.expire(intTest, 1);
        Assert.assertEquals("24", Dew.cluster.cache.get(intTest));
        Thread.sleep(1000);
        //Assert.assertEquals(null, Dew.cluster.cache.get(intTest));
        Dew.cluster.cache.del(setTest);
        Dew.cluster.cache.smset(setTest, new ArrayList<String>() {{
            add("v1");
            add("v2");
        }});
        Dew.cluster.cache.sset(setTest, "v3");
        Assert.assertEquals(3, Dew.cluster.cache.slen(setTest));
        String popValue = Dew.cluster.cache.spop(setTest);
        Set<String> values = Dew.cluster.cache.sget(setTest);
        Assert.assertEquals(2, values.size());
        Assert.assertTrue(!values.contains(popValue));
        List<String> list = new ArrayList<String>() {{
            addAll(values);
        }};
        Assert.assertEquals(2, Dew.cluster.cache.sdel(setTest, list.get(0), list.get(1)));
        Assert.assertTrue(!Dew.cluster.cache.exists(setTest));

        //test bit
        boolean b = Dew.cluster.cache.getBit("ss", 100);
        Assert.assertTrue(!b);
        b = Dew.cluster.cache.setBit("ss", 100, true);
        Assert.assertTrue(!b);
        b = Dew.cluster.cache.setBit("ss", 100, false);
        Assert.assertTrue(b);

        //test hash incr
        Assert.assertEquals(Dew.cluster.cache.hashIncrBy(hashIntTest, "key1", 1), 1);
        Assert.assertEquals(Dew.cluster.cache.hashDecrBy(hashIntTest, "key2", 1), -1);
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
        Thread.sleep(500);
        logger.info("count   " + countDownLatch.getCount());
        countDownLatch.await();
        logger.info("测试1     " + Thread.activeCount());
        Thread.sleep(500);
        logger.info("测试2     " + Thread.activeCount());
        for (int i = 0; i < 10; i++) {
            logger.info(Thread.activeCount() + "");
            logger.info("单位开始");
            Thread.sleep(500);
            Dew.cluster.mq.publish("test_pub_sub", "msgA");
            Dew.cluster.mq.publish("test_pub_sub", "msgB");
            Thread.sleep(500);
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
