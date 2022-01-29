/*
 * Copyright 2022. the original author or authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package group.idealworld.dew.core.cluster;

import com.ecfront.dew.common.$;
import com.ecfront.dew.common.DependencyHelper;
import group.idealworld.dew.core.cluster.dto.MessageHeader;
import group.idealworld.dew.core.cluster.ha.ClusterHA;
import group.idealworld.dew.core.cluster.ha.H2ClusterHA;
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
    private static final Logger logger = LoggerFactory.getLogger(Cluster.class);
    public static String instanceId = $.field.createUUID();
    private static Function<String, Map<String, Object>> _mqGetHeader;
    private static Function<MessageHeader, Map<String, Object>> _mqSetHeader;
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
     * @param mqGetHeader 在发送MQ消息前自定义设置MQ Header , Input:Topic name, Output:Header Items
     * @param mqSetHeader 在收到MQ消息时自定义设置MQ Header , Input:Topic name + Header Items, Output:Header Items
     */
    public static void initMqHeader(
            Function<String, Map<String, Object>> mqGetHeader, Function<MessageHeader, Map<String, Object>> mqSetHeader) {
        _mqGetHeader = mqGetHeader;
        _mqSetHeader = mqSetHeader;
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
        if (DependencyHelper.hasDependency("org.h2.jdbcx.JdbcConnectionPool")) {
            clusterHA = new H2ClusterHA();
        } else {
            logger.warn("Not found HA implementation drives , HA disabled.");
            return;
        }
        try {
            if (haConfig.getStoragePath() == null || haConfig.getStoragePath().isEmpty()) {
                haConfig.setStoragePath("./");
            } else {
                if (!haConfig.getStoragePath().endsWith("/")) {
                    haConfig.setStoragePath(haConfig.getStoragePath() + "/");
                }
            }
            if (haConfig.getStorageName() == null || haConfig.getStorageName().isEmpty()) {
                haConfig.setStorageName(applicationName);
            }
            clusterHA.init(haConfig);
            logger.info("HA initialized");
        } catch (SQLException e) {
            logger.error("HA init error", e);
        }
    }

    static boolean haEnabled() {
        return clusterHA != null;
    }

    static ClusterHA getClusterHA() {
        return clusterHA;
    }

    static Map<String, Object> getMQHeader(String name) {
        if (_mqGetHeader != null) {
            return _mqGetHeader.apply(name);
        } else {
            return new HashMap<>();
        }
    }

    static Map<String, Object> setMQHeader(String name, Map<String, Object> header) {
        if (_mqSetHeader != null) {
            return _mqSetHeader.apply(new MessageHeader(name, header));
        }
        return header;
    }
}
