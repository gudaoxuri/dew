package com.tairanchina.csp.dew.core.cluster.spi.kafka;

import com.tairanchina.csp.dew.core.cluster.ClusterMQ;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;

import java.util.Collections;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

/**
 * desription:
 * Created by ding on 2017/11/9.
 */
public class KafkaClusterMQ implements ClusterMQ {

    private KafkaAdapter kafkaAdapter;

    private final ExecutorService executorService = Executors.newCachedThreadPool();

    public KafkaClusterMQ(KafkaAdapter kafkaAdapter) {
        this.kafkaAdapter = kafkaAdapter;
    }

    @Override
    public boolean publish(String topic, String message) {
        return publish(topic, message, false);
    }

    public boolean publish(String topic, String message, boolean confirm) {
        try {
            if (confirm) {
                kafkaAdapter.getKafkaProducer().send(new ProducerRecord<>(topic, message), new KafkaCallBack(message));
            } else {
                kafkaAdapter.getKafkaProducer().send(new ProducerRecord<>(topic, message));
            }
        } catch (Exception e) {
            logger.error("[MQ] Kafka send error. record:  " + message, e);
            return false;
        }
        return true;
    }

    @Override
    public void subscribe(String topic, Consumer<String> consumer) {
        new Thread(() -> {
            KafkaConsumer<String, String> kafkaConsumer = kafkaAdapter.getKafkaConsumer(true);
            kafkaConsumer.subscribe(Collections.singletonList(topic));
            while (true) {
                ConsumerRecords<String, String> records = kafkaConsumer.poll(1000);
                executorService.execute(() -> records.forEach(record -> consumer.accept(record.value())));
            }
        }).start();
    }

    @Override
    public boolean request(String topic, String message) {
        return request(topic, message, false);
    }

    public boolean request(String topic, String message, boolean confirm) {
        return publish(topic, message, confirm);
    }

    @Override
    public void response(String topic, Consumer<String> consumer) {
        new Thread(() -> {
            KafkaConsumer<String, String> kafkaConsumer = kafkaAdapter.getKafkaConsumer(false);
            kafkaConsumer.subscribe(Collections.singletonList(topic));
            while (true) {
                ConsumerRecords<String, String> records = kafkaConsumer.poll(1000);
                executorService.execute(() -> records.forEach(record -> consumer.accept(record.value())));
            }
        }).start();
    }

    @Override
    public void responseAsyn(String address, int threadNum, Consumer<String> consumer, Consumer<Exception> failed) {

    }

    class KafkaCallBack implements Callback {

        private String message;

        public KafkaCallBack(String message) {
            this.message = message;
        }

        @Override
        public void onCompletion(RecordMetadata metadata, Exception exception) {
            if (exception != null) {
                logger.error("[MQ] Kafka callback failed , record:  " + message, metadata, exception);
            }
        }
    }

    public KafkaClusterMQ setKafkaAdapter(KafkaAdapter kafkaAdapter) {
        this.kafkaAdapter = kafkaAdapter;
        return this;
    }
}
