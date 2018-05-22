package com.tairanchina.csp.dew.mybatis.multi;

import com.tairanchina.csp.dew.jdbc.sharding.ShardingEnvironmentAware;
import com.tairanchina.csp.dew.mybatis.multi.entity.TOrder;
import com.tairanchina.csp.dew.mybatis.multi.service.TOrderService;
import io.shardingjdbc.transaction.api.SoftTransactionManager;
import io.shardingjdbc.transaction.bed.BEDSoftTransaction;
import io.shardingjdbc.transaction.constants.SoftTransactionType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.sql.SQLException;

/**
 * desription:
 * Created by ding on 2018/1/2.
 */
@Component
public class MultiInit {

    private static final Logger logger = LoggerFactory.getLogger(MultiInit.class);

    @Autowired
    private TOrderService tOrderService;

    @Autowired
    private SoftTransactionManager softTransactionManager;


    @Autowired
    private ShardingEnvironmentAware shardingEnvironmentAware;

    /**
     * 测试事务代码
     *
     * @throws SQLException
     */
//    @PostConstruct
    public void init() throws SQLException, InterruptedException {
        new Thread(() -> {
            logger.info("sharding init test start");
            BEDSoftTransaction softTransaction = (BEDSoftTransaction) softTransactionManager.getTransaction(SoftTransactionType.BestEffortsDelivery);
            try {
                try {
                    softTransaction.begin(shardingEnvironmentAware.dataSource().getConnection());
                } catch (SQLException e) {
                    logger.info("sharding transaction begin failed");
                }
                TOrder tOrder = new TOrder();
                tOrder.setUserId(12).setStatus("test");
                tOrder.setOrderId(1012);
                tOrderService.insert(tOrder);
                tOrderService.insert(tOrder);
                tOrderService.insert(tOrder);
                logger.info("sharding init test end");
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } finally {
                try {
                    softTransaction.end();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }).start();
        Thread.sleep(100);
        new Thread(() -> {
            TOrder tOrder = new TOrder();
            tOrder.setUserId(12).setStatus("test");
            tOrder.setOrderId(1020);
            tOrderService.insert(tOrder);
            tOrderService.insert(tOrder);
        }).start();
        new Thread(() -> {
            TOrder tOrder = new TOrder();
            tOrder.setUserId(12).setStatus("test");
            tOrder.setOrderId(1021);
            tOrderService.insert(tOrder);
            tOrderService.insert(tOrder);
        }).start();
        new Thread(() -> {
            TOrder tOrder = new TOrder();
            tOrder.setUserId(12).setStatus("test");
            tOrder.setOrderId(1022);
            tOrderService.insert(tOrder);
            tOrderService.insert(tOrder);
        }).start();
        new Thread(() -> {
            TOrder tOrder = new TOrder();
            tOrder.setUserId(12).setStatus("test");
            tOrder.setOrderId(1023);
            tOrderService.insert(tOrder);
            tOrderService.insert(tOrder);
        }).start();

    }
}
