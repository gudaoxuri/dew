package group.idealworld.dew.core.cluster.spi.redis;

import group.idealworld.dew.core.cluster.ClusterMap;
import group.idealworld.dew.core.cluster.ClusterMapWrap;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.concurrent.ConcurrentHashMap;

/**
 * 分布式Map服务多实例封装 Redis 实现.
 *
 * @author gudaoxuri
 */
public class RedisClusterMapWrap implements ClusterMapWrap {

    private static final ConcurrentHashMap<String, ClusterMap> MAP_CONTAINER = new ConcurrentHashMap<>();

    private RedisTemplate<String, String> redisTemplate;

    /**
     * Instantiates a new Redis cluster map wrap.
     *
     * @param redisTemplate the redis template
     */
    public RedisClusterMapWrap(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public <M> ClusterMap<M> instance(String key, Class<M> clazz) {
        MAP_CONTAINER.putIfAbsent(key, new RedisClusterMap<>(key, clazz, redisTemplate));
        return MAP_CONTAINER.get(key);
    }

}
