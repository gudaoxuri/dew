package com.ecfront.dew.core.cluster.spi.redis;

import com.ecfront.dew.core.cluster.ClusterMQ;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.function.Consumer;

@Component
public class RedisClusterMQ implements ClusterMQ {

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Override
    public void publish(String topic, String message) {
        logger.trace("[MQ] publish {}:{}", topic, message);
        RedisConnection connection = null;
        try {
            connection = redisTemplate.getConnectionFactory().getConnection();
            connection.publish(topic.getBytes(), message.getBytes());
        } finally {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        }
    }

    @Override
    public void subscribe(String topic, Consumer<String> consumer) {
        RedisConnection connection = null;
        try {
            connection = redisTemplate.getConnectionFactory().getConnection();
            connection.subscribe((message, pattern) -> {
                try {
                    String msg = new String(message.getBody(), "UTF-8");
                    logger.trace("[MQ] subscribe {}:{}", topic, msg);
                    consumer.accept(msg);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }, topic.getBytes());
        } finally {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        }
    }

    @Override
    public void request(String address, String message) {
        logger.trace("[MQ] request {}:{}", address, message);
        RedisConnection connection = null;
        try {
            connection = redisTemplate.getConnectionFactory().getConnection();
            connection.lPush(address.getBytes(), message.getBytes());
        } finally {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        }
    }

    @Override
    public void response(String address, Consumer<String> consumer) {
        new Thread(() -> {
            RedisConnection connection = null;
            try {
                connection = redisTemplate.getConnectionFactory().getConnection();
                while (!connection.isClosed()) {
                    List<byte[]> messages = connection.bRPop(30, address.getBytes());
                    if (messages == null) {
                        continue;
                    }
                    String message = new String(messages.get(1), "UTF-8");
                    logger.trace("[MQ] response {}:{}", address, message);
                    consumer.accept(message);
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (connection != null && !connection.isClosed()) {
                    connection.close();
                }
            }
        }).start();
    }

}
