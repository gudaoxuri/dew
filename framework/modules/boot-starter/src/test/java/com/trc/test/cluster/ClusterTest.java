package com.trc.test.cluster;

import group.idealworld.dew.Dew;
import group.idealworld.dew.core.cluster.test.ClusterCacheTest;
import group.idealworld.dew.core.cluster.test.ClusterElectionTest;
import group.idealworld.dew.core.cluster.test.ClusterLockTest;
import group.idealworld.dew.core.cluster.test.ClusterMapTest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Cluster test.
 *
 * @author gudaoxuri
 */
@Component
public class ClusterTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(ClusterTest.class);

    /**
     * Test all.
     *
     * @throws InterruptedException the interrupted exception
     */
    public void testAll() throws InterruptedException {
        LOGGER.info("Testing Cache");
        new ClusterCacheTest().test(Dew.cluster.cache, null);
        LOGGER.info("Testing Lock");
        new ClusterLockTest().test(Dew.cluster.lock.instance("test"));
        LOGGER.info("Testing Map");
        new ClusterMapTest().test(Dew.cluster.map.instance("test", ClusterMapTest.TestMapObj.class));
        // LOGGER.info("Testing MQ");
        // new ClusterMQTest().test(Dew.cluster.mq);
        LOGGER.info("Testing Election");
        new ClusterElectionTest().test(Dew.cluster.election.instance("test"));
    }

}
