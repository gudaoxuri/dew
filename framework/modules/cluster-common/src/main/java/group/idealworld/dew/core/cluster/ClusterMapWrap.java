package group.idealworld.dew.core.cluster;

/**
 * 分布式Map服务多实例封装.
 *
 * @author gudaoxuri
 */
public interface ClusterMapWrap {

    /**
     * 分布式Map服务实例获取.
     *
     * @param key   实例Key
     * @param clazz 值的类型
     * @param <M>   值的类型
     * @return 分布式Map服务实例
     */
    <M> ClusterMap<M> instance(String key, Class<M> clazz);

}
