/*
 * Copyright 2022. the original author or authors
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

package group.idealworld.dew.core.cluster.spi.rabbit;

import com.rabbitmq.client.*;
import group.idealworld.dew.core.cluster.AbsClusterMQ;
import group.idealworld.dew.core.cluster.dto.MessageWrap;
import org.springframework.amqp.rabbit.connection.Connection;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeoutException;
import java.util.function.Consumer;

/**
 * MQ服务 Rabbit 实现.
 *
 * @author gudaoxuri
 */
public class RabbitClusterMQ extends AbsClusterMQ {

    private static SendBeforeFun sendBeforeFun = (exchange, routingKey, messageProperties) -> null;
    private static SendErrorFun sendErrorFun = (ex, beforeResult) -> {
    };
    private static SendFinishFun sendFinishFun = beforeResult -> {
    };
    private static ReceiveBeforeFun receiveBeforeFun = (exchange, routingKey, queueName, messageProperties) -> null;
    private static ReceiveErrorFun receiveErrorFun = (ex, beforeResult) -> {
    };
    private static ReceiveFinishFun receiveFinishFun = beforeResult -> {
    };
    private RabbitAdapter rabbitAdapter;

    /**
     * Instantiates a new Rabbit cluster mq.
     *
     * @param rabbitAdapter the rabbit adapter
     */
    public RabbitClusterMQ(RabbitAdapter rabbitAdapter) {
        this.rabbitAdapter = rabbitAdapter;
    }

    /**
     * Sets send before fun.
     *
     * @param sendBeforeFun the send before fun
     */
    public static void setSendBeforeFun(SendBeforeFun sendBeforeFun) {
        RabbitClusterMQ.sendBeforeFun = sendBeforeFun;
    }

    /**
     * Sets send error fun.
     *
     * @param sendErrorFun the send error fun
     */
    public static void setSendErrorFun(SendErrorFun sendErrorFun) {
        RabbitClusterMQ.sendErrorFun = sendErrorFun;
    }

    /**
     * Sets send finish fun.
     *
     * @param sendFinishFun the send finish fun
     */
    public static void setSendFinishFun(SendFinishFun sendFinishFun) {
        RabbitClusterMQ.sendFinishFun = sendFinishFun;
    }

    /**
     * Sets receive before fun.
     *
     * @param receiveBeforeFun the receive before fun
     */
    public static void setReceiveBeforeFun(ReceiveBeforeFun receiveBeforeFun) {
        RabbitClusterMQ.receiveBeforeFun = receiveBeforeFun;
    }

    /**
     * Sets receive error fun.
     *
     * @param receiveErrorFun the receive error fun
     */
    public static void setReceiveErrorFun(ReceiveErrorFun receiveErrorFun) {
        RabbitClusterMQ.receiveErrorFun = receiveErrorFun;
    }

    /**
     * Sets receive finish fun.
     *
     * @param receiveFinishFun the receive finish fun
     */
    public static void setReceiveFinishFun(ReceiveFinishFun receiveFinishFun) {
        RabbitClusterMQ.receiveFinishFun = receiveFinishFun;
    }

    /**
     * MQ 发布订阅模式 之 发布.
     * <p>
     * exchange = fanout
     * 请确保发布之前 topic 已经存在
     *
     * @param topic   主题
     * @param message 消息内容
     * @param header  消息头
     * @param confirm 是否需要确认
     * @return 是否发布成功，此返回值仅在 confirm 模式下才能保证严格准确！
     */
    @Override
    public boolean doPublish(String topic, String message, Optional<Map<String, Object>> header, boolean confirm) {
        Connection connection = rabbitAdapter.getConnection();
        Channel channel = connection.createChannel(false);
        Object funResult = null;
        try {
            if (confirm) {
                channel.confirmSelect();
            }
            channel.exchangeDeclare(topic, BuiltinExchangeType.FANOUT, true);
            Map<String, Object> sendHeader = getMQHeader(topic);
            header.ifPresent(sendHeader::putAll);
            AMQP.BasicProperties properties = new AMQP.BasicProperties("text/plain",
                    null,
                    sendHeader,
                    2,
                    0, null, null, null,
                    null, null, null, null,
                    null, null);
            funResult = sendBeforeFun.invoke(topic, "", properties);
            channel.basicPublish(topic, "", properties, message.getBytes());
            if (confirm) {
                try {
                    return channel.waitForConfirms();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    logger.error("[MQ] Rabbit publish error.", e);
                    sendErrorFun.invoke(e, funResult);
                    return false;
                }
            } else {
                return true;
            }
        } catch (IOException e) {
            logger.error("[MQ] Rabbit publish error.", e);
            sendErrorFun.invoke(e, funResult);
            return false;
        } finally {
            try {
                channel.close();
                sendFinishFun.invoke(funResult);
            } catch (IOException | TimeoutException e) {
                logger.error("[MQ] Rabbit publish error.", e);
            }
            connection.close();
        }
    }

    /**
     * MQ 发布订阅模式 之 订阅.
     * <p>
     * exchange = fanout
     * 非阻塞方式
     *
     * @param topic    主题
     * @param consumer 订阅处理方法
     */
    @Override
    protected void doSubscribe(String topic, Consumer<MessageWrap> consumer) {
        Channel channel = rabbitAdapter.getConnection().createChannel(false);
        try {
            channel.exchangeDeclare(topic, BuiltinExchangeType.FANOUT, true);
            String queueName = channel.queueDeclare().getQueue();
            channel.queueBind(queueName, topic, "");
            channel.basicQos(1);
            channel.basicConsume(queueName, false, getDefaultConsumer(channel, topic, topic, "", queueName, consumer));
        } catch (IOException e) {
            logger.error("[MQ] Rabbit response error.", e);
        }
    }

    /**
     * MQ 请求响应模式 之 请求.
     * <p>
     * exchange = fanout
     *
     * @param address 请求地址
     * @param message 消息内容
     * @param header  消息头
     * @param confirm 是否需要确认
     * @return 是否发布成功，此返回值仅在 confirm 模式下才能保证严格准确！
     */
    public boolean doRequest(String address, String message, Optional<Map<String, Object>> header, boolean confirm) {
        Connection connection = rabbitAdapter.getConnection();
        Channel channel = connection.createChannel(false);
        Object funResult = null;
        try {
            if (confirm) {
                channel.confirmSelect();
            }
            channel.queueDeclare(address, true, false, false, null);
            Map<String, Object> sendHeader = getMQHeader(address);
            header.ifPresent(sendHeader::putAll);
            AMQP.BasicProperties properties = new AMQP.BasicProperties("text/plain",
                    null,
                    sendHeader,
                    2,
                    0, null, null, null,
                    null, null, null, null,
                    null, null);
            funResult = sendBeforeFun.invoke("", address, properties);
            channel.basicPublish("", address, properties, message.getBytes());
            if (confirm) {
                try {
                    return channel.waitForConfirms();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    logger.error("[MQ] Rabbit request error.", e);
                    sendErrorFun.invoke(e, funResult);
                    return false;
                }
            } else {
                return true;
            }
        } catch (IOException e) {
            logger.error("[MQ] Rabbit request error.", e);
            sendErrorFun.invoke(e, funResult);
            return false;
        } finally {
            try {
                channel.close();
                sendFinishFun.invoke(funResult);
            } catch (IOException | TimeoutException e) {
                logger.error("[MQ] Rabbit request error.", e);
            }
            connection.close();
        }
    }

    /**
     * MQ 发布订阅模式 之 发布.
     * <p>
     * exchange = topic
     *
     * @param topic      主题
     * @param routingKey 路由Key
     * @param queueName  队列名
     * @param message    消息内容
     * @param header     消息头
     * @param confirm    是否需要确认
     * @return 是否发布成功，此返回值仅在rabbit confirm 模式下才能保证严格准确！
     */
    public boolean publishWithTopic(String topic, String routingKey, String queueName, String message,
                                    Optional<Map<String, Object>> header, boolean confirm) {
        logger.trace("[MQ] publishWithTopic {}:{}", topic, message);
        Connection connection = rabbitAdapter.getConnection();
        Channel channel = connection.createChannel(false);
        Object funResult = null;
        try {
            if (confirm) {
                channel.confirmSelect();
            }
            channel.queueDeclare(queueName, true, false, false, null);
            channel.exchangeDeclare(topic, BuiltinExchangeType.TOPIC, true);
            Map<String, Object> sendHeader = getMQHeader(topic);
            header.ifPresent(sendHeader::putAll);
            AMQP.BasicProperties properties = new AMQP.BasicProperties("text/plain",
                    null,
                    sendHeader,
                    2,
                    0, null, null, null,
                    null, null, null, null,
                    null, null);
            funResult = sendBeforeFun.invoke(topic, routingKey, properties);
            channel.basicPublish(topic, routingKey, properties, message.getBytes());
            if (confirm) {
                try {
                    return channel.waitForConfirms();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    logger.error("[MQ] Rabbit publishWithTopic error.", e);
                    sendErrorFun.invoke(e, funResult);
                    return false;
                }
            } else {
                return true;
            }
        } catch (IOException e) {
            logger.error("[MQ] Rabbit publishWithTopic error.", e);
            sendErrorFun.invoke(e, funResult);
            return false;
        } finally {
            try {
                channel.close();
                sendFinishFun.invoke(funResult);
            } catch (IOException | TimeoutException e) {
                logger.error("[MQ] Rabbit publishWithTopic error.", e);
            }
            connection.close();
        }
    }

    /**
     * MQ 发布订阅模式 之 订阅.
     * <p>
     * exchange = topic
     * 非阻塞方式
     *
     * @param topic      主题
     * @param routingKey 路由Key
     * @param queueName  队列名
     * @param consumer   订阅处理方法
     */
    public void subscribeWithTopic(String topic, String routingKey, String queueName, Consumer<MessageWrap> consumer) {
        Channel channel = rabbitAdapter.getConnection().createChannel(false);
        try {
            channel.queueDeclare(queueName, true, false, false, null);
            channel.exchangeDeclare(topic, BuiltinExchangeType.TOPIC, true);
            channel.queueBind(queueName, topic, routingKey);
            channel.basicQos(1);
            channel.basicConsume(queueName, false, getDefaultConsumer(channel, topic, topic, routingKey, queueName, consumer));
        } catch (IOException e) {
            logger.error("[MQ] Rabbit subscribeWithTopic error.", e);
        }
    }

    @Override
    protected void doResponse(String address, Consumer<MessageWrap> consumer) {
        Channel channel = rabbitAdapter.getConnection().createChannel(false);
        try {
            channel.queueDeclare(address, true, false, false, null);
            channel.basicQos(1);
            channel.basicConsume(address, false, getDefaultConsumer(channel, address, "", address, address, consumer));
        } catch (IOException e) {
            logger.error("[MQ] Rabbit response error.", e);
        }
    }

    private DefaultConsumer getDefaultConsumer(Channel channel, String flag, String exchange, String routingKey, String queueName,
                                               Consumer<MessageWrap> consumer) {
        return new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                Object funResult = receiveBeforeFun.invoke(exchange, routingKey, queueName, properties);
                try {
                    Map<String, Object> receiveHeader = setMQHeader(flag, properties.getHeaders());
                    String message = new String(body, StandardCharsets.UTF_8);
                    consumer.accept(new MessageWrap(flag, Optional.of(receiveHeader), message));
                } catch (RuntimeException e) {
                    receiveErrorFun.invoke(e, funResult);
                    throw e;
                } finally {
                    channel.basicAck(envelope.getDeliveryTag(), false);
                    receiveFinishFun.invoke(funResult);
                }
            }
        };
    }

    @Override
    public boolean supportHeader() {
        return true;
    }
}
