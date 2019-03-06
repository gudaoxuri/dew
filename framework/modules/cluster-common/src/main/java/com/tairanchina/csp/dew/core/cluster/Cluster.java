package com.tairanchina.csp.dew.core.cluster;

import com.ecfront.dew.common.$;
import com.tairanchina.csp.dew.core.cluster.ha.ClusterHA;
import com.tairanchina.csp.dew.core.cluster.ha.H2ClusterHA;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * 集群服务
 */
public class Cluster {
    private static final Logger logger = LoggerFactory.getLogger(Cluster.class);

    private static Function<String, Map<String, Object>> _mqGetHeader;
    private static Consumer<Object[]> _mqSetHeader;
    private static ClusterHA clusterHA = null;
    private static String applicationName = "";
    public static String instanceId = $.field.createUUID();

    public static void init(String appName,String instId) {
        applicationName = appName;
        instanceId = instId;
    }

    public static void initMQHeader(Function<String, Map<String, Object>> mqGetHeader, Consumer<Object[]> mqSetHeader) {
        _mqGetHeader = mqGetHeader;
        _mqSetHeader = mqSetHeader;
    }

    public static void ha() {
        ha(new HashMap<String, String>() {{
            put("url", "jdbc:h2:./.ha_" + applicationName + ";DB_CLOSE_ON_EXIT=FALSE");
        }});
    }

    public static void ha(Map<String, String> args) {
        clusterHA = new H2ClusterHA();
        try {
            clusterHA.init(args);
            logger.info("HA initialized");
        } catch (SQLException e) {
            logger.error("HA init error", e);
        }
    }

    public static boolean haEnabled() {
        return clusterHA != null;
    }

    public static ClusterHA getClusterHA() {
        return clusterHA;
    }

    public static Map<String, Object> getMQHeader(String name) {
        if (_mqGetHeader != null) {
            return _mqGetHeader.apply(name);
        } else {
            return new HashMap<>();
        }
    }

    public static void setMQHeader(String name, Map<String, Object> header) {
        if (_mqSetHeader != null) {
            _mqSetHeader.accept(new Object[]{name, header});
        }
    }

    /**
     * MQ服务
     */
    public ClusterMQ mq;

    /**
     * 分布式锁服务
     */
    public ClusterLockWrap lock;

    /**
     * 分布式Map服务
     */
    public ClusterMapWrap map;

    /**
     * 缓存服务
     */
    public ClusterCache cache;

    /**
     * 领导者选举服务
     */
    public ClusterElectionWrap election;

}
