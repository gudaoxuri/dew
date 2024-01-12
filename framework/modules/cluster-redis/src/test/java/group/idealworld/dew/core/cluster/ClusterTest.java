package group.idealworld.dew.core.cluster;

import group.idealworld.dew.core.cluster.spi.redis.*;
import group.idealworld.dew.core.cluster.test.*;
import group.idealworld.dew.test.RedisExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.testcontainers.junit.jupiter.Testcontainers;

/**
 * Cluster test.
 *
 * @author gudaoxuri
 */
@ExtendWith({ SpringExtension.class, RedisExtension.class })
@ContextConfiguration(initializers = RedisExtension.Initializer.class)
@SpringBootApplication
@SpringBootTest
@Testcontainers
public class ClusterTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(ClusterTest.class);

    @Autowired
    private RedisClusterCacheWrap redisClusterCacheWrap;
    @Autowired
    private RedisClusterMQ redisClusterMQ;
    @Autowired
    private RedisClusterMapWrap redisClusterMapWrap;
    @Autowired
    private RedisClusterLockWrap redisClusterLockWrap;
    @Autowired
    private RedisClusterElectionWrap redisClusterElectionWrap;

    /**
     * Test mq.
     *
     * @throws InterruptedException the interrupted exception
     */
    // @Test
    // public void testMQ() throws InterruptedException {
    // LOGGER.info("testMQ");
    // new ClusterMQTest().test(redisClusterMQ);
    // }

    /**
     * Test cache.
     *
     * @throws InterruptedException the interrupted exception
     */
    @Test
    public void testCache() throws InterruptedException {
        LOGGER.info("testCache");
        new ClusterCacheTest().test(redisClusterCacheWrap.instance(), redisClusterCacheWrap.instance("other"));
    }

    /**
     * Test lock.
     *
     * @throws InterruptedException the interrupted exception
     */
    @Test
    public void testLock() throws InterruptedException {
        LOGGER.info("testLock");
        new ClusterLockTest().test(redisClusterLockWrap.instance("test"));
    }

    /**
     * Test map.
     *
     * @throws InterruptedException the interrupted exception
     */
    @Test
    public void testMap() throws InterruptedException {
        LOGGER.info("testMap");
        new ClusterMapTest().test(redisClusterMapWrap.instance("test", ClusterMapTest.TestMapObj.class));
    }

    /**
     * Test election.
     *
     * @throws InterruptedException the interrupted exception
     */
    @Test
    public void testElection() throws InterruptedException {
        LOGGER.info("testElection");
        new ClusterElectionTest().test(redisClusterElectionWrap.instance("test"));
    }

}
