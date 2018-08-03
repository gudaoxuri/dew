package com.tairanchina.csp.dew.core.cluster.spi.rabbit;

import com.ecfront.dew.common.$;
import com.rabbitmq.client.*;
import com.tairanchina.csp.dew.core.cluster.ClusterMQ;
import com.tairanchina.csp.dew.core.h2.H2Utils;
import org.springframework.amqp.rabbit.connection.Connection;

import java.io.IOException;
import java.util.concurrent.TimeoutException;
import java.util.function.Consumer;

public class RabbitClusterMQ implements ClusterMQ {

    private RabbitAdapter rabbitAdapter;

    public RabbitClusterMQ(RabbitAdapter rabbitAdapter) {
        this.rabbitAdapter = rabbitAdapter;
    }

    @Override
    public boolean publish(String topic, String message) {
        return publish(topic, message, false);
    }

    public boolean publish(String topic, String message, boolean confirm) {
        logger.trace("[MQ] publish {}:{}", topic, message);
        Connection connection = rabbitAdapter.getConnection();
        Channel channel = connection.createChannel(false);
        try {
            if (confirm) {
                channel.confirmSelect();
            }
            channel.exchangeDeclare(topic, "fanout", true);
            AMQP.BasicProperties properties = new AMQP.BasicProperties("text/plain",
                    null,
                    getMQHeader(topic),
                    2,
                    0, null, null, null,
                    null, null, null, null,
                    null, null);
            channel.basicPublish(topic, "", properties, message.getBytes());
            if (confirm) {
                try {
                    return channel.waitForConfirms();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    logger.error("[MQ] Rabbit publish error.", e);
                    return false;
                }
            } else {
                return true;
            }
        } catch (IOException e) {
            logger.error("[MQ] Rabbit publish error.", e);
            return false;
        } finally {
            try {
                channel.close();
            } catch (IOException | TimeoutException e) {
                logger.error("[MQ] Rabbit publish error.", e);
            }
            connection.close();
        }
    }

    @Override
    public void subscribe(String topic, Consumer<String> consumer) {
        Channel channel = rabbitAdapter.getConnection().createChannel(false);
        try {
            channel.exchangeDeclare(topic, "fanout", true);
            String queueName = channel.queueDeclare().getQueue();
            channel.queueBind(queueName, topic, "");
            channel.basicQos(1);
            channel.basicConsume(queueName, false, getDefaultConsumer(channel, topic, consumer));
        } catch (IOException e) {
            logger.error("[MQ] Rabbit response error.", e);
        }
    }

    public boolean publishWithTopic(String topic, String routingKey, String queueName, String message, boolean confirm) {
        logger.trace("[MQ] publish {}:{}", topic, message);
        Connection connection = rabbitAdapter.getConnection();
        Channel channel = connection.createChannel(false);
        try {
            if (confirm) {
                channel.confirmSelect();
            }
            channel.queueDeclare(queueName, true, false, false, null);
            channel.exchangeDeclare(topic, BuiltinExchangeType.TOPIC, true);
            AMQP.BasicProperties properties = new AMQP.BasicProperties("text/plain",
                    null,
                    getMQHeader(topic),
                    2,
                    0, null, null, null,
                    null, null, null, null,
                    null, null);
            channel.basicPublish(topic, routingKey, properties, message.getBytes());
            if (confirm) {
                try {
                    return channel.waitForConfirms();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    logger.error("[MQ] Rabbit publish error.", e);
                    return false;
                }
            } else {
                return true;
            }
        } catch (IOException e) {
            logger.error("[MQ] Rabbit publish error.", e);
            return false;
        } finally {
            try {
                channel.close();
            } catch (IOException | TimeoutException e) {
                logger.error("[MQ] Rabbit publish error.", e);
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
            channel.basicConsume(queueName, false, getDefaultConsumer(channel, topic, consumer));
        } catch (IOException e) {
            logger.error("[MQ] Rabbit response error.", e);
        }
    }

    @Override
    public boolean request(String address, String message) {
        return request(address, message, false);
    }

    public boolean request(String address, String message, boolean confirm) {
        logger.trace("[MQ] request {}:{}", address, message);
        Connection connection = rabbitAdapter.getConnection();
        Channel channel = connection.createChannel(false);
        try {
            if (confirm) {
                channel.confirmSelect();
            }
            channel.queueDeclare(address, true, false, false, null);
            AMQP.BasicProperties properties = new AMQP.BasicProperties("text/plain",
                    null,
                    getMQHeader(address),
                    2,
                    0, null, null, null,
                    null, null, null, null,
                    null, null);
            channel.basicPublish("", address, properties, message.getBytes());
            if (confirm) {
                try {
                    return channel.waitForConfirms();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    logger.error("[MQ] Rabbit request error.", e);
                    return false;
                }
            } else {
                return true;
            }
        } catch (IOException e) {
            logger.error("[MQ] Rabbit request error.", e);
            return false;
        } finally {
            try {
                channel.close();
            } catch (IOException | TimeoutException e) {
                logger.error("[MQ] Rabbit request error.", e);
            }
            connection.close();
        }
    }

    @Override
    public void response(String address, Consumer<String> consumer) {
        new Thread(() -> {
            H2Utils.runH2Job(address, consumer);
            Channel channel = rabbitAdapter.getConnection().createChannel(false);
            try {
                channel.queueDeclare(address, true, false, false, null);
                channel.basicQos(1);
                channel.basicConsume(address, false, getDefaultConsumer(channel, address, consumer));
            } catch (IOException e) {
                logger.error("[MQ] Rabbit response error.", e);
            }
        }).start();
    }


    private DefaultConsumer getDefaultConsumer(Channel channel, String topic, Consumer<String> consumer) {
        return new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                setMQHeader(topic, properties.getHeaders());
                String message = new String(body, "UTF-8");
                logger.trace("[MQ] response/subscribe {}:{}", topic, message);
                try {
                    String uuid = $.field.createUUID();
                    H2Utils.createJob(topic, uuid, "RUNNING", message);
                    consumer.accept(message);
                    channel.basicAck(envelope.getDeliveryTag(), false);
                    H2Utils.deleteJob(uuid);
                } catch (Exception e) {
                    logger.error("[MQ] Rabbit response/subscribe error.", e);
                }
            }
        };
    }

}
