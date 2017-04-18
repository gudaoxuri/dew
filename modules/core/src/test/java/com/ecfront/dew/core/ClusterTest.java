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
    public void testMap() throws InterruptedException {
        ClusterDistMap<TestMapObj> mapObj = Dew.cluster.dist.map("test_obj_map");
        mapObj.clear();
        TestMapObj obj = new TestMapObj();
        obj.a = "测试";
        mapObj.put("a", obj);
        Assert.assertEquals(mapObj.get("a").a, "测试");

        ClusterDistMap<Long> map = Dew.cluster.dist.map("test_map");
        map.clear();
        Dew.Timer.periodic(1000, () -> map.put("a" + System.currentTimeMillis(), System.currentTimeMillis()));
        Dew.Timer.periodic(10000, () -> map.getAll().forEach((key, value) -> System.out.println(">>a:" + value)));
        new CountDownLatch(1).await();
    }

    @Test
    public void testLock() throws InterruptedException {
        ClusterDistLock lock = Dew.cluster.dist.lock("test_lock");
        lock.delete();
        Thread t1 = new Thread(() -> {
            lock.lock();
            System.out.println("Lock > " + Thread.currentThread().getId());
            try {
                Thread.sleep(500);
            } catch (Exception e) {
            } finally {
                System.out.println("UnLock > " + Thread.currentThread().getId());
                lock.unLock();
            }
        });
        t1.start();
        t1.join();
        Thread t3 = new Thread(() -> {
            ClusterDistLock lockLocal = Dew.cluster.dist.lock("test_lock");
            try {
                Assert.assertTrue(lockLocal.tryLock());
                System.out.println("locked");
                Thread.sleep(10000);
                lockLocal.unLock();
                System.out.println("unLock");
            } catch (Exception e) {
            } finally {
                System.out.println("UnLock > " + Thread.currentThread().getId());
                lockLocal.unLock();
            }
        });
        t3.start();
        Thread.sleep(1000);
        Thread t4 = new Thread(() -> {
            ClusterDistLock lockLocal = Dew.cluster.dist.lock("test_lock");
            try {
                while (!lockLocal.tryLock()) {
                    System.out.println("waiting 1 unlock");
                    Thread.sleep(100);
                }
            } catch (Exception e) {
            } finally {
                System.out.println("UnLock > " + Thread.currentThread().getId());
                lockLocal.unLock();
            }
        });
        t4.start();
        Thread t5 = new Thread(() -> {
            ClusterDistLock lockLocal = Dew.cluster.dist.lock("test_lock");
            try {
                while (!lockLocal.tryLock(5000)) {
                    System.out.println("waiting 2 unlock");
                    Thread.sleep(100);
                }
            } catch (Exception e) {
            } finally {
                System.out.println("UnLock > " + Thread.currentThread().getId());
                lockLocal.unLock();
            }
        });
        t5.start();
        t3.join();
        t4.join();
        t5.join();
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
