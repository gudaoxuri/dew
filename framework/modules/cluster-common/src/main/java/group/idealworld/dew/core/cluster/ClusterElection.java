package group.idealworld.dew.core.cluster;

/**
 * 领导者选举服务.
 *
 * @author gudaoxuri
 */
public interface ClusterElection {

    /**
     * 当前工程是否是领导者.
     *
     * @return 是否是领导者
     */
    boolean isLeader();

}
