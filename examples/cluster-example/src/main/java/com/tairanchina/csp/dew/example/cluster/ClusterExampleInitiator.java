package com.tairanchina.csp.dew.example.cluster;


import com.ecfront.dew.common.$;
import com.tairanchina.csp.dew.Dew;
import com.tairanchina.csp.dew.core.cluster.ClusterLock;
import com.tairanchina.csp.dew.core.cluster.ClusterMap;
import com.tairanchina.csp.dew.core.cluster.spi.rabbit.RabbitClusterMQ;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.Serializable;

@Component
public class ClusterExampleInitiator {

    private static final Logger logger = LoggerFactory.getLogger(ClusterExampleInitiator.class);

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

        // dist instance
        ClusterMap<TestMapObj> mapObj = Dew.cluster.dist.map("test_obj_map", TestMapObj.class);
        mapObj.clear();
        TestMapObj obj = new TestMapObj();
        obj.a = "测试";
        mapObj.put("test", obj);
        assert "测试".equals(mapObj.get("test").a);
        // ...

        // dist instance
        ClusterLock lock = Dew.cluster.dist.lock("test_lock");
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
        // rabbit confirm
        if (Dew.cluster.mq instanceof RabbitClusterMQ) {
            boolean success = ((RabbitClusterMQ) Dew.cluster.mq).publish("test_pub_sub", "confirm message", true);
            success = ((RabbitClusterMQ) Dew.cluster.mq).request("test_rep_resp", "confirm message", true);
        }
    }

    static class TestMapObj implements Serializable {
        private String a;

        public String getA() {
            return a;
        }

        public void setA(String a) {
            this.a = a;
        }
    }

}
