package group.idealworld.dew.core.cluster;

/**
 * 分布式锁服务多实例封装.
 *
 * @author gudaoxuri
 */
public interface ClusterLockWrap {

    /**
     * 分布式锁服务实例获取.
     *
     * @param key 实例Key
     * @return 分布式锁服务实例
     */
    ClusterLock instance(String key);

}
