package group.idealworld.dew.core.cluster;

/**
 * 缓存服务多实例封装.
 *
 * @author gudaoxuri
 */
public interface ClusterCacheWrap {

    /**
     * 缓存服务实例获取，默认实例.
     *
     * @return 缓存服务实例
     */
    default ClusterCache instance() {
        return instance("");
    }

    /**
     * 缓存服务服务实例获取.
     *
     * @param key 实例Key
     * @return 缓存服务实例
     */
    ClusterCache instance(String key);

    /**
     * 是否存在对应的缓存服务实例.
     *
     * @param key 实例Key
     * @return 是否存在
     */
    boolean exist(String key);

}
