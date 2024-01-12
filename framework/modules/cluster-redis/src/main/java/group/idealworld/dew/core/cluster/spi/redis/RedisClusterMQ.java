package group.idealworld.dew.core.cluster.spi.redis;

import com.ecfront.dew.common.exception.RTUnsupportedEncodingException;
import group.idealworld.dew.core.cluster.AbsClusterMQ;
import group.idealworld.dew.core.cluster.dto.MessageWrap;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;

/**
 * MQ服务 Redis 实现.
 *
 * @author gudaoxuri
 */
public class RedisClusterMQ extends AbsClusterMQ {

    private RedisTemplate<String, String> redisTemplate;

    /**
     * Instantiates a new Redis cluster mq.
     *
     * @param redisTemplate the redis template
     */
    RedisClusterMQ(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    protected boolean doPublish(String topic, String message, Optional<Map<String, Object>> header, boolean confirm) {
        if (confirm) {
            throw new RTUnsupportedEncodingException("Redis doesn't support confirm mode");
        }
        redisTemplate.execute((RedisCallback<Void>) connection -> {
            connection.publish(topic.getBytes(), message.getBytes());
            return null;
        });
        return true;
    }

    @Override
    protected void doSubscribe(String topic, Consumer<MessageWrap> consumer) {
        new Thread(() -> redisTemplate.execute((RedisCallback<Void>) connection -> {
            connection.subscribe(
                    (message, pattern) -> consumer
                            .accept(new MessageWrap(topic, new String(message.getBody(), StandardCharsets.UTF_8))),
                    topic.getBytes());
            return null;
        })).start();
    }

    @Override
    protected boolean doRequest(String address, String message, Optional<Map<String, Object>> header, boolean confirm) {
        if (confirm) {
            throw new RTUnsupportedEncodingException("Redis doesn't support confirm mode");
        }
        redisTemplate.execute((RedisCallback<Void>) connection -> {
            connection.lPush(address.getBytes(), message.getBytes());
            return null;
        });
        return true;
    }

    @Override
    protected void doResponse(String address, Consumer<MessageWrap> consumer) {
        new Thread(() -> redisTemplate.execute((RedisCallback<Void>) connection -> {
            while (!connection.isClosed()) {
                List<byte[]> messages = connection.bRPop(30, address.getBytes());
                if (messages == null) {
                    continue;
                }
                String message = new String(messages.get(1), StandardCharsets.UTF_8);
                consumer.accept(new MessageWrap(address, message));
            }
            return null;
        })).start();
    }

    @Override
    public boolean supportHeader() {
        return false;
    }
}
