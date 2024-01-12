package group.idealworld.dew.core.cluster;

import group.idealworld.dew.core.cluster.spi.rocket.RocketClusterMQ;
import group.idealworld.dew.core.cluster.test.ClusterMQTest;
import group.idealworld.dew.test.RocketMQExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.testcontainers.junit.jupiter.Testcontainers;

/**
 * Cluster test.
 *
 * @author nipeixuan
 */
@ExtendWith({SpringExtension.class, RocketMQExtension.class})
@ContextConfiguration(initializers = RocketMQExtension.Initializer.class)
@SpringBootApplication
@SpringBootTest
@Testcontainers
public class ClusterTest {

    @Autowired
    private RocketClusterMQ rocketClusterMQ;

    /**
     * Test mq.
     *
     * @throws InterruptedException the interrupted exception
     */
    @Test
    public void testMQ() throws InterruptedException {
        new ClusterMQTest().test(rocketClusterMQ);
    }

}
