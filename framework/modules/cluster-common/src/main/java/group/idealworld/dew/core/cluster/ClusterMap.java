package group.idealworld.dew.core.cluster;

import java.util.Map;
import java.util.function.Consumer;

/**
 * 分布式Map服务.
 *
 * @param <M> 值的类型
 * @author gudaoxuri
 */
public interface ClusterMap<M> {

    /**
     * 添加Item，同步实现.
     *
     * @param key   key
     * @param value value
     */
    void put(String key, M value);

    /**
     * 添加Item，异步实现.
     *
     * @param key   key
     * @param value value
     */
    void putAsync(String key, M value);

    /**
     * 添加不存在的Item，同步实现.
     *
     * @param key   key
     * @param value value
     */
    void putIfAbsent(String key, M value);

    /**
     * 指定Key是否存在.
     *
     * @param key key
     * @return 是否存在 boolean
     */
    boolean containsKey(String key);

    /**
     * 获取所有Item.
     *
     * @return 所有Item all
     */
    Map<String, M> getAll();

    /**
     * 获取指定key的value.
     *
     * @param key key
     * @return 对应的value m
     */
    M get(String key);

    /**
     * 删除指定key的Item，同步实现.
     *
     * @param key key
     */
    void remove(String key);

    /**
     * 删除指定key的Item，异步实现.
     *
     * @param key key
     */
    void removeAsync(String key);

    /**
     * 清空Map.
     */
    void clear();

    /**
     * 注册新增Item时要执行的函数.
     * <p>
     * 目前只支持Hazelcast实现
     *
     * @param fun 执行的函数
     * @return the cluster map
     */
    default ClusterMap<M> regEntryAddedEvent(Consumer<EntryEvent<M>> fun) {
        return this;
    }

    /**
     * 注册删除Item时要执行的函数.
     * <p>
     * 目前只支持Hazelcast实现
     *
     * @param fun 执行的函数
     * @return the cluster map
     */
    default ClusterMap<M> regEntryRemovedEvent(Consumer<EntryEvent<M>> fun) {
        return this;
    }

    /**
     * 注册更新Item时要执行的函数.
     * <p>
     * 目前只支持Hazelcast实现
     *
     * @param fun 执行的函数
     * @return the cluster map
     */
    default ClusterMap<M> regEntryUpdatedEvent(Consumer<EntryEvent<M>> fun) {
        return this;
    }

    /**
     * 注册清空Map时要执行的函数.
     * <p>
     * 目前只支持Hazelcast实现
     *
     * @param fun 执行的函数
     * @return the cluster map
     */
    default ClusterMap<M> regMapClearedEvent(VoidProcessFun fun) {
        return this;
    }

    /**
     * Entry event.
     *
     * @param <V> the type parameter
     */
    class EntryEvent<V> {
        private String key;
        private V oldValue;
        private V value;

        /**
         * Gets key.
         *
         * @return the key
         */
        public String getKey() {
            return key;
        }

        /**
         * Sets key.
         *
         * @param key the key
         */
        public void setKey(String key) {
            this.key = key;
        }

        /**
         * Gets old value.
         *
         * @return the old value
         */
        public V getOldValue() {
            return oldValue;
        }

        /**
         * Sets old value.
         *
         * @param oldValue the old value
         */
        public void setOldValue(V oldValue) {
            this.oldValue = oldValue;
        }

        /**
         * Gets value.
         *
         * @return the value
         */
        public V getValue() {
            return value;
        }

        /**
         * Sets value.
         *
         * @param value the value
         */
        public void setValue(V value) {
            this.value = value;
        }

    }
}
