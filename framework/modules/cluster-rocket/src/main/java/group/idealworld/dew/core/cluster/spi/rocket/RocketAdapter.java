package group.idealworld.dew.core.cluster.spi.rocket;

import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.spring.core.RocketMQTemplate;

/**
 * Rocket adapter.
 *
 * @author nipeixuan
 */
public class RocketAdapter {

    private final RocketMQTemplate rocketMQTemplate;

    /**
     * Instantiates a new Rabbit adapter.
     *
     * @param rocketMQTemplate the rabbit template
     */
    public RocketAdapter(RocketMQTemplate rocketMQTemplate) {
        this.rocketMQTemplate = rocketMQTemplate;
    }

    DefaultMQProducer getProducer() {
        return rocketMQTemplate.getProducer();
    }

    RocketMQTemplate getRocketMQTemplate() {
        return this.rocketMQTemplate;
    }

}
