/*
 * Copyright 2019. the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ms.dew.core.cluster.spi.kafka;

import io.opentracing.Tracer;
import io.opentracing.contrib.kafka.spring.TracingConsumerFactory;
import io.opentracing.contrib.kafka.spring.TracingProducerFactory;
import org.apache.kafka.clients.KafkaClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.*;

/**
 * The type Kafka auto configuration.
 *
 * @author 迹_Jason
 */
@Configuration
@ConditionalOnClass(KafkaClient.class)
@ConditionalOnExpression("#{'${dew.cluster.cache}'=='kafka' "
        + "|| '${dew.cluster.mq}'=='kafka' "
        + "|| '${dew.cluster.lock}'=='kafka' "
        + "|| '${dew.cluster.map}'=='kafka' "
        + "|| '${dew.cluster.election}'=='kafka'}")
public class KafkaAutoConfiguration {

    @Autowired
    private KafkaProperties kafkaProperties;

    @Autowired(required = false)
    private Tracer tracer;

    /**
     * Consumer factory consumer factory.
     *
     * @return the consumer factory
     */
    @Bean
    public ConsumerFactory<String, String> consumerFactory() {
        if (tracer == null) {
            return new DefaultKafkaConsumerFactory<>(kafkaProperties.buildConsumerProperties());
        } else {
            return new TracingConsumerFactory<>(new DefaultKafkaConsumerFactory<>(kafkaProperties.buildConsumerProperties()), tracer);
        }
    }

    /**
     * Producer factory producer factory.
     *
     * @return the producer factory
     */
    @Bean
    public ProducerFactory<String, String> producerFactory() {
        if (tracer == null) {
            return new DefaultKafkaProducerFactory<>(kafkaProperties.buildProducerProperties());
        } else {
            return new TracingProducerFactory<>(new DefaultKafkaProducerFactory<>(kafkaProperties.buildProducerProperties()), tracer);
        }
    }


    /**
     * Kafka adapter kafka adapter.
     *
     * @param producerFactory the producer factory
     * @param consumerFactory the consumer factory
     * @return the kafka adapter
     */
    @Bean
    public KafkaAdapter<String, String> kafkaAdapter(ProducerFactory<String, String> producerFactory,
                                                     ConcurrentKafkaListenerContainerFactory<String, String> consumerFactory) {
        return new KafkaAdapter<>(producerFactory.createProducer(), consumerFactory);
    }

    /**
     * Kafka cluster mq kafka cluster mq.
     *
     * @param kafkaAdapter the kafka adapter
     * @return the kafka cluster mq
     */
    @Bean
    public KafkaClusterMQ kafkaClusterMQ(KafkaAdapter<String, String> kafkaAdapter) {
        return new KafkaClusterMQ(kafkaAdapter);
    }

    /**
     * Kafka template kafka template.
     *
     * @return the kafka template
     */
    @Bean
    public KafkaTemplate<String, String> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }


    /**
     * Kafka listener container factory concurrent kafka listener container factory.
     *
     * @return the concurrent kafka listener container factory
     */
    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, String> kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, String> containerFactory = new ConcurrentKafkaListenerContainerFactory<>();
        containerFactory.setConsumerFactory(consumerFactory());
        containerFactory.setConcurrency(3);
        containerFactory.getContainerProperties().setPollTimeout(3000);
        return containerFactory;
    }

}
