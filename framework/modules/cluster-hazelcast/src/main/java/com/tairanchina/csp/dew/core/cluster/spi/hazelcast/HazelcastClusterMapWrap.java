package com.tairanchina.csp.dew.core.cluster.spi.hazelcast;

import com.tairanchina.csp.dew.core.cluster.ClusterMap;
import com.tairanchina.csp.dew.core.cluster.ClusterMapWrap;

import java.util.concurrent.ConcurrentHashMap;

public class HazelcastClusterMapWrap implements ClusterMapWrap {

    private static final ConcurrentHashMap<String, ClusterMap> MAP_CONTAINER = new ConcurrentHashMap<>();

    private HazelcastAdapter hazelcastAdapter;

    public HazelcastClusterMapWrap(HazelcastAdapter hazelcastAdapter) {
        this.hazelcastAdapter = hazelcastAdapter;
    }

    @Override
    public <M> ClusterMap<M> instance(String key, Class<M> clazz) {
        MAP_CONTAINER.putIfAbsent(key, new HazelcastClusterMap<M>(key, hazelcastAdapter.getHazelcastInstance()));
        return MAP_CONTAINER.get(key);
    }

}
