package com.trc.dew.ds.util;

import com.tairanchina.csp.dew.Dew;
import com.trc.dew.ds.entity.BasicEntity;
import org.junit.Assert;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;


@Component
public class TxService {

    @Transactional
    public void testCommit() {
        BasicEntity basicEntity = new BasicEntity();
        basicEntity.setFieldA("TransactionA1");
        basicEntity.setFieldB("TransactionA1");
        Object id = Dew.ds().insert(basicEntity);
        Assert.assertTrue((int) id != 0);
    }

    @Transactional(rollbackFor = {Exception.class, RuntimeException.class, NullPointerException.class})
    public void testRollBack() throws Exception {
        BasicEntity basicEntity = new BasicEntity();
        basicEntity.setFieldA("TransactionA2");
        basicEntity.setFieldB("TransactionA2");
        Object id = Dew.ds().insert(basicEntity);
        Assert.assertTrue((int) id != 0);
        throw new Exception("");
    }

    @Transactional("test2TransactionManager")
    public void testMultiCommit() {
        BasicEntity basicEntity = new BasicEntity();
        basicEntity.setId(1);
        basicEntity.setFieldA("TransactionA1");
        basicEntity.setFieldB("TransactionA1");
        Object id = Dew.ds("test2").insert(basicEntity);
        Assert.assertTrue((int) id != 0);
    }

    @Transactional(value = ("test2TransactionManager"), rollbackFor = {Exception.class, RuntimeException.class, NullPointerException.class})
    public void testMultiRollBack() throws Exception {
        BasicEntity basicEntity = new BasicEntity();
        basicEntity.setId(2);
        basicEntity.setFieldA("TransactionA2");
        basicEntity.setFieldB("TransactionA2");
        Object id = Dew.ds("test2").insert(basicEntity);
        Assert.assertTrue((int) id != 0);
        throw new Exception("");
    }

}
