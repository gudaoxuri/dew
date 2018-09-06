package com.tairanchina.csp.dew.core.cluster.spi.redis;

import com.tairanchina.csp.dew.core.cluster.ClusterMQ;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.function.Consumer;


public class RedisClusterMQ implements ClusterMQ {

    private RedisTemplate<String, String> redisTemplate;

    public RedisClusterMQ(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public boolean doPublish(String topic, String message) {
        redisTemplate.execute((RedisCallback<Void>) connection -> {
            connection.publish(topic.getBytes(), message.getBytes());
            return null;
        });
        return true;
    }

    @Override
    public void doSubscribe(String topic, Consumer<String> consumer) {
        new Thread(() -> redisTemplate.execute((RedisCallback<Void>) connection -> {
            connection.subscribe((message, pattern) ->
                            consumer.accept(new String(message.getBody(), StandardCharsets.UTF_8))
                    , topic.getBytes());
            return null;
        })).start();
    }

    @Override
    public boolean doRequest(String address, String message) {
        redisTemplate.execute((RedisCallback<Void>) connection -> {
            connection.lPush(address.getBytes(), message.getBytes());
            return null;
        });
        return true;
    }

    @Override
    public void doResponse(String address, Consumer<String> consumer) {
        new Thread(() -> redisTemplate.execute((RedisCallback<Void>) connection -> {
            while (!connection.isClosed()) {
                List<byte[]> messages = connection.bRPop(30, address.getBytes());
                if (messages == null) {
                    continue;
                }
                String message = new String(messages.get(1), StandardCharsets.UTF_8);
                consumer.accept(message);
            }
            return null;
        })).start();
    }
}
