package com.ecfront.dew.core;

import com.ecfront.dew.common.JsonHelper;
import com.ecfront.dew.core.cluster.ClusterDistLock;
import com.ecfront.dew.core.cluster.ClusterDistMap;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

@RunWith(SpringRunner.class)
@SpringBootApplication
@SpringBootTest(classes = DewApplication.class)
@ComponentScan(basePackageClasses = {ClusterTest.class, Dew.class})
public class ClusterTest {

    @Test
    public void testCache() throws InterruptedException {
        Dew.cluster.cache.flushdb();
        Dew.cluster.cache.del("n_test");
        Assert.assertTrue(!Dew.cluster.cache.exists("n_test"));
        Dew.cluster.cache.set("n_test", "{\"name\":\"jzy\"}", 1);
        Assert.assertTrue(Dew.cluster.cache.exists("n_test"));
        Assert.assertEquals("jzy", JsonHelper.toJson(Dew.cluster.cache.get("n_test")).get("name").asText());
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
        Dew.cluster.cache.incrBy("int_test", 10);
        Assert.assertEquals("10", Dew.cluster.cache.get("int_test"));
        Dew.cluster.cache.incrBy("int_test", 0);
        Assert.assertEquals("10", Dew.cluster.cache.get("int_test"));
        Dew.cluster.cache.incrBy("int_test", 10);
        Assert.assertEquals("20", Dew.cluster.cache.get("int_test"));
        Dew.cluster.cache.decrBy("int_test", 4);
        Dew.cluster.cache.decrBy("int_test", 2);
        Assert.assertEquals("14", Dew.cluster.cache.get("int_test"));
        Dew.cluster.cache.expire("int_test", 1);
        Assert.assertEquals("14", Dew.cluster.cache.get("int_test"));
        Thread.sleep(1100);
        Assert.assertEquals(null, Dew.cluster.cache.get("int_test"));
    }

    @Test
    public void testDist() throws InterruptedException {
        // map
        ClusterDistMap<TestMapObj> mapObj = Dew.cluster.dist.map("test_obj_map", TestMapObj.class);
        mapObj.regEntryAddedEvent(entryEvent ->
                System.out.println("Event : Add key:" + entryEvent.getKey() + ",value:" + entryEvent.getValue().a));
        mapObj.regEntryRemovedEvent(entryEvent ->
                System.out.println("Event : Remove key:" + entryEvent.getKey() + ",value:null,old value:" + entryEvent.getOldValue().a));
        mapObj.regEntryUpdatedEvent(entryEvent ->
                System.out.println("Event : Update key:" + entryEvent.getKey() + ",value:" + entryEvent.getValue().a + ",old value:" + entryEvent.getOldValue().a));
        mapObj.regMapClearedEvent(() -> System.out.println("Event : Clear"));

        mapObj.clear();
        TestMapObj obj = new TestMapObj();
        obj.a = "测试";
        mapObj.put("a", obj);
        mapObj.put("b", obj);
        obj.a = "测试2";
        mapObj.put("b", obj);
        mapObj.remove("b");
        Assert.assertEquals(mapObj.get("a").a, "测试");
        mapObj.clear();

        ClusterDistMap<Long> map = Dew.cluster.dist.map("test_map", Long.class);
        map.clear();
        Dew.Timer.periodic(1, () -> map.put("a" + System.currentTimeMillis(), System.currentTimeMillis()));
        Dew.Timer.periodic(10, () -> map.getAll().forEach((key, value) -> System.out.println(">>a:" + value)));
        Thread.sleep(15);

        // lock
        ClusterDistLock lock = Dew.cluster.dist.lock("test_lock");
        lock.delete();
        Thread t1 = new Thread(() -> {
            lock.lock();
            System.out.println("Lock1 > " + Thread.currentThread().getId());
            try {
                Thread.sleep(500);
            } catch (Exception e) {
            } finally {
                System.out.println("UnLock1 > " + Thread.currentThread().getId());
                lock.unLock();
            }
        });
        t1.start();
        t1.join();
        Thread t2 = new Thread(() -> {
            ClusterDistLock lockLocal = Dew.cluster.dist.lock("test_lock");
            try {
                Assert.assertTrue(lockLocal.tryLock());
                System.out.println("Lock2 > " + Thread.currentThread().getId());
                Thread.sleep(10000);
            } catch (Exception e) {
            } finally {
                lockLocal.unLock();
                System.out.println("UnLock2 > " + Thread.currentThread().getId());
            }
        });
        t2.start();
        Thread.sleep(1000);
        Thread t3 = new Thread(() -> {
            ClusterDistLock lockLocal = Dew.cluster.dist.lock("test_lock");
            try {
                while (!lockLocal.tryLock()) {
                    System.out.println("waiting 1 unlock");
                    Thread.sleep(100);
                }
                System.out.println("Lock3 > " + Thread.currentThread().getId());
            } catch (Exception e) {
            } finally {
                System.out.println("UnLock3 > " + Thread.currentThread().getId());
                lockLocal.unLock();
            }
        });
        t3.start();
        Thread t4 = new Thread(() -> {
            ClusterDistLock lockLocal = Dew.cluster.dist.lock("test_lock");
            try {
                while (!lockLocal.tryLock(5000)) {
                    System.out.println("waiting 2 unlock");
                    Thread.sleep(100);
                }
                System.out.println("Lock4 > " + Thread.currentThread().getId());
            } catch (Exception e) {
            } finally {
                System.out.println("UnLock4 > " + Thread.currentThread().getId());
                lockLocal.unLock();
            }
        });
        t4.start();
        t2.join();
        t3.join();
        t4.join();
    }

    @Test
    public void testMQ() throws InterruptedException {
        // pub-sub
        CountDownLatch pubSubCdl = new CountDownLatch(4);
        new Thread(() -> {
            Dew.cluster.mq.subscribe("test_pub_sub", message -> {
                Assert.assertTrue(message.contains("msg"));
                System.out.println("1 pub_sub>>" + message);
                pubSubCdl.countDown();
            });
            try {
                pubSubCdl.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();

        new Thread(() -> {
            Dew.cluster.mq.subscribe("test_pub_sub", message -> {
                Assert.assertTrue(message.contains("msg"));
                System.out.println("2 pub_sub>>" + message);
                pubSubCdl.countDown();
            });
            try {
                pubSubCdl.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();

        new Thread(() -> {
            Dew.cluster.mq.subscribe("test_pub_sub/a", message -> {
                Assert.assertTrue(1 == 2);
            });
            try {
                new CountDownLatch(1).await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();

        Thread.sleep(1000);
        Dew.cluster.mq.publish("test_pub_sub", "msgA");
        Thread.sleep(100);
        Dew.cluster.mq.publish("test_pub_sub", "msgB");
        pubSubCdl.await();

        // req-resp
        List<String> conflictFlag = new ArrayList<>();
        new Thread(() -> {
            Dew.cluster.mq.response("test_rep_resp", message -> {
                if (conflictFlag.contains(message)) {
                    Assert.assertTrue(1 == 2);
                } else {
                    conflictFlag.add(message);
                    System.out.println("1 req_resp>>" + message);
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
                    System.out.println("2 req_resp>>" + message);
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
    }

    static class TestMapObj implements Serializable {
        private String a;

        public String getA() {
            return a;
        }

        public void setA(String a) {
            this.a = a;
        }
    }


}
