package com.tairanchina.csp.dew.core.cluster;

import com.tairanchina.csp.dew.core.h2.H2Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * 集群服务
 */
public class Cluster {
    private static final Logger logger = LoggerFactory.getLogger(Cluster.class);

    public static final String CLASS_LOAD_UNIQUE_FLAG = UUID.randomUUID().toString();

    private static Function<String, Map<String, Object>> _mqGetHeader;
    private static Consumer<Object[]> _mqSetHeader;

    public static void initMQHeader(Function<String, Map<String, Object>> mqGetHeader, Consumer<Object[]> mqSetHeader) {
        _mqGetHeader = mqGetHeader;
        _mqSetHeader = mqSetHeader;
    }

    public static void initH2Database(String url, String user, String password) {
        logger.info("init h2 database...");
        try {
            H2Utils.init(url, user, password);
            logger.info("h2 database initialized");
        } catch (SQLException e) {
            logger.error("init h2 database error", e);
        }
    }

    public static Map<String, Object> getMQHeader(String name) {
        if(_mqGetHeader!=null){
            return _mqGetHeader.apply(name);
        }else {
            return null;
        }
    }

    public static void setMQHeader(String name, Map<String, Object> header) {
        if(_mqSetHeader!=null){
            _mqSetHeader.accept(new Object[]{name, header});
        }
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
