package group.idealworld.dew.core.cluster.spi.hazelcast;

import group.idealworld.dew.core.cluster.ClusterLock;
import group.idealworld.dew.core.cluster.ClusterLockWrap;

import java.util.concurrent.ConcurrentHashMap;

/**
 * 分布式锁服务多实例封装 Hazelcast 实现.
 *
 * @author gudaoxuri
 */
public class HazelcastClusterLockWrap implements ClusterLockWrap {

    private static final ConcurrentHashMap<String, ClusterLock> LOCK_CONTAINER = new ConcurrentHashMap<>();

    private HazelcastAdapter hazelcastAdapter;

    /**
     * Instantiates a new Hazelcast cluster lock wrap.
     *
     * @param hazelcastAdapter the hazelcast adapter
     */
    public HazelcastClusterLockWrap(HazelcastAdapter hazelcastAdapter) {
        this.hazelcastAdapter = hazelcastAdapter;
    }

    @Override
    public ClusterLock instance(String key) {
        LOCK_CONTAINER.putIfAbsent(key, new HazelcastClusterLock(key, hazelcastAdapter.getHazelcastInstance()));
        return LOCK_CONTAINER.get(key);
    }

}
