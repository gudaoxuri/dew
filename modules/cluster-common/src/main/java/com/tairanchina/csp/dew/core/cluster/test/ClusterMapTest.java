package com.tairanchina.csp.dew.core.cluster.test;

import com.tairanchina.csp.dew.core.cluster.ClusterMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;

public class ClusterMapTest {

    private static final Logger logger = LoggerFactory.getLogger(ClusterMapTest.class);

    public void test(ClusterMap<TestMapObj> map) throws InterruptedException {
        map.clear();
        assert !map.containsKey("map");
        assert map.get("map") == null;
        // sync
        TestMapObj obj = new TestMapObj();
        obj.setA("测试");
        map.put("map", obj);
        assert map.containsKey("map");
        assert map.get("map").getA().equals("测试");
        map.remove("map");
        assert !map.containsKey("map");
        assert map.get("map") == null;
        // async
        map.putAsync("async_map", obj);
        while (!map.containsKey("async_map")) {
            Thread.sleep(100);
        }
        assert map.get("async_map").getA().equals("测试");
        map.removeAsync("async_map");
        while (map.containsKey("async_map")) {
            Thread.sleep(100);
        }
        assert map.get("map") == null;
        // getall
        map.put("map1", obj);
        map.put("map2", obj);
        assert map.getAll().get("map1").getA().equals("测试") && map.getAll().containsKey("map2");
    }

    public static class TestMapObj implements Serializable {

        private String a;

        public String getA() {
            return a;
        }

        public void setA(String a) {
            this.a = a;
        }

    }

}
