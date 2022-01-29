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

import group.idealworld.dew.core.cluster.ClusterCache;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Cluster cache test.
 *
 * @author gudaoxuri
 */
public class ClusterCacheTest {

    /**
     * Test.
     *
     * @param cache the cache
     * @throws InterruptedException the interrupted exception
     */
    public void test(ClusterCache cache, ClusterCache authCache) throws InterruptedException {
        cache.flushdb();
        assert !cache.exists("key");
        assert cache.getSet("key", "keyxx") == null;
        cache.set("key", "value");
        assert cache.get("key").equals("value");
        assert cache.getSet("key", "new_value").equals("value");
        assert cache.get("key").equals("new_value");
        assert cache.exists("key");
        cache.del("key");
        assert !cache.exists("key");
        assert cache.getSet("key", "value") == null;
        assert cache.get("key").equals("value");
        cache.del("key");

        // setex
        cache.setex("key", "value", 1);
        assert cache.get("key").equals("value");
        Thread.sleep(1100);
        assert cache.get("key") == null;
        assert !cache.exists("key");

        // setnx
        assert cache.setnx("key", "value", 1);
        assert cache.get("key").equals("value");
        assert !cache.setnx("key", "new_value", 1);
        assert cache.get("key").equals("value");

        // ttl
        assert cache.ttl("ttl") == -2;
        cache.set("ttl", "");
        assert cache.ttl("ttl") == -1;
        cache.expire("ttl", 10);
        assert cache.ttl("ttl") > 0;

        // list
        assert cache.lget("list").isEmpty();
        cache.lpush("list", "1");
        cache.lmset("list", new ArrayList<>() {
            {
                add("2");
                add("2");
                add("3");
            }
        });
        assert cache.llen("list") == 4;
        assert cache.lget("list").get(0).equals("3");
        assert cache.lpop("list").equals("3");

        // set
        assert cache.sget("set").isEmpty();
        cache.sset("set", "1");
        cache.smset("set", new ArrayList<>() {
            {
                add("2");
                add("2");
                add("3");
            }
        });
        assert cache.slen("set") == 3;
        assert cache.sget("set").contains("1");
        cache.spop("set");
        assert cache.slen("set") == 2;
        cache.sdel("set", "1", "2", "3");
        assert cache.slen("set") == 0;

        // hash
        assert cache.hgetAll("hash").isEmpty();
        assert cache.hget("hash", "field1") == null;
        cache.hset("hash", "field1", "value1");
        assert cache.hget("hash", "field1").equals("value1");
        cache.hmset("hash", new HashMap<String, String>() {
            {
                put("field2", "value2");
                put("field3", "value3");
            }
        });
        assert cache.hgetAll("hash").containsKey("field2");
        assert cache.hexists("hash", "field2");
        assert cache.hkeys("hash").contains("field2");
        assert cache.hvalues("hash").contains("value2");
        assert cache.hlen("hash") == 3;
        cache.hdel("hash", "field2");
        assert cache.hlen("hash") == 2;

        // counter
        assert cache.incrBy("counter", 1) == 1;
        assert cache.incrBy("counter", 1) == 2;
        assert cache.decrBy("counter", 1) == 1;
        assert cache.decrBy("counter", 1) == 0;
        assert cache.decrBy("counter", 1) == -1;
        assert cache.hashIncrBy("h_counter", "field1", 1) == 1;
        assert cache.hashIncrBy("h_counter", "field1", 1) == 2;
        assert cache.hashDecrBy("h_counter", "field1", 1) == 1;
        assert cache.hashDecrBy("h_counter", "field1", 1) == 0;
        assert cache.hashDecrBy("h_counter", "field1", 1) == -1;

        // bit
        assert !cache.setBit("bit", 100, true);
        assert cache.getBit("bit", 100);
        assert cache.setBit("bit", 100, true);
        assert !cache.getBit("bit", 101);
        assert !cache.setBit("bit", 101, false);
        assert !cache.getBit("bit", 101);
        assert !cache.setBit("bit", 101, true);
        assert cache.getBit("bit", 101);


        cache.flushdb();
        cache.setIfAbsent("key", "Ture");
        cache.setIfAbsent("key", "False");
        assert cache.get("key").equals("Ture");

        cache.hsetIfAbsent("hkey", "key", "Ture");
        cache.hsetIfAbsent("hkey", "key", "False");
        assert cache.hget("hkey", "key").equals("Ture");

        // multi
        if (authCache != null) {
            authCache.set("token", "xxxxx");
            assert !cache.exists("token");
            assert authCache.exists("token");
            authCache.del("token");
            assert !authCache.exists("token");
        }

    }

}
