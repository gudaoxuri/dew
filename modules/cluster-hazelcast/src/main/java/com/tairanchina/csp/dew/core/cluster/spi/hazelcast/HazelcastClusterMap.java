package com.tairanchina.csp.dew.core.cluster.spi.hazelcast;

import com.tairanchina.csp.dew.core.cluster.ClusterMap;
import com.tairanchina.csp.dew.core.cluster.VoidProcessFun;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import com.hazelcast.map.listener.EntryAddedListener;
import com.hazelcast.map.listener.EntryRemovedListener;
import com.hazelcast.map.listener.EntryUpdatedListener;
import com.hazelcast.map.listener.MapClearedListener;

import java.util.Map;
import java.util.function.Consumer;

public class HazelcastClusterMap<M> implements ClusterMap<M> {

    private IMap<String, M> map;

    public HazelcastClusterMap(String mapKey, HazelcastInstance hazelcastInstance) {
        map = hazelcastInstance.getMap("dew:cluster:map:" + mapKey);
    }

    @Override
    public void put(String key, M value) {
        map.put(key, value);
    }

    @Override
    public void putAsync(String key, M value) {
        map.putAsync(key, value);
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
        return map.getAll(map.keySet());
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
        map.removeAsync(key);
    }

    @Override
    public void clear() {
        map.clear();
    }

    @Override
    public HazelcastClusterMap<M> regEntryAddedEvent(Consumer<EntryEvent<M>> fun) {
        map.addEntryListener((EntryAddedListener<String, M>) entryEvent -> packageEntryEvent(fun, entryEvent), true);
        return this;
    }

    @Override
    public HazelcastClusterMap<M> regEntryRemovedEvent(Consumer<EntryEvent<M>> fun) {
        map.addEntryListener((EntryRemovedListener<String, M>) entryEvent -> packageEntryEvent(fun, entryEvent), true);
        return this;
    }

    @Override
    public HazelcastClusterMap<M> regEntryUpdatedEvent(Consumer<EntryEvent<M>> fun) {
        map.addEntryListener((EntryUpdatedListener<String, M>) entryEvent -> packageEntryEvent(fun, entryEvent), true);
        return this;
    }

    private void packageEntryEvent(Consumer<EntryEvent<M>> fun, com.hazelcast.core.EntryEvent<String, M> entryEvent) {
        EntryEvent<M> ee = new EntryEvent<>();
        ee.setKey(entryEvent.getKey());
        ee.setOldValue(entryEvent.getOldValue());
        ee.setValue(entryEvent.getValue());
        fun.accept(ee);
    }

    @Override
    public HazelcastClusterMap<M> regMapClearedEvent(VoidProcessFun fun) {
        map.addEntryListener((MapClearedListener) event -> fun.exec(), false);
        return this;
    }


}
