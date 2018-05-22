package com.tairanchina.csp.dew.mybatis.multi;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.tairanchina.csp.dew.Dew;
import com.tairanchina.csp.dew.jdbc.DewDS;
import com.tairanchina.csp.dew.jdbc.DewSB;
import com.tairanchina.csp.dew.jdbc.sharding.ShardingEnvironmentAware;
import com.tairanchina.csp.dew.mybatis.multi.entity.TOrder;
import com.tairanchina.csp.dew.mybatis.multi.entity.User;
import com.tairanchina.csp.dew.mybatis.multi.service.TOrderService;
import com.tairanchina.csp.dew.mybatis.multi.service.UserService;
import com.tairanchina.csp.dew.mybatis.multi.service.UserService2;
import io.shardingjdbc.transaction.api.SoftTransactionManager;
import io.shardingjdbc.transaction.bed.BEDSoftTransaction;
import io.shardingjdbc.transaction.constants.SoftTransactionType;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.sql.SQLException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * desription:
 * Created by ding on 2017/12/28.
 */
@SpringBootTest(classes = MybatisMultiApplication.class)
@RunWith(SpringRunner.class)
public class MybatisMultiTest {

    private static final Logger logger = LoggerFactory.getLogger(MybatisMultiTest.class);

    @Autowired
    private UserService userService;

    @Autowired
    private UserService2 userService2;

    @Autowired
    private TOrderService tOrderService;

    @Autowired
    private SoftTransactionManager softTransactionManager;


    @Autowired
    private ShardingEnvironmentAware shardingEnvironmentAware;

    @Before
    public void init() {
        ((DewDS) Dew.ds()).jdbc().execute("CREATE TABLE IF NOT EXISTS user\n" +
                "(\n" +
                "test_id int primary key,\n" +
                "name varchar(50),\n" +
                "age INT ,\n" +
                "test_type INT ,\n" +
                "test_date datetime,\n" +
                "role long,\n" +
                "phone varchar(50)\n" +
                ")");
        ((DewDS) Dew.ds("test1")).jdbc().execute("CREATE TABLE IF NOT EXISTS user\n" +
                "(\n" +
                "test_id int primary key,\n" +
                "name varchar(50),\n" +
                "age INT ,\n" +
                "test_type INT ,\n" +
                "test_date datetime,\n" +
                "role long,\n" +
                "phone varchar(50)\n" +
                ")");
        // 删除项目中测试的数据
        tOrderService.delete(new EntityWrapper<TOrder>().eq("user_id", 12));
        tOrderService.delete(new EntityWrapper<TOrder>().eq("user_id", 13));
    }

    @Test
    @Transactional
//    @Transactional("test1TransactionManager")  指定控制事务的数据源
    public void testUser() {
        testUserProcess(userService);
        testUserProcess(userService2);
    }

    public void testUserProcess(ServiceImpl service) {
        User user = new User();
        user.setId(1L);
        user.setName("Tom");
        service.insert(user);
        logger.info("======================");

        User exampleUser = (User) service.selectById(user.getId());
        exampleUser.setAge(18);
        service.updateById(exampleUser);

        List<User> userList = service.selectList(
                new EntityWrapper<User>().eq("name", "Tom")
        );
        logger.info("========userList=========size====={}", userList.size());

        Page<User> userListTemp = service.selectPage(
                new Page<User>(1, 2),
                new EntityWrapper<User>().eq("name", "Tom")
        );
        logger.info("========userList=========size====={}", userListTemp.getRecords().size());

        userList = service.selectList(
                new EntityWrapper<User>().eq("age", "19")
        );
        logger.info("========userList===age===19===size====={}", userList.size());

        service.delete(new EntityWrapper<User>().eq("age", "19"));

        userList = service.selectList(
                new EntityWrapper<User>().eq("age", "19")
        );
        logger.info("========userList=========size====={}", userList.size());

        List<String> ages = null;
        if (service instanceof UserService) {
            ages = ((UserService) service).ageGroup();
        }
        if (service instanceof UserService2) {
            ages = ((UserService2) service).ageGroup();
        }
        logger.info("========userList=========ages====={}", ages);
    }

    @Test
    public void testShardingPlus() {
        long countStart = tOrderService.selectCount(new EntityWrapper<>());
        TOrder tOrder = new TOrder();
        tOrder.setUserId(13).setStatus("test");
        for (int i = 1110; i < 1120; i++) {
            tOrder.setId(null).setOrderId(i);
            tOrderService.insert(tOrder);
        }
        tOrder.setUserId(12).setStatus("test");
        for (int i = 1010; i < 1020; i++) {
            tOrder.setId(null).setOrderId(i);
            tOrderService.insert(tOrder);
        }
        Assert.assertTrue((tOrderService.selectCount(null) - countStart) == 20);
        List<TOrder> tOrderList = tOrderService.selectList(new EntityWrapper<TOrder>().eq("status", "test"));
        Assert.assertEquals(20, tOrderList.size());
        tOrderService.delete(new EntityWrapper<TOrder>().eq("user_id", 12));
        tOrderService.delete(new EntityWrapper<TOrder>().eq("user_id", 13));
        Assert.assertEquals(20, Dew.ds("sharding").countAll(TOrder.class));
    }

    @Test
    public void testShardingDew() {
        long countStart = Dew.ds("sharding").countAll(TOrder.class);
        TOrder tOrder = new TOrder();
        tOrder.setUserId(13).setStatus("test");
        for (int i = 1110; i < 1120; i++) {
            tOrder.setOrderId(i);
            Dew.ds("sharding").insert(tOrder);
        }
        tOrder.setUserId(12).setStatus("test");
        for (int i = 1010; i < 1020; i++) {
            tOrder.setOrderId(i);
            Dew.ds("sharding").insert(tOrder);
        }
        Assert.assertTrue((Dew.ds("sharding").countAll(TOrder.class) - countStart) == 20);
        List<TOrder> tOrderList = Dew.ds("sharding").find(DewSB.inst().eq("status", "test"), TOrder.class);
        Assert.assertEquals(20, tOrderList.size());
        Dew.ds("sharding").delete(DewSB.inst().eq("userId", 12), TOrder.class);
        Dew.ds("sharding").delete(DewSB.inst().eq("userId", 13), TOrder.class);
        Assert.assertEquals(20, Dew.ds("sharding").countAll(TOrder.class));
    }

    @Test
    public void testShardingWithXml() {
        long countStart = tOrderService.countAllByXml();
        TOrder tOrder = new TOrder();
        tOrder.setUserId(13).setStatus("test");
        for (int i = 1110; i < 1120; i++) {
            tOrder.setId(i + "").setOrderId(i);
            tOrderService.insert(tOrder);
        }
        tOrder.setUserId(12).setStatus("test");
        for (int i = 1010; i < 1020; i++) {
            tOrder.setId(i + "").setOrderId(i);
            tOrderService.insert(tOrder);
        }
        Assert.assertTrue((tOrderService.countAllByXml() - countStart) == 20);
        List<TOrder> tOrderList = tOrderService.selectList(new EntityWrapper<TOrder>().eq("status", "test"));
        Assert.assertEquals(20, tOrderList.size());
        tOrderService.deleteByXml(12);
        tOrderService.deleteByXml(13);
        Assert.assertEquals(20, Dew.ds("sharding").countAll(TOrder.class));

    }


    @Test
    public void testPressure() throws InterruptedException {
        long start = Instant.now().toEpochMilli();
        // 100 个并发，各执行 5000条插入
        CountDownLatch countDownLatch = new CountDownLatch(100);
        int times = 2500;
        for (int i = 10000; i < 260000; i += times) {
            new Thread(new PressureTask(i, i + times, countDownLatch, false)).start();
        }
        countDownLatch.await();
        logger.info("运行结束，耗时" + (Instant.now().toEpochMilli() - start) + " ms");
    }

    class PressureTask implements Runnable {

        private int start;

        private int end;

        private boolean enableTransaction;

        private CountDownLatch countDownLatch;

        public PressureTask() {
        }

        PressureTask(int start, int end, CountDownLatch countDownLatch, boolean enableTransaction) {
            this.start = start;
            this.end = end;
            this.countDownLatch = countDownLatch;
            this.enableTransaction = enableTransaction;
        }

        @Override
        public void run() {
            BEDSoftTransaction softTransaction = (BEDSoftTransaction) softTransactionManager.getTransaction(SoftTransactionType.BestEffortsDelivery);
            try {
                // 是否开启事务
                if (enableTransaction) {
                    try {
                        softTransaction.begin(shardingEnvironmentAware.dataSource().getConnection());
                    } catch (SQLException e) {
                        logger.error("sharding transaction begin failed", e);
                    }
                }
                TOrder tOrder = new TOrder();
                tOrder.setUserId(13).setStatus("test");
                for (int i = start; i < end; i++) {
                    tOrder.setId(null).setOrderId(i);
                    tOrderService.insertByXml(tOrder);
                }
                tOrder.setUserId(12).setStatus("test");
                for (int i = start; i < end; i++) {
                    tOrder.setId(null).setOrderId(i);
                    tOrderService.insertByXml(tOrder);
                }
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    Thread.interrupted();
                    logger.error("sharding transaction begin failed", e);
                }
            } finally {
                try {
                    // 是否开启事务
                    if (enableTransaction) {
                        softTransaction.end();
                    }
                    countDownLatch.countDown();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Test
    public void testBatchInsert() {
        List<User> mybatis = new ArrayList<>();
        List<User> dew = new ArrayList<>();

        for (int i = 1; i <= 3000; i++) {
            User user = new User();
            user.setId((long) i);
            user.setRole((long) i);
            user.setAge(i);
            user.setName("user" + i);
            user.setTestDate(new Date());
            user.setTestType(i);
            user.setPhone("15957199704");
            if (i > 1500) {
                dew.add(user);
            } else {
                mybatis.add(user);
            }
        }
        long mybatisStart = Instant.now().toEpochMilli();
        /*for (User user:mybatis){
            userService.insert(user);
        }*/
        userService.batchInsert(mybatis);
        logger.info("mybatis耗时   " + (Instant.now().toEpochMilli() - mybatisStart));
        long dewStart = Instant.now().toEpochMilli();
        Dew.ds().insert(dew);
        logger.info("dew耗时   " + (Instant.now().toEpochMilli() - dewStart));
    }
}
