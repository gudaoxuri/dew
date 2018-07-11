package com.tairanchina.csp.dew.core.cluster;

import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * 集群服务
 */
public class Cluster {

    public static final String CLASS_LOAD_UNIQUE_FLAG = UUID.randomUUID().toString();

    private static Function<String,Map<String, Object>> _mqGetHeader;
    private static Consumer<Object[]> _mqSetHeader;

    public static void initMQHeader(Function<String,Map<String, Object>> mqGetHeader, Consumer<Object[]> mqSetHeader) {
        _mqGetHeader = mqGetHeader;
        _mqSetHeader = mqSetHeader;
    }

    public static Map<String, Object> getMQHeader(String name) {
        return _mqGetHeader.apply(name);
    }

    public static void setMQHeader(String name,Map<String, Object> header) {
        _mqSetHeader.accept(new Object[]{name,header});
    }

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
