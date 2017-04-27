package com.ecfront.dew.core.cluster.spi.ignite;

import com.ecfront.dew.core.cluster.ClusterDistMap;
import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCache;

import javax.cache.Cache;
import java.util.Map;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class IgniteClusterDistMap<M> implements ClusterDistMap<M> {

    private IgniteCache<String, M> map;

    IgniteClusterDistMap(String mapKey, Ignite ignite) {
        map = ignite.getOrCreateCache("dew:dist:map:" + mapKey);
    }

    @Override
    public void put(String key, M value) {
        map.put(key, value);
    }

    @Override
    public void putAsync(String key, M value) {
        map.withAsync().put(key, value);
    }

    @Override
    public void putIfAbsent(String key, M value) {
        map.putIfAbsent(key, value);
    }

    @Override
    public boolean containsKey(String key) {
        return map.containsKey(key);
    }

    @Override
    public Map<String, M> getAll() {
        return StreamSupport.stream(Spliterators.spliteratorUnknownSize(map.iterator(), Spliterator.ORDERED), false)
                .collect(Collectors.toMap(Cache.Entry::getKey, Cache.Entry::getValue));
    }

    @Override
    public M get(String key) {
        return map.get(key);
    }

    @Override
    public void remove(String key) {
        map.remove(key);
    }

    @Override
    public void removeAsync(String key) {
        map.withAsync().remove(key);
    }

    @Override
    public void clear() {
        map.clear();
    }

}
