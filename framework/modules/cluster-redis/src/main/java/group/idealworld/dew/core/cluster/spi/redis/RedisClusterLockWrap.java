package group.idealworld.dew.core.cluster.spi.redis;

import group.idealworld.dew.core.cluster.ClusterLock;
import group.idealworld.dew.core.cluster.ClusterLockWrap;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.concurrent.ConcurrentHashMap;

/**
 * 分布式锁服务多实例封装 Redis 实现.
 *
 * @author gudaoxuri
 */
public class RedisClusterLockWrap implements ClusterLockWrap {

    private static final ConcurrentHashMap<String, ClusterLock> LOCK_CONTAINER = new ConcurrentHashMap<>();

    private RedisTemplate<String, String> redisTemplate;

    /**
     * Instantiates a new Redis cluster lock wrap.
     *
     * @param redisTemplate the redis template
     */
    public RedisClusterLockWrap(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public ClusterLock instance(String key) {
        LOCK_CONTAINER.putIfAbsent(key, new RedisClusterLock(key, redisTemplate));
        return LOCK_CONTAINER.get(key);
    }

}
