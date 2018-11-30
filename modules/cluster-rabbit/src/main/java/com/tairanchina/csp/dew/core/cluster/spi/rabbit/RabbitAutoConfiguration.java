package com.tairanchina.csp.dew.core.cluster.spi.rabbit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

@Configuration
@ConditionalOnClass(RabbitTemplate.class)
@ConditionalOnExpression("#{'${dew.cluster.cache}'=='rabbit' || '${dew.cluster.mq}'=='rabbit' || '${dew.cluster.lock}'=='rabbit' || '${dew.cluster.map}'=='rabbit' || '${dew.cluster.election}'=='rabbit'}")
public class RabbitAutoConfiguration {

    private static final Logger logger = LoggerFactory.getLogger(RabbitAutoConfiguration.class);

    @PostConstruct
    private void init() {
        logger.info("Load Auto Configuration : {}", this.getClass().getName());
    }

    @Bean
    public RabbitAdapter rabbitAdapter(RabbitTemplate rabbitTemplate) {
        return new RabbitAdapter(rabbitTemplate);
    }

    @Bean
    @ConditionalOnExpression("'${dew.cluster.mq}'=='rabbit'")
    public RabbitClusterMQ rabbitClusterMQ(RabbitAdapter rabbitAdapter) {
        return new RabbitClusterMQ(rabbitAdapter);
    }

}
