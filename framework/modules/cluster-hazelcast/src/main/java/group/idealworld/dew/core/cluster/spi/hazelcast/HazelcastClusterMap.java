/*
 * Copyright 2022. the original author or authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package group.idealworld.dew.core.cluster.spi.hazelcast;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import com.hazelcast.map.listener.EntryAddedListener;
import com.hazelcast.map.listener.EntryRemovedListener;
import com.hazelcast.map.listener.EntryUpdatedListener;
import com.hazelcast.map.listener.MapClearedListener;
import group.idealworld.dew.core.cluster.ClusterMap;
import group.idealworld.dew.core.cluster.VoidProcessFun;

import java.util.Map;
import java.util.function.Consumer;

/**
 * 分布式Map服务 Hazelcast 实现.
 *
 * @param <M> 值的类型
 * @author gudaoxuri
 */
public class HazelcastClusterMap<M> implements ClusterMap<M> {

    private IMap<String, M> map;

    /**
     * Instantiates a new Hazelcast cluster map.
     *
     * @param mapKey            the map key
     * @param hazelcastInstance the hazelcast instance
     */
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
