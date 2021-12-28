/*
 * Copyright 2020. the original author or authors.
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

        // pub-sub
        Dew.cluster.mq.subscribe("test_pub_sub", message ->
                logger.info("pub_sub>>" + message));
        Thread.sleep(1000);
        Dew.cluster.mq.publish("test_pub_sub", "msgA",null,true);
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
