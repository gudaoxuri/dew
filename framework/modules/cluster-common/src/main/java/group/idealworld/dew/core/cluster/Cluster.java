package group.idealworld.dew.core.cluster;

import com.ecfront.dew.common.$;
import com.ecfront.dew.common.DependencyHelper;
import group.idealworld.dew.core.cluster.dto.MessageHeader;
import group.idealworld.dew.core.cluster.ha.ClusterHA;
import group.idealworld.dew.core.cluster.ha.SqliteClusterHA;
import group.idealworld.dew.core.cluster.ha.dto.HAConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * 集群服务.
 *
 * @author gudaoxuri
 */
public class Cluster {
    private static final Logger LOGGER = LoggerFactory.getLogger(Cluster.class);

    /**
     * 实例Id.
     */
    public static String instanceId = $.field.createUUID();
    private static Function<String, Map<String, Object>> mqGetHeader;
    private static Function<MessageHeader, Map<String, Object>> mqSetHeader;
    private static ClusterHA clusterHA = null;
    private static String applicationName = "_default";
    /**
     * MQ服务.
     */
    public ClusterMQ mq;
    /**
     * 分布式锁服务.
     */
    public ClusterLockWrap lock;
    /**
     * 分布式Map服务.
     */
    public ClusterMapWrap map;
    /**
     * 多实例缓存服务.
     */
    public ClusterCacheWrap caches;
    /**
     * 默认缓存服务.
     */
    public ClusterCache cache;
    /**
     * 领导者选举服务.
     */
    public ClusterElectionWrap election;
    /**
     * 链路追踪服务.
     */
    public ClusterTrace trace;

    /**
     * 初始化.
     *
     * @param appName 应用名，可用于HA的存储配置
     * @param instId  实例Id，全局唯一，用于区别不同实例
     */
    public static void init(String appName, String instId) {
        applicationName = appName;
        instanceId = instId;
    }

    /**
     * 初始化MQ Header的自定义处理方法.
     *
     * @param mqGetHeader 在发送MQ消息前自定义设置MQ Header , Input:Topic name, Output:Header
     *                    Items
     * @param mqSetHeader 在收到MQ消息时自定义设置MQ Header , Input:Topic name + Header Items,
     *                    Output:Header Items
     */
    public static void initMqHeader(Function<String, Map<String, Object>> mqGetHeader,
                                    Function<MessageHeader, Map<String, Object>> mqSetHeader) {
        Cluster.mqGetHeader = mqGetHeader;
        Cluster.mqSetHeader = mqSetHeader;
    }

    /**
     * 启用HA.
     * <p>
     * 使用默认配置
     */
    public static void ha() {
        ha(new HAConfig());
    }

    /**
     * 启用HA.
     *
     * @param haConfig HA配置信息
     */
    public static void ha(HAConfig haConfig) {
        if (DependencyHelper.hasDependency("org.sqlite.javax.SQLiteConnectionPoolDataSource")) {
            clusterHA = new SqliteClusterHA();
        } else {
            LOGGER.warn("Not found HA implementation drives , HA disabled.");
            return;
        }
        try {
            clusterHA.init(haConfig);
            LOGGER.info("HA initialized");
        } catch (SQLException e) {
            LOGGER.error("HA init error", e);
        }
    }

    static boolean haEnabled() {
        return clusterHA != null;
    }

    static ClusterHA getClusterHA() {
        return clusterHA;
    }

    static Map<String, Object> getMQHeader(String name) {
        if (mqGetHeader != null) {
            return mqGetHeader.apply(name);
        } else {
            return new HashMap<>();
        }
    }

    static Map<String, Object> setMQHeader(String name, Map<String, Object> header) {
        if (mqSetHeader != null) {
            return mqSetHeader.apply(new MessageHeader(name, header));
        }
        return header;
    }
}
