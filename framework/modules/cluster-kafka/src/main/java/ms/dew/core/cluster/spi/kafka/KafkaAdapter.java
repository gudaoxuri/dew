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

import org.apache.kafka.clients.producer.Producer;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;

/**
 * The type Kafka adapter.
 *
 * @param <T> the type parameter
 * @param <B> the type parameter
 * @author 迹_Jason
 */
public class KafkaAdapter<T, B> {

    private Producer<T, B> producer;
    private ConcurrentKafkaListenerContainerFactory<T, B> consumer;

    /**
     * Instantiates a new Kafka adapter.
     *
     * @param producer the producer
     * @param consumer the consumer
     */
    public KafkaAdapter(Producer<T, B> producer, ConcurrentKafkaListenerContainerFactory<T, B> consumer) {
        this.producer = producer;
        this.consumer = consumer;
    }

    /**
     * Producer producer.
     *
     * @return the producer
     */
    public Producer<T, B> producer() {
        return this.producer;
    }

    /**
     * Consumer consumer.
     *
     * @return the consumer
     */
    public ConcurrentKafkaListenerContainerFactory<T, B> consumer() {
        return this.consumer;
    }
}
