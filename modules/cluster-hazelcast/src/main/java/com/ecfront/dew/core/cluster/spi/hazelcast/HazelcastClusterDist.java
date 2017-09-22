package com.ecfront.dew.core.cluster.spi.hazelcast;

import com.ecfront.dew.core.cluster.ClusterDistMap;
import com.ecfront.dew.core.cluster.ClusterDist;
import com.ecfront.dew.core.cluster.ClusterDistLock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnBean(HazelcastAdapter.class)
public class HazelcastClusterDist implements ClusterDist {

    @Autowired
    private HazelcastAdapter hazelcastAdapter;

    @Override
    public ClusterDistLock lock(String key) {
        return new HazelcastClusterDistLock(key, hazelcastAdapter.getHazelcastInstance());
    }

    @Override
    public <M> ClusterDistMap<M> map(String key, Class<M> clazz) {
        return new HazelcastClusterDistMap<>(key, hazelcastAdapter.getHazelcastInstance());
    }

}
