package group.idealworld.dew.core.cluster;

import group.idealworld.dew.core.cluster.spi.rabbit.RabbitClusterMQ;
import group.idealworld.dew.core.cluster.test.ClusterMQTest;
import group.idealworld.dew.test.RabbitMQExtension;
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
 * @author gudaoxuri
 */
@ExtendWith({SpringExtension.class, RabbitMQExtension.class})
@ContextConfiguration(initializers = RabbitMQExtension.Initializer.class)
@SpringBootApplication
@SpringBootTest
@Testcontainers
public class ClusterTest {

    @Autowired
    private RabbitClusterMQ rabbitClusterMQ;

    /**
     * Test mq.
     *
     * @throws InterruptedException the interrupted exception
     */
    @Test
    public void testMQ() throws InterruptedException {
        new ClusterMQTest().test(rabbitClusterMQ);
    }

}
