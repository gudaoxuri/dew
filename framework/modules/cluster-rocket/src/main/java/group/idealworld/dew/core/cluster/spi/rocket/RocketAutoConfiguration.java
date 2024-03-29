package group.idealworld.dew.core.cluster.spi.rocket;

import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

/**
 * rocket auto configuration.
 *
 * @author nipeixuan
 */
@Configuration
@ConditionalOnClass(RocketMQTemplate.class)
@ConditionalOnExpression("#{'${dew.cluster.mq}'=='rocket'}")
public class RocketAutoConfiguration {

    private static final Logger LOGGER = LoggerFactory.getLogger(RocketAutoConfiguration.class);

    @Value("${rocketmq.producer.group}")
    private String producerGroupName;

    @Value("${rocketmq.consumer.group}")
    private String consumerGroupName;

    @Value("${rocketmq.name-server}")
    private String nameServer;

    @PostConstruct
    public void init() {
        LOGGER.info("Load Auto Configuration : {}", this.getClass().getName());
    }

    /**
     * Rabbit adapter.
     *
     * @param rocketMQTemplate the rocket template
     * @return the rocket adapter
     */
    @Bean
    public RocketAdapter rocketAdapter(RocketMQTemplate rocketMQTemplate) {
        return new RocketAdapter(rocketMQTemplate);
    }

    /**
     * Rocket cluster mq.
     *
     * @param rocketAdapter the rocket adapter
     * @return the rocket cluster mq
     */
    @Bean
    @ConditionalOnExpression("'${dew.cluster.mq}'=='rocket'")
    public RocketClusterMQ rocketClusterMQ(RocketAdapter rocketAdapter) {
        return new RocketClusterMQ(rocketAdapter, nameServer, producerGroupName, consumerGroupName);
    }

}
