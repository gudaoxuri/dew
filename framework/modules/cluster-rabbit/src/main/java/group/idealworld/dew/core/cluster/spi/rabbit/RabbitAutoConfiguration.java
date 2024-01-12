package group.idealworld.dew.core.cluster.spi.rabbit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import jakarta.annotation.PostConstruct;

/**
 * Rabbit auto configuration.
 *
 * @author gudaoxuri
 */
@Configuration
@ConditionalOnClass(RabbitTemplate.class)
@ConditionalOnExpression("#{'${dew.cluster.mq}'=='rabbit'}")
public class RabbitAutoConfiguration {

    private static final Logger LOGGER = LoggerFactory.getLogger(RabbitAutoConfiguration.class);

    /**
     * Init.
     */
    @PostConstruct
    public void initConf() {
        LOGGER.info("Load Auto Configuration : {}", this.getClass().getName());
    }

    /**
     * Rabbit adapter.
     *
     * @param rabbitTemplate the rabbit template
     * @return the rabbit adapter
     */
    @Bean
    public RabbitAdapter rabbitAdapter(RabbitTemplate rabbitTemplate) {
        return new RabbitAdapter(rabbitTemplate);
    }

    /**
     * Rabbit cluster mq.
     *
     * @param rabbitAdapter the rabbit adapter
     * @return the rabbit cluster mq
     */
    @Bean
    @ConditionalOnExpression("'${dew.cluster.mq}'=='rabbit'")
    public RabbitClusterMQ rabbitClusterMQ(RabbitAdapter rabbitAdapter) {
        return new RabbitClusterMQ(rabbitAdapter);
    }

}
