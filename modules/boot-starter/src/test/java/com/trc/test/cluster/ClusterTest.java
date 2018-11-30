package com.trc.test.cluster;

import com.tairanchina.csp.dew.Dew;
import com.tairanchina.csp.dew.core.cluster.test.*;
import org.springframework.stereotype.Component;

@Component
public class ClusterTest {

    public void testAll() throws InterruptedException {
        new ClusterMQTest().test(Dew.cluster.mq);
        new ClusterCacheTest().test(Dew.cluster.cache);
        new ClusterLockTest().test(Dew.cluster.lock.instance("test"));
        new ClusterMapTest().test(Dew.cluster.map.instance("test", ClusterMapTest.TestMapObj.class));
        new ClusterElectionTest().test(Dew.cluster.election.instance("test"));
    }

}
