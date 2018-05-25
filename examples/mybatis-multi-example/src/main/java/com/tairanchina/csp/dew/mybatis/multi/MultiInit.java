package com.tairanchina.csp.dew.mybatis.multi;

import com.tairanchina.csp.dew.mybatis.multi.entity.TOrder;
import com.tairanchina.csp.dew.mybatis.multi.service.TOrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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


    /**
     * 测试事务代码
     *
     * @throws SQLException
     */
//    @PostConstruct
    public void init() throws InterruptedException {
        new Thread(() -> {
            logger.info("init test start");
            TOrder tOrder = new TOrder();
            tOrder.setUserId(12).setStatus("test");
            tOrder.setOrderId(1012);
            tOrderService.insert(tOrder);
            tOrderService.insert(tOrder);
            tOrderService.insert(tOrder);
            logger.info("init test end");
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
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
