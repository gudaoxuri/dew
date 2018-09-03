package com.tairanchina.csp.dew.core.cluster.spi.rabbit;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
import com.tairanchina.csp.dew.core.cluster.ClusterMQ;
import org.springframework.amqp.rabbit.connection.Connection;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeoutException;
import java.util.function.Consumer;

public class RabbitClusterMQ implements ClusterMQ {

    private RabbitAdapter rabbitAdapter;

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
    public void doSubscribe(String topic, Consumer<String> consumer) {
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

    @Override
    public boolean doRequest(String address, String message) {
        return doRequest(address, message, true);
    }

    public boolean doRequest(String address, String message, boolean confirm) {
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
    public void doResponse(String address, Consumer<String> consumer) {
        Channel channel = rabbitAdapter.getConnection().createChannel(false);
        try {
            channel.queueDeclare(address, true, false, false, null);
            channel.basicQos(1);
            channel.basicConsume(address, false, getDefaultConsumer(channel, address, consumer));
        } catch (IOException e) {
            logger.error("[MQ] Rabbit response error.", e);
        }
    }

    private DefaultConsumer getDefaultConsumer(Channel channel, String topic, Consumer<String> consumer) {
        return new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                setMQHeader(topic, properties.getHeaders());
                String message = new String(body, StandardCharsets.UTF_8);
                consumer.accept(message);
                channel.basicAck(envelope.getDeliveryTag(), false);
            }
        };
    }

}
