package com.ecfront.dew.core.cluster;

import java.util.UUID;

/**
 * 集群服务
 */
public class Cluster {

    public static final String CLASS_LOAD_UNIQUE_FLAG = UUID.randomUUID().toString();

    /**
     * MQ服务
     */
    public ClusterMQ mq;

    /**
     * 常用分布式服务，锁与Map
     */
    public ClusterDist dist;

    /**
     * 缓存服务
     */
    public ClusterCache cache;

    /**
     * 领导者选举服务
     */
    public ClusterElection election;

}
