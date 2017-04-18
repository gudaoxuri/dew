package com.ecfront.dew.core.cluster;

import java.util.Map;
import java.util.function.Consumer;

public interface ClusterDistMap<M> {

    void put(String key, M value);

    void putAsync(String key, M value);

    void putIfAbsent(String key, M value);

    boolean containsKey(String key);

    Map<String, M> getAll();

    M get(String key);

    void remove(String key);

    void removeAsync(String key);

    void clear();

    default ClusterDistMap<M> regEntryAddedEvent(Consumer<EntryEvent<String, M>> fun) {
        return this;
    }

    default ClusterDistMap<M> regEntryRemovedEvent(Consumer<EntryEvent<String, M>> fun) {
        return this;
    }

    default ClusterDistMap<M> regEntryUpdatedEvent(Consumer<EntryEvent<String, M>> fun) {
        return this;
    }

    default ClusterDistMap<M> regMapClearedEvent(VoidProcessFun fun) {
        return this;
    }

    class EntryEvent<String, V> {
        private String key;
        private V oldValue;
        private V value;
        private V mergingValue;

        public String getKey() {
            return key;
        }

        public void setKey(String key) {
            this.key = key;
        }

        public V getOldValue() {
            return oldValue;
        }

        public void setOldValue(V oldValue) {
            this.oldValue = oldValue;
        }

        public V getValue() {
            return value;
        }

        public void setValue(V value) {
            this.value = value;
        }

        public V getMergingValue() {
            return mergingValue;
        }

        public void setMergingValue(V mergingValue) {
            this.mergingValue = mergingValue;
        }
    }
}
