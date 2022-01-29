/*
 * Copyright 2022. the original author or authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
