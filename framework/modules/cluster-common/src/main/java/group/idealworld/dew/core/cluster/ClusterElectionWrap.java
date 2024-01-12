package group.idealworld.dew.core.cluster;

/**
 * 领导者选举服务多实例封装.
 *
 * @author gudaoxuri
 */
public interface ClusterElectionWrap {

    /**
     * 领导者选举服务实例获取，默认实例.
     *
     * @return 领导者选举服务实例
     */
    ClusterElection instance();

    /**
     * 领导者选举服务实例获取.
     *
     * @param key 实例Key
     * @return 领导者选举服务实例
     */
    ClusterElection instance(String key);

}
