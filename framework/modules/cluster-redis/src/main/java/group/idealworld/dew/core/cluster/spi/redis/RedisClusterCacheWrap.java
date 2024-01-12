package group.idealworld.dew.core.cluster.spi.redis;

import group.idealworld.dew.core.cluster.ClusterCache;
import group.idealworld.dew.core.cluster.ClusterCacheWrap;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 缓存服务多实例封装 Redis 实现.
 *
 * @author gudaoxuri
 */
public class RedisClusterCacheWrap implements ClusterCacheWrap {

    private static final ConcurrentHashMap<String, ClusterCache> CACHE_CONTAINER = new ConcurrentHashMap<>();

    RedisClusterCacheWrap(Map<String, RedisTemplate<String, String>> redisTemplates) {
        redisTemplates.forEach((k, v) -> CACHE_CONTAINER.put(k, new RedisClusterCache(v)));
    }

    @Override
    public ClusterCache instance(String key) {
        return CACHE_CONTAINER.get(key);
    }

    @Override
    public boolean exist(String key) {
        return CACHE_CONTAINER.contains(key);
    }
}
