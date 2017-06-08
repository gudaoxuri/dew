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
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

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

        private static RestTemplate restTemplate;

        public static void setRestTemplate(RestTemplate _restTemplate) {
            restTemplate = _restTemplate;
        }

        public static <T> ResponseEntity<T> get(String url, Class<T> respClazz) {
            return get(url, null, respClazz);
        }

        public static <T> ResponseEntity<T> get(String url, Map<String, String> header, Class<T> respClazz) {
            return exchange(HttpMethod.GET, url, null, header, respClazz);
        }

        public static <T> ResponseEntity<T> delete(String url, Class<T> respClazz) {
            return delete(url, null, respClazz);
        }

        public static <T> ResponseEntity<T> delete(String url, Map<String, String> header, Class<T> respClazz) {
            return exchange(HttpMethod.DELETE, url, null, header, respClazz);
        }

        public static <T> ResponseEntity<T> head(String url, Class<T> respClazz) {
            return head(url, null, respClazz);
        }

        public static <T> ResponseEntity<T> head(String url, Map<String, String> header, Class<T> respClazz) {
            return exchange(HttpMethod.HEAD, url, null, header, respClazz);
        }

        public static <T> ResponseEntity<T> options(String url, Class<T> respClazz) {
            return options(url, null, respClazz);
        }

        public static <T> ResponseEntity<T> options(String url, Map<String, String> header, Class<T> respClazz) {
            return exchange(HttpMethod.OPTIONS, url, null, header, respClazz);
        }

        public static <T> ResponseEntity<T> post(String url, Object body, Class<T> respClazz) {
            return post(url, body, null, respClazz);
        }

        public static <T> ResponseEntity<T> post(String url, Object body, Map<String, String> header, Class<T> respClazz) {
            return exchange(HttpMethod.POST, url, body, header, respClazz);
        }

        public static <T> ResponseEntity<T> put(String url, Object body, Class<T> respClazz) {
            return put(url, body, null, respClazz);
        }

        public static <T> ResponseEntity<T> put(String url, Object body, Map<String, String> header, Class<T> respClazz) {
            return exchange(HttpMethod.PUT, url, body, header, respClazz);
        }

        public static <T> ResponseEntity<T> exchange(HttpMethod httpMethod, String url, Object body, Map<String, String> header, Class<T> respClazz) {
            HttpHeaders headers = new HttpHeaders();
            if (header != null) {
                header.forEach(headers::add);
            }
            tryAttachTokenToHeader(headers);
            HttpEntity entity;
            if (body != null) {
                entity = new HttpEntity(body, headers);
            } else {
                entity = new HttpEntity(headers);
            }
            return restTemplate.exchange(tryAttachTokenToUrl(url), httpMethod, entity, respClazz);
        }

        private static String tryAttachTokenToUrl(String url) {
            if (!Dew.dewConfig.getSecurity().isTokenInHeader()) {
                String token = Dew.context().getToken();
                if (url.contains("&")) {
                    return url + "&" + Dew.dewConfig.getSecurity().getTokenFlag() + "=" + token;
                } else {
                    return url + "?" + Dew.dewConfig.getSecurity().getTokenFlag() + "=" + token;
                }
            }
            return url;
        }

        private static void tryAttachTokenToHeader(HttpHeaders headers) {
            if (Dew.dewConfig.getSecurity().isTokenInHeader()) {
                String token = Dew.context().getToken();
                headers.add(Dew.dewConfig.getSecurity().getTokenFlag(), token);
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

    }

    public static class Auth {

        public static Optional<OptInfo> getOptInfo() {
            return Dew.context().optInfo();
        }

        public static Optional<OptInfo> getOptInfo(String token) {
            String optInfoStr = Dew.cluster.cache.get(Dew.Constant.TOKEN_INFO_FLAG + token);
            if (optInfoStr != null && !optInfoStr.isEmpty()) {
                return Optional.of($.json.toObject(optInfoStr, OptInfo.class));
            } else {
                return Optional.empty();
            }
        }

        public static void removeOptInfo() {
            Optional<OptInfo> tokenInfoOpt = getOptInfo();
            if (tokenInfoOpt.isPresent()) {
                Dew.cluster.cache.del(Dew.Constant.TOKEN_ID_REL_FLAG + tokenInfoOpt.get().getAccountCode());
                Dew.cluster.cache.del(Dew.Constant.TOKEN_INFO_FLAG + tokenInfoOpt.get().getToken());
            }
        }

        public static void removeOptInfo(String token) {
            Optional<OptInfo> tokenInfoOpt = getOptInfo(token);
            if (tokenInfoOpt.isPresent()) {
                Dew.cluster.cache.del(Dew.Constant.TOKEN_ID_REL_FLAG + tokenInfoOpt.get().getAccountCode());
                Dew.cluster.cache.del(Dew.Constant.TOKEN_INFO_FLAG + token);
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
            Dew.cluster.cache.del(Dew.Constant.TOKEN_INFO_FLAG + Dew.cluster.cache.get(Dew.Constant.TOKEN_ID_REL_FLAG + optInfo.getAccountCode()));
            Dew.cluster.cache.set(Dew.Constant.TOKEN_ID_REL_FLAG + optInfo.getAccountCode(), optInfo.getToken());
            Dew.cluster.cache.set(Dew.Constant.TOKEN_INFO_FLAG + optInfo.getToken(), $.json.toJsonString(optInfo));
        }

    }

}
