package com.tairanchina.csp.dew.core.cluster.spi.rabbit;

import com.rabbitmq.client.*;
import com.tairanchina.csp.dew.core.cluster.ClusterMQ;
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
            channel.basicPublish(topic, "", MessageProperties.PERSISTENT_TEXT_PLAIN, message.getBytes());
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
            channel.basicPublish(topic, routingKey, MessageProperties.PERSISTENT_TEXT_PLAIN, message.getBytes());
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

    public void subscribewithTopic(String topic, String routingKey, String queueName, Consumer<String> consumer) {
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
            channel.basicPublish("", address, MessageProperties.PERSISTENT_TEXT_PLAIN, message.getBytes());
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
        Channel channel = rabbitAdapter.getConnection().createChannel(false);
        try {
            channel.queueDeclare(address, true, false, false, null);
            channel.basicQos(1);
            channel.basicConsume(address, false, new DefaultConsumer(channel) {
                @Override
                public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                    String message = new String(body, "UTF-8");
                    logger.trace("[MQ] response {}:{}", address, message);
                    try {
                        consumer.accept(message);
                        channel.basicAck(envelope.getDeliveryTag(), false);
                    } catch (Exception e) {
                        logger.error("[MQ] Rabbit response error.", e);
                    }
                }
            });
        } catch (IOException e) {
            logger.error("[MQ] Rabbit response error.", e);
        }
    }

    public DefaultConsumer getDefaultConsumer(Channel channel, String topic, Consumer<String> consumer) {
        return new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                String message = new String(body, "UTF-8");
                logger.trace("[MQ] subscribe {}:{}", topic, message);
                try {
                    consumer.accept(message);
                    channel.basicAck(envelope.getDeliveryTag(), false);
                } catch (Exception e) {
                    logger.error("[MQ] Rabbit subscribe error.", e);
                }
            }
        };
    }

}
