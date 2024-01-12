package group.idealworld.dew.core.cluster.test;

import group.idealworld.dew.core.cluster.ClusterMap;

import java.io.Serializable;

/**
 * Cluster map test.
 *
 * @author gudaoxuri
 */
public class ClusterMapTest {

    /**
     * Test.
     *
     * @param map the map
     * @throws InterruptedException the interrupted exception
     */
    public void test(ClusterMap<TestMapObj> map) throws InterruptedException {
        map.clear();
        assert !map.containsKey("instance");
        assert map.get("instance") == null;
        // sync
        TestMapObj obj = new TestMapObj();
        obj.setField("测试");
        map.put("instance", obj);
        assert map.containsKey("instance");
        assert map.get("instance").getField().equals("测试");
        map.remove("instance");
        assert !map.containsKey("instance");
        assert map.get("instance") == null;
        // async
        map.putAsync("async_map", obj);
        while (!map.containsKey("async_map")) {
            Thread.sleep(100);
        }
        assert map.get("async_map").getField().equals("测试");
        map.removeAsync("async_map");
        while (map.containsKey("async_map")) {
            Thread.sleep(100);
        }
        assert map.get("instance") == null;
        // getall
        map.put("map1", obj);
        map.put("map2", obj);
        assert map.getAll().get("map1").getField().equals("测试") && map.getAll().containsKey("map2");
    }

    /**
     * Test map obj.
     */
    public static class TestMapObj implements Serializable {

        private String field;

        /**
         * Gets a.
         *
         * @return the a
         */
        public String getField() {
            return field;
        }

        /**
         * Sets a.
         *
         * @param field the a
         */
        public void setField(String field) {
            this.field = field;
        }

    }

}
