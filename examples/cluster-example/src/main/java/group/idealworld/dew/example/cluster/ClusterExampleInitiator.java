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

package group.idealworld.dew.example.cluster;

import com.ecfront.dew.common.$;
import group.idealworld.dew.Dew;
import group.idealworld.dew.core.cluster.ClusterLock;
import group.idealworld.dew.core.cluster.ClusterMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.Serializable;

/**
 * Cluster example initiator.
 *
 * @author gudaoxuri
 */
@Component
public class ClusterExampleInitiator {

    private static final Logger logger = LoggerFactory.getLogger(ClusterExampleInitiator.class);

    /**
     * Init.
     *
     * @throws Exception the exception
     */
    @PostConstruct
    public void init() throws Exception {
        // cache
        Dew.cluster.cache.flushdb();
        Dew.cluster.cache.del("n_test");
        assert !Dew.cluster.cache.exists("n_test");
        Dew.cluster.cache.setex("n_test", "{\"name\":\"jzy\"}", 1);
        assert Dew.cluster.cache.exists("n_test");
        assert "jzy".equals($.json.toJson(Dew.cluster.cache.get("n_test")).get("name").asText());
        Thread.sleep(1000);
        assert !Dew.cluster.cache.exists("n_test");
        assert null == Dew.cluster.cache.get("n_test");
        // ...

        // map
        ClusterMap<TestMapObj> mapObj = Dew.cluster.map.instance("test_obj_map", TestMapObj.class);
        mapObj.clear();
        TestMapObj obj = new TestMapObj();
        obj.someField = "测试";
        mapObj.put("test", obj);
        assert "测试".equals(mapObj.get("test").someField);
        // ...

        // lock
        ClusterLock lock = Dew.cluster.lock.instance("test_lock");
        // tryLock 示例，等待0ms，忘了手工unLock或出异常时1s后自动解锁
        if (lock.tryLock(0, 1000)) {
            try {
                // 已加锁，执行业务方法
            } finally {
                // 必须手工解锁
                lock.unLock();
            }
        }
        // tryLockWithFun 示例
        lock.tryLockWithFun(0, 1000, () -> {
            // 已加锁，执行业务方法，tryLockWithFun会将业务方法包裹在try-cache中，无需手工解锁
        });

        // pub-sub
        Dew.cluster.mq.subscribe("test_pub_sub", message ->
                logger.info("pub_sub>>" + message));
        Thread.sleep(1000);
        Dew.cluster.mq.publish("test_pub_sub", "msgA");
        Dew.cluster.mq.publish("test_pub_sub", "msgB");
        // req-resp
        Dew.cluster.mq.response("test_rep_resp", message ->
                logger.info("req_resp>>" + message));
        Dew.cluster.mq.request("test_rep_resp", "msg1");
        Dew.cluster.mq.request("test_rep_resp", "msg2");
    }

    /**
     * Test map obj.
     */
    static class TestMapObj implements Serializable {
        private String someField;

        /**
         * Gets someField.
         *
         * @return the someField
         */
        public String getSomeField() {
            return someField;
        }

        /**
         * Sets someField.
         *
         * @param someField the someField
         */
        public void setSomeField(String someField) {
            this.someField = someField;
        }
    }

}
