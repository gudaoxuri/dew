package com.tairanchina.csp.dew.core.cluster.spi.hazelcast;

import com.tairanchina.csp.dew.core.cluster.ClusterMap;
import com.tairanchina.csp.dew.core.cluster.ClusterMapWrap;

public class HazelcastClusterMapWrap implements ClusterMapWrap {

    private HazelcastAdapter hazelcastAdapter;

    public HazelcastClusterMapWrap(HazelcastAdapter hazelcastAdapter) {
        this.hazelcastAdapter = hazelcastAdapter;
    }

    @Override
    public <M> ClusterMap<M> instance(String key, Class<M> clazz) {
        return new HazelcastClusterMap<>(key, hazelcastAdapter.getHazelcastInstance());
    }

}
