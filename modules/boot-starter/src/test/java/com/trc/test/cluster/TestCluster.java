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
        // Dist Test
        testDistMapExp();
        testDistLockWithFun();
        testUnLock();
        testConnection();
        testDistMap();

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
