package com.tairanchina.csp.dew.core.cluster.spi.redis;

import com.tairanchina.csp.dew.core.cluster.ClusterMQ;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.List;
import java.util.function.Consumer;


public class RedisClusterMQ implements ClusterMQ {

    private RedisTemplate<String, String> redisTemplate;

    public RedisClusterMQ(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public boolean publish(String topic, String message) {
        logger.trace("[MQ] publish {}:{}", topic, message);
        redisTemplate.execute((RedisCallback<Void>) connection -> {
            connection.publish(topic.getBytes(), message.getBytes());
            return null;
        });
        return true;
    }

    @Override
    public void subscribe(String topic, Consumer<String> consumer) {
        new Thread(() -> redisTemplate.execute((RedisCallback<Void>) connection -> {
            connection.subscribe((message, pattern) -> {
                try {
                    String msg = new String(message.getBody(), "UTF-8");
                    logger.trace("[MQ] subscribe {}:{}", topic, msg);
                    consumer.accept(msg);
                } catch (Exception e) {
                    logger.error("Redis Subscribe error.", e);
                }
            }, topic.getBytes());
            return null;
        })).start();
    }

    @Override
    public boolean request(String address, String message) {
        logger.trace("[MQ] request {}:{}", address, message);
        redisTemplate.execute((RedisCallback<Void>) connection -> {
            connection.lPush(address.getBytes(), message.getBytes());
            return null;
        });
        return true;
    }

    @Override
    public void response(String address, Consumer<String> consumer) {
        new Thread(() -> redisTemplate.execute((RedisCallback<Void>) connection -> {
                    try {
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
                        logger.error("Redis Response error.", e);
                    }
                    return null;
                }
        )).start();
    }

    @Override
    public void responseAsyn(String address, int threadNum, Consumer<String> consumer, Consumer<Exception> failed) {

    }

}
