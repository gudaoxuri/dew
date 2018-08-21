package com.tairanchina.csp.dew.core.cluster.spi.kafka;

import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;

@Configuration
@ConditionalOnClass(KafkaTemplate.class)
@EnableConfigurationProperties(KafkaProperties.class)
public class KafkaAutoConfiguration {

    private KafkaProperties kafkaProperties;

    public KafkaAutoConfiguration(KafkaProperties kafkaProperties) {
        this.kafkaProperties = kafkaProperties;
    }

    @Bean
    @ConditionalOnExpression("'${dew.cluster.mq}'=='kafka'")
    public KafkaAdapter kafkaAdapter(){
        return new KafkaAdapter(kafkaProperties);
    }

    @Bean
    @ConditionalOnBean(KafkaAdapter.class)
    public KafkaClusterMQ kafkaClusterMQ(KafkaAdapter kafkaAdapter){
        return new KafkaClusterMQ(kafkaAdapter);
    }
}
