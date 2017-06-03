package com.ecfront.dew.core;

import com.ecfront.dew.common.$;
import com.ecfront.dew.core.cluster.Cluster;
import com.ecfront.dew.core.cluster.ClusterCache;
import com.ecfront.dew.core.cluster.ClusterDist;
import com.ecfront.dew.core.cluster.ClusterMQ;
import com.ecfront.dew.core.dto.OptInfo;
import com.ecfront.dew.core.entity.EntityContainer;
import com.ecfront.dew.core.fun.VoidExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.servlet.http.HttpServletRequest;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Component
public class Dew {

    @Autowired
    @Qualifier("dewConfig")
    private DewConfig _dewConfig;
    @Autowired
    private ApplicationContext _applicationContext;

    @PostConstruct
    public void init() {
        Dew.applicationContext = _applicationContext;
        if (Dew.applicationContext.containsBean(_dewConfig.getCluster().getCache() + "ClusterCache")) {
            Dew.cluster.cache = (ClusterCache) Dew.applicationContext.getBean(_dewConfig.getCluster().getCache() + "ClusterCache");
        }
        if (Dew.applicationContext.containsBean(_dewConfig.getCluster().getDist() + "ClusterDist")) {
            Dew.cluster.dist = (ClusterDist) Dew.applicationContext.getBean(_dewConfig.getCluster().getDist() + "ClusterDist");
        }
        if (Dew.applicationContext.containsBean(_dewConfig.getCluster().getMq() + "ClusterMQ")) {
            Dew.cluster.mq = (ClusterMQ) Dew.applicationContext.getBean(_dewConfig.getCluster().getMq() + "ClusterMQ");
        }
        Dew.dewConfig = _dewConfig;
    }

    @PostConstruct
    @ConditionalOnClass({Entity.class})
    public void initEntity() {
        _applicationContext.getBean(EntityContainer.class);
    }

    public static class Constant {
        // token存储key
        public static final String TOKEN_INFO_FLAG = "dew:auth:token:info:";
        // Token Id 关联 key : dew:auth:token:id:rel:<code> value : <token Id>
        public static final String TOKEN_ID_REL_FLAG = "dew:auth:token:id:rel:";

        public static final String MQ_AUTH_TENANT_ADD = "dew.auth.tenant.add";
        public static final String MQ_AUTH_TENANT_REMOVE = "dew.auth.tenant.remove";
        public static final String MQ_AUTH_RESOURCE_ADD = "dew.auth.resource.add";
        public static final String MQ_AUTH_RESOURCE_REMOVE = "dew.auth.resource.remove";
        public static final String MQ_AUTH_ROLE_ADD = "dew.auth.role.add";
        public static final String MQ_AUTH_ROLE_REMOVE = "dew.auth.role.remove";
        public static final String MQ_AUTH_ACCOUNT_ADD = "dew.auth.account.add";
        public static final String MQ_AUTH_ACCOUNT_REMOVE = "dew.auth.account.remove";

        public static final String MQ_AUTH_REFRESH = "dew.auth.refresh";

    }

    /**
     * 组件基础信息
     */
    public static class Info {
        // 应用名称
        public static String name;
        // 应用主机IP
        public static String ip;
        // 应用主机名
        public static String host;
        // 应用实例，各组件唯一
        public static String instance = $.field.createUUID();

        static {
            try {
                name = Dew.applicationContext.getId();
                ip = InetAddress.getLocalHost().getHostAddress();
                host = InetAddress.getLocalHost().getHostName();
            } catch (UnknownHostException e) {
                e.printStackTrace();
            }
        }

    }

    public static Cluster cluster = new Cluster();

    public static ApplicationContext applicationContext;

    public static EntityManager em;

    public static DewConfig dewConfig;

    /**
     * 获取请求上下文信息
     *
     * @return 请求上下文信息
     */
    public static DewContext context() {
        return DewContext.getContext();
    }

    /**
     * 请求消息（基于RestTemplate）辅助工具
     */
    public static class EB {

        public static String buildUrl(String serviceName, String path) {
            return buildUrl(serviceName, path, Dew.context().getToken());
        }

        public static String buildUrl(String serviceName, String path, String token) {
            String url = "http://" + serviceName + "/" + path;
            if (url.contains("&")) {
                return url + "&" + Dew.dewConfig.getSecurity().getTokenFlag() + "=" + token;
            } else {
                return url + "?" + Dew.dewConfig.getSecurity().getTokenFlag() + "=" + token;
            }
        }

    }

    /**
     * 定时器支持（带请求上下文绑定）
     */
    public static class Timer {

        private static final Logger logger = LoggerFactory.getLogger(Timer.class);

        public static void periodic(long initialDelaySec, long periodSec, VoidExecutor fun) {
            DewContext context = Dew.context();
            $.timer.periodic(initialDelaySec, periodSec, true, () -> {
                DewContext.setContext(context);
                try {
                    fun.exec();
                } catch (Exception e) {
                    logger.error("[Timer] Execute error", e);
                }
            });
        }

        public static void periodic(long periodSec, VoidExecutor fun) {
            periodic(0, periodSec, fun);
        }

        public static void timer(long delaySec, VoidExecutor fun) {
            DewContext context = Dew.context();
            $.timer.timer(delaySec, () -> {
                DewContext.setContext(context);
                try {
                    fun.exec();
                } catch (Exception e) {
                    logger.error("[Timer] Execute error", e);
                }
            });
        }

    }

    /**
     * 常用工具
     */
    public static class Util {

        public static String getRealIP(HttpServletRequest request) {
            Map<String, String> requestHeader = new HashMap<>();
            Enumeration<String> header = request.getHeaderNames();
            while (header.hasMoreElements()) {
                String key = header.nextElement();
                requestHeader.put(key.toLowerCase(), request.getHeader(key));
            }
            return getRealIP(requestHeader, request.getRemoteAddr());
        }

        public static String getRealIP(Map<String, String> requestHeader, String remoteAddr) {
            if (requestHeader.containsKey("x-forwarded-for") && requestHeader.get("x-forwarded-for") != null && !requestHeader.get("x-forwarded-for").isEmpty()) {
                return requestHeader.get("x-forwarded-for");
            }
            if (requestHeader.containsKey("wl-proxy-client-ip") && requestHeader.get("wl-proxy-client-ip") != null && !requestHeader.get("wl-proxy-client-ip").isEmpty()) {
                return requestHeader.get("wl-proxy-client-ip");
            }
            if (requestHeader.containsKey("x-forwarded-host") && requestHeader.get("x-forwarded-host") != null && !requestHeader.get("x-forwarded-host").isEmpty()) {
                return requestHeader.get("x-forwarded-host");
            }
            return remoteAddr;
        }

        private static ExecutorService executorService = Executors.newCachedThreadPool();

        public static void newThread(Runnable fun) {
            executorService.execute(fun);
        }

        public static class RunnableWithContext implements Runnable {

            private VoidExecutor fun;
            private DewContext context;

            public RunnableWithContext(VoidExecutor fun) {
                this.fun = fun;
                this.context = DewContext.getContext();
            }

            @Override
            public void run() {
                DewContext.setContext(context);
                fun.exec();
            }
        }

        public static Optional<OptInfo> getOptInfoByAccCode(String accountCode) {
            String token = Dew.cluster.cache.get(Dew.Constant.TOKEN_ID_REL_FLAG + accountCode);
            if (token != null && !token.isEmpty()) {
                return Optional.of($.json.toObject(Dew.cluster.cache.get(Dew.Constant.TOKEN_INFO_FLAG + token), OptInfo.class));
            } else {
                return Optional.empty();
            }
        }

        public static void setOptInfo(OptInfo optInfo) {
            Dew.cluster.cache.set(Dew.Constant.TOKEN_ID_REL_FLAG + optInfo.getAccountCode(), optInfo.getToken());
            Dew.cluster.cache.set(Dew.Constant.TOKEN_INFO_FLAG + optInfo.getToken(), $.json.toJsonString(optInfo));
        }

    }

}
