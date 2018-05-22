package com.trc.dew.sharding;


import com.tairanchina.csp.dew.Dew;
import com.tairanchina.csp.dew.jdbc.DewSB;
import com.trc.dew.sharding.entity.TOrder;
import org.junit.Assert;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class TestSharding {

    public void testSharding() {
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
    }
}
