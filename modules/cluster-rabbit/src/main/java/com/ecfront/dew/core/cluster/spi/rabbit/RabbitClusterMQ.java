package com.ecfront.dew.core.cluster.spi.rabbit;

import com.rabbitmq.client.*;
import com.ecfront.dew.core.cluster.ClusterMQ;
import org.springframework.amqp.rabbit.connection.Connection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.concurrent.TimeoutException;
import java.util.function.Consumer;

@Component
@ConditionalOnBean(RabbitAdapter.class)
public class RabbitClusterMQ implements ClusterMQ {

    @Autowired
    private RabbitAdapter rabbitAdapter;

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
            channel.exchangeDeclare(topic, "fanout",true);
            channel.basicPublish(topic, "", MessageProperties.PERSISTENT_TEXT_PLAIN, message.getBytes());
            if (confirm) {
                try {
                    return channel.waitForConfirms();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    logger.error("[MQ] Rabbit publish error.", e);
                    return false;
                }
            }else{
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
            channel.exchangeDeclare(topic, "fanout",true);
            String queueName = channel.queueDeclare().getQueue();
            channel.queueBind(queueName, topic, "");
            channel.basicQos(1);
            channel.basicConsume(queueName, false, new DefaultConsumer(channel) {
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
            });
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
            }else{
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

}
