package ms.dew.core.cluster.spi.kafka;

import ms.dew.core.cluster.AbsClusterMQ;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.util.StringUtils;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

/**
 * Created on 2019/5/9.
 *
 * @author 迹_Jason
 */
public class KafkaClusterMQ extends AbsClusterMQ {

    private static Callback sendAckFun = (metadata, exception) -> {
    };

    private final KafkaAdapter<String, String> kafkaAdapter;

    private Duration poll = Duration.ofMillis(100);

    /**
     * Instantiates a new Kafka cluster mq.
     *
     * @param kafkaAdapter the kafka adapter
     * @param poll         the poll
     */
    public KafkaClusterMQ(KafkaAdapter<String, String> kafkaAdapter, Duration poll) {
        this.kafkaAdapter = kafkaAdapter;
        if (!poll.isZero()) {
            this.poll = poll;
        }
    }

    public KafkaClusterMQ(KafkaAdapter<String, String> kafkaAdapter) {
        this.kafkaAdapter = kafkaAdapter;
    }

    public static Callback getSendAckFun() {
        return sendAckFun;
    }

    public static void setSendAckFun(Callback sendAckFun) {
        KafkaClusterMQ.sendAckFun = sendAckFun;
    }

    public Duration getPoll() {
        return poll;
    }

    public void setPoll(Duration poll) {
        this.poll = poll;
    }

    @Override
    protected boolean doPublish(String topic, String message) {
        kafkaAdapter.producer().send(new ProducerRecord<>(topic, message), sendAckFun);
        return true;
    }

    @Override
    protected void doSubscribe(String topics, Consumer<String> consumer) {
        consumer(topics, consumer);
    }

    @Override
    protected boolean doRequest(String topic, String message) {
        kafkaAdapter.producer().send(new ProducerRecord<>(topic, message), sendAckFun);
        return true;
    }

    @Override
    protected void doResponse(String topics, Consumer<String> consumer) {
        consumer(topics, consumer);
    }

    private void consumer(String topics, Consumer<String> consumer) {
        final List<String> topicList;
        if (StringUtils.hasLength(topics)) {
            topicList = Arrays.asList(topics.split(","));
        } else {
            topicList = new ArrayList<>();
        }

        final org.apache.kafka.clients.consumer.Consumer kafkaConsumer = kafkaAdapter.consumer().getConsumerFactory()
                .createConsumer(UUID.randomUUID().toString());
        try {
            kafkaConsumer.subscribe(topicList);
            while (true) {
                ConsumerRecords<String, String> records = kafkaConsumer.poll(poll);
                if (records.isEmpty()) {
                    return;
                }
                records.forEach(rd -> {
                    if (logger.isDebugEnabled()) {
                        logger.debug("Consumer Record partition:{} - offset:{}", rd.partition(), rd.offset());
                        logger.debug("Consumer Record value:{}", rd.value());
                    }
                    consumer.accept(rd.value());
                });
                kafkaConsumer.commitAsync();
            }
        } finally {
            kafkaConsumer.close();
        }
    }


}
