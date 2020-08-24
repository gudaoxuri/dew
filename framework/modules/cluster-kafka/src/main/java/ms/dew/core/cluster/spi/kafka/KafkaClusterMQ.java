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

import ms.dew.core.cluster.AbsClusterMQ;
import ms.dew.core.cluster.dto.MessageWrap;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.Header;
import org.apache.kafka.common.header.Headers;
import org.apache.kafka.common.header.internals.RecordHeader;
import org.apache.kafka.common.header.internals.RecordHeaders;
import org.springframework.util.StringUtils;

import java.time.Duration;
import java.util.*;
import java.util.function.Consumer;

/**
 * The type Kafka cluster mq.
 *
 * @author 迹_Jason.
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

    /**
     * Instantiates a new Kafka cluster mq.
     *
     * @param kafkaAdapter the kafka adapter
     */
    public KafkaClusterMQ(KafkaAdapter<String, String> kafkaAdapter) {
        this.kafkaAdapter = kafkaAdapter;
    }

    /**
     * Gets send ack fun.
     *
     * @return the send ack fun
     */
    public static Callback getSendAckFun() {
        return sendAckFun;
    }

    /**
     * Sets send ack fun.
     *
     * @param sendAckFun the send ack fun
     */
    public static void setSendAckFun(Callback sendAckFun) {
        KafkaClusterMQ.sendAckFun = sendAckFun;
    }

    /**
     * Gets poll.
     *
     * @return the poll
     */
    public Duration getPoll() {
        return poll;
    }

    /**
     * Sets poll.
     *
     * @param poll the poll
     */
    public void setPoll(Duration poll) {
        this.poll = poll;
    }

    /**
     * Kafka Consumer.
     *
     * @param topics   topics
     * @param consumer the Consumer Object
     */
    private void consumer(String topics, Consumer<MessageWrap> consumer) {
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
                    MessageWrap message = new MessageWrap();
                    message.setBody(rd.value());
                    message.setName(rd.topic());
                    message.setHeader(convertHeader(rd.headers()));
                    consumer.accept(message);
                });
                kafkaConsumer.commitAsync();
            }
        } finally {
            kafkaConsumer.close();
        }
    }

    /**
     * MQ 发布订阅模式 之 发布.
     * <p>
     * 请确保发布之前 topic 已经存在
     *
     * @param topic   主题
     * @param message 消息内容
     * @param header  消息头
     * @param confirm 是否需要确认
     * @return 是否发布成功，此返回值仅在 confirm 模式下才能保证严格准确！
     */
    @Override
    protected boolean doPublish(String topic, String message, Optional<Map<String, Object>> header, boolean confirm) {
        kafkaAdapter.producer().send(new ProducerRecord<>(topic, null, null, null, message, convertHeader(header)), sendAckFun);
        return true;
    }

    /**
     * Map => Headers.
     *
     * @param header Map Object
     * @return Headers
     */
    private Headers convertHeader(Optional<Map<String, Object>> header) {
        if (header.isPresent()) {
            Headers headers = new RecordHeaders();
            header.get().forEach((k, v) -> {
                Header h = new RecordHeader(k, v.toString().getBytes());
                headers.add(h);
            });
            return headers;
        }
        return null;
    }

    /**
     * Headers => Map.
     *
     * @param header Headers Object
     * @return Map
     */
    private Optional<Map<String, Object>> convertHeader(Headers header) {
        if (header != null) {
            Map<String, Object> headers = new HashMap<>();
            header.forEach(k -> headers.put(k.key(), new String(k.value())));
            return Optional.of(headers);
        }
        return Optional.empty();
    }

    /**
     * Kafka 发布订阅模式 之 订阅.
     * <p>
     * 非阻塞方式
     *
     * @param topic    主题
     * @param consumer 订阅处理方法
     */
    @Override
    protected void doSubscribe(String topic, Consumer<MessageWrap> consumer) {
        consumer(topic, consumer);
    }

    /**
     * Kafka 请求响应模式 之 请求.
     *
     * @param topic   主题
     * @param message 消息内容
     * @param header  消息头
     * @param confirm 是否需要确认
     * @return 是否发布成功，此返回值仅在 confirm 模式下才能保证严格准确！
     */
    @Override
    protected boolean doRequest(String topic, String message, Optional<Map<String, Object>> header, boolean confirm) {
        kafkaAdapter.producer().send(new ProducerRecord<>(topic, null, null, null, message, convertHeader(header)), sendAckFun);
        return true;
    }

    /**
     * Kafka 请求响应模式 之 响应.
     * <p>
     * 非阻塞方式
     *
     * @param topic    主题
     * @param consumer 响应处理方法
     */
    @Override
    protected void doResponse(String topic, Consumer<MessageWrap> consumer) {
        consumer(topic, consumer);
    }

    /**
     * Is header support.
     *
     * @return the result
     */
    @Override
    public boolean supportHeader() {
        return true;
    }
}
