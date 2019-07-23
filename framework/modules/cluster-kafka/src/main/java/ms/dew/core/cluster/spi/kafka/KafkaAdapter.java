package ms.dew.core.cluster.spi.kafka;

import org.apache.kafka.clients.producer.Producer;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;

/**
 * Created on 2019/5/9.
 *
 * @param <T> the type parameter
 * @param <B> the type parameter
 * @author 迹_Jason
 */
public class KafkaAdapter<T, B> {

    private Producer<T, B> producer;
    private ConcurrentKafkaListenerContainerFactory<T, B> consumer;

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
