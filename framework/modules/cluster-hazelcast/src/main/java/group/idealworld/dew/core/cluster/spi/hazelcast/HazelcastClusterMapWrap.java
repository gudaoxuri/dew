package group.idealworld.dew.core.cluster.spi.hazelcast;

import group.idealworld.dew.core.cluster.ClusterMap;
import group.idealworld.dew.core.cluster.ClusterMapWrap;

import java.util.concurrent.ConcurrentHashMap;

/**
 * 分布式Map服务多实例封装 Hazelcast 实现.
 *
 * @author gudaoxuri
 */
public class HazelcastClusterMapWrap implements ClusterMapWrap {

    private static final ConcurrentHashMap<String, ClusterMap> MAP_CONTAINER = new ConcurrentHashMap<>();

    private HazelcastAdapter hazelcastAdapter;

    /**
     * Instantiates a new Hazelcast cluster map wrap.
     *
     * @param hazelcastAdapter the hazelcast adapter
     */
    public HazelcastClusterMapWrap(HazelcastAdapter hazelcastAdapter) {
        this.hazelcastAdapter = hazelcastAdapter;
    }

    @Override
    public <M> ClusterMap<M> instance(String key, Class<M> clazz) {
        MAP_CONTAINER.putIfAbsent(key, new HazelcastClusterMap<M>(key, hazelcastAdapter.getHazelcastInstance()));
        return MAP_CONTAINER.get(key);
    }

}
