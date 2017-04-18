package com.ecfront.dew.core.cluster.spi.hazelcast;

import com.ecfront.dew.core.cluster.ClusterDistMap;
import com.ecfront.dew.core.cluster.VoidProcessFun;
import com.hazelcast.core.IMap;
import com.hazelcast.core.MapEvent;
import com.hazelcast.map.listener.EntryAddedListener;
import com.hazelcast.map.listener.EntryRemovedListener;
import com.hazelcast.map.listener.EntryUpdatedListener;
import com.hazelcast.map.listener.MapClearedListener;

import java.util.Map;
import java.util.function.Consumer;

public class HazelcastClusterDistMap<M> implements ClusterDistMap<M> {

    private IMap<String, M> map;

    public HazelcastClusterDistMap(String mapKey, Class<M> clazz, HazelcastAdapter hazelcastAdapter) {
        map = hazelcastAdapter.getHazelcastInstance().getMap("dew:dist:map:" + mapKey);
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
    public HazelcastClusterDistMap<M> regEntryAddedEvent(Consumer<EntryEvent<String, M>> fun) {
        map.addEntryListener((EntryAddedListener<String, M>) entryEvent -> {
            EntryEvent<String, M> ee = new EntryEvent<>();
            ee.setKey(entryEvent.getKey());
            ee.setOldValue(entryEvent.getOldValue());
            ee.setValue(entryEvent.getValue());
            fun.accept(ee);
        }, true);
        return this;
    }

    @Override
    public HazelcastClusterDistMap<M> regEntryRemovedEvent(Consumer<EntryEvent<String, M>> fun) {
        map.addEntryListener((EntryRemovedListener<String, M>) entryEvent -> {
            EntryEvent<String, M> ee = new EntryEvent<>();
            ee.setKey(entryEvent.getKey());
            ee.setOldValue(entryEvent.getOldValue());
            ee.setValue(entryEvent.getValue());
            fun.accept(ee);
        }, true);
        return this;
    }

    @Override
    public HazelcastClusterDistMap<M> regEntryUpdatedEvent(Consumer<EntryEvent<String, M>> fun) {
        map.addEntryListener((EntryUpdatedListener<String, M>) entryEvent -> {
            EntryEvent<String, M> ee = new EntryEvent<>();
            ee.setKey(entryEvent.getKey());
            ee.setOldValue(entryEvent.getOldValue());
            ee.setValue(entryEvent.getValue());
            fun.accept(ee);
        }, true);
        return this;
    }

    @Override
    public HazelcastClusterDistMap<M> regMapClearedEvent(VoidProcessFun fun) {
        map.addEntryListener(new MapClearedListener() {
            @Override
            public void mapCleared(MapEvent event) {
                fun.exec();
            }
        }, false);
        return this;
    }


}
