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

package com.tairanchina.csp.dew.core.cluster.spi.rabbit;

import com.rabbitmq.client.*;
import com.tairanchina.csp.dew.core.cluster.AbsClusterMQ;
import org.springframework.amqp.rabbit.connection.Connection;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeoutException;
import java.util.function.Consumer;

public class RabbitClusterMQ extends AbsClusterMQ {

    private RabbitAdapter rabbitAdapter;

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

    public static void setSendBeforeFun(SendBeforeFun sendBeforeFun) {
        RabbitClusterMQ.sendBeforeFun = sendBeforeFun;
    }

    public static void setSendErrorFun(SendErrorFun sendErrorFun) {
        RabbitClusterMQ.sendErrorFun = sendErrorFun;
    }

    public static void setSendFinishFun(SendFinishFun sendFinishFun) {
        RabbitClusterMQ.sendFinishFun = sendFinishFun;
    }

    public static void setReceiveBeforeFun(ReceiveBeforeFun receiveBeforeFun) {
        RabbitClusterMQ.receiveBeforeFun = receiveBeforeFun;
    }

    public static void setReceiveErrorFun(ReceiveErrorFun receiveErrorFun) {
        RabbitClusterMQ.receiveErrorFun = receiveErrorFun;
    }

    public static void setReceiveFinishFun(ReceiveFinishFun receiveFinishFun) {
        RabbitClusterMQ.receiveFinishFun = receiveFinishFun;
    }

    public RabbitClusterMQ(RabbitAdapter rabbitAdapter) {
        this.rabbitAdapter = rabbitAdapter;
    }

    @Override
    public boolean doPublish(String topic, String message) {
        return doPublish(topic, message, true);
    }

    public boolean doPublish(String topic, String message, boolean confirm) {
        Connection connection = rabbitAdapter.getConnection();
        Channel channel = connection.createChannel(false);
        Object funResult = null;
        try {
            if (confirm) {
                channel.confirmSelect();
            }
            channel.exchangeDeclare(topic, "fanout", true);
            AMQP.BasicProperties properties = new AMQP.BasicProperties("text/plain",
                    null,
                    getMqHeader(topic),
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

    @Override
    protected void doSubscribe(String topic, Consumer<String> consumer) {
        Channel channel = rabbitAdapter.getConnection().createChannel(false);
        try {
            channel.exchangeDeclare(topic, "fanout", true);
            String queueName = channel.queueDeclare().getQueue();
            channel.queueBind(queueName, topic, "");
            channel.basicQos(1);
            channel.basicConsume(queueName, false, getDefaultConsumer(channel, topic, topic, "", queueName, consumer));
        } catch (IOException e) {
            logger.error("[MQ] Rabbit response error.", e);
        }
    }

    @Override
    protected boolean doRequest(String address, String message) {
        return doRequest(address, message, true);
    }

    public boolean doRequest(String address, String message, boolean confirm) {
        Connection connection = rabbitAdapter.getConnection();
        Channel channel = connection.createChannel(false);
        Object funResult = null;
        try {
            if (confirm) {
                channel.confirmSelect();
            }
            channel.queueDeclare(address, true, false, false, null);
            AMQP.BasicProperties properties = new AMQP.BasicProperties("text/plain",
                    null,
                    getMqHeader(address),
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

    public boolean publishWithTopic(String topic, String routingKey, String queueName, String message, boolean confirm) {
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
            AMQP.BasicProperties properties = new AMQP.BasicProperties("text/plain",
                    null,
                    getMqHeader(topic),
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

    public void subscribeWithTopic(String topic, String routingKey, String queueName, Consumer<String> consumer) {
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
    protected void doResponse(String address, Consumer<String> consumer) {
        Channel channel = rabbitAdapter.getConnection().createChannel(false);
        try {
            channel.queueDeclare(address, true, false, false, null);
            channel.basicQos(1);
            channel.basicConsume(address, false, getDefaultConsumer(channel, address, "", address, address, consumer));
        } catch (IOException e) {
            logger.error("[MQ] Rabbit response error.", e);
        }
    }

    private DefaultConsumer getDefaultConsumer(Channel channel, String flag, String exchange, String routingKey, String queueName, Consumer<String> consumer) {
        return new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                setMqHeader(flag, properties.getHeaders());
                String message = new String(body, StandardCharsets.UTF_8);
                Object funResult = receiveBeforeFun.invoke(exchange, routingKey, queueName, properties);
                try {
                    consumer.accept(message);
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

}
