package com.ecfront.dew.example.sharding.test;


import com.ecfront.dew.Dew;
import com.ecfront.dew.example.sharding.ShardingApplication;
import com.ecfront.dew.example.sharding.entity.TOrder;
import com.ecfront.dew.jdbc.DewSB;
import com.ecfront.dew.Dew;
import com.ecfront.dew.example.sharding.ShardingApplication;
import com.ecfront.dew.example.sharding.entity.TOrder;
import com.ecfront.dew.jdbc.DewSB;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {ShardingApplication.class})
public class ShardingTest {

    @Test
    public void testShardingDataSourceAndTable() {
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
        Assert.assertEquals(20,Dew.ds("sharding").countAll(TOrder.class));
    }

    @Test
    public void testShardingAndMasterSlave() throws InterruptedException {
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
        Thread.sleep(1000);
        List<TOrder> tOrderList = Dew.ds("sharding").find(DewSB.inst().eq("status", "test"), TOrder.class);
//        Assert.assertEquals(20, tOrderList.size());
        Dew.ds("sharding").delete(DewSB.inst().eq("userId", 12), TOrder.class);
        Dew.ds("sharding").delete(DewSB.inst().eq("userId", 13), TOrder.class);

    }

}
