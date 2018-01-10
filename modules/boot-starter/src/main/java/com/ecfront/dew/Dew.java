package com.ecfront.dew;

import com.ecfront.dew.common.$;
import com.ecfront.dew.common.StandardCode;
import com.ecfront.dew.core.cluster.*;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.ecfront.dew.core.DewConfig;
import com.ecfront.dew.core.DewContext;
import com.ecfront.dew.core.auth.AuthAdapter;
import com.ecfront.dew.core.auth.BasicAuthAdapter;
import com.ecfront.dew.core.cluster.*;
import com.ecfront.dew.core.fun.VoidExecutor;
import com.ecfront.dew.core.fun.VoidPredicate;
import com.ecfront.dew.core.jdbc.DS;
import com.ecfront.dew.core.jdbc.DSManager;
import com.ecfront.dew.core.loding.DewLoadImmediately;
import com.ecfront.dew.core.utils.NetUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.jackson.JacksonProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.net.InetAddress;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Component
public class Dew {

    private static final Logger logger = LoggerFactory.getLogger(Dew.class);

    public static Cluster cluster = new Cluster();
    public static ApplicationContext applicationContext;
    public static DewConfig dewConfig;
    public static AuthAdapter auth;

    @Value("${spring.application.name}")
    private String applicationName;

    @Autowired
    @Qualifier("dewConfig")
    private DewConfig innerDewConfig;

    @Autowired(required = false)
    private JacksonProperties jacksonProperties;

    @Autowired
    private ApplicationContext innerApplicationContext;

    @PostConstruct
    private void init() throws IOException, ClassNotFoundException {
        Dew.applicationContext = innerApplicationContext;
        if (Dew.applicationContext.containsBean(innerDewConfig.getCluster().getCache() + "ClusterCache")) {
            Dew.cluster.cache = (ClusterCache) Dew.applicationContext.getBean(innerDewConfig.getCluster().getCache() + "ClusterCache");
        }
        if (Dew.applicationContext.containsBean(innerDewConfig.getCluster().getDist() + "ClusterDist")) {
            Dew.cluster.dist = (ClusterDist) Dew.applicationContext.getBean(innerDewConfig.getCluster().getDist() + "ClusterDist");
        }
        if (Dew.applicationContext.containsBean(innerDewConfig.getCluster().getMq() + "ClusterMQ")) {
            Dew.cluster.mq = (ClusterMQ) Dew.applicationContext.getBean(innerDewConfig.getCluster().getMq() + "ClusterMQ");
        }
        if (Dew.applicationContext.containsBean(innerDewConfig.getCluster().getElection() + "ClusterElection")) {
            Dew.cluster.election = (ClusterElection) Dew.applicationContext.getBean(innerDewConfig.getCluster().getElection() + "ClusterElection");
        }
        Dew.dewConfig = innerDewConfig;
        if (Dew.applicationContext.containsBean(DSManager.class.getSimpleName())) {
            Dew.applicationContext.getBean(DSManager.class);
        }
        Dew.Info.name = applicationName;
        // Load Auth Adapter
        auth = Dew.applicationContext.getBean(BasicAuthAdapter.class);
        // Support java8 Time
        if (jacksonProperties != null) {
            jacksonProperties.getSerialization().put(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        }
        // Load Immediately
        Set<Class<?>> loadOrders = $.clazz.scan(Dew.class.getPackage().getName(), new HashSet<Class<? extends Annotation>>() {{
            add(DewLoadImmediately.class);
        }}, null);
        loadOrders.forEach(loadOrder -> Dew.applicationContext.getBean(loadOrder));
    }

    public static class Constant {
        public static final String HTTP_REQUEST_FROM_FLAG = "Request-From";
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
        public static String instance;

        static {
            InetAddress inetAddress = NetUtils.getLocalAddress();
            ip = inetAddress.getHostAddress();
            host = inetAddress.getHostName();
            instance = $.field.createUUID();
        }

    }

    public static DS ds() {
        return DSManager.select("");
    }

    public static DS ds(String dsName) {
        return DSManager.select(dsName);
    }

    /**
     * 获取请求上下文信息
     *
     * @return 请求上下文信息
     */
    public static DewContext context() {
        return DewContext.getContext();
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

        private static ExecutorService executorService = Executors.newCachedThreadPool();

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

    public static class E {

        /**
         * 异常处理-重用Http状态
         *
         * @param code 异常编码
         * @param ex   异常类型
         */
        public static <E extends Throwable> E e(String code, E ex) {
            return e(code, ex, -1);
        }

        /**
         * 异常处理-重用Http状态
         *
         * @param code           异常编码
         * @param ex             异常类型
         * @param customHttpCode 自定义Http状态码
         */
        public static <E extends Throwable> E e(String code, E ex, StandardCode customHttpCode) {
            return e(code, ex, Integer.valueOf(customHttpCode.toString()));
        }

        /**
         * 异常处理-重用Http状态
         *
         * @param code           异常编码
         * @param ex             异常类型
         * @param customHttpCode 自定义Http状态码
         */
        public static <E extends Throwable> E e(String code, E ex, int customHttpCode) {
            try {
                $.bean.setValue(ex, "detailMessage", $.json.createObjectNode()
                        .put("code", code)
                        .put("message", ex.getLocalizedMessage())
                        .put("customHttpCode", customHttpCode)
                        .toString());
            } catch (NoSuchFieldException e1) {
                logger.error("Throw Exception Convert error", e1);
            }
            return ex;
        }

        public static <E extends RuntimeException> void checkNotNull(Object obj, E ex) {
            check(() -> obj == null, ex);
        }

        public static <E extends RuntimeException> void checkNotEmpty(Iterable<?> objects, E ex) {
            check(() -> objects == null || !objects.iterator().hasNext(), ex);
        }

        public static <E extends RuntimeException> void checkNotEmpty(Map<?, ?> objects, E ex) {
            check(() -> objects == null || objects.size() == 0, ex);
        }

        /**
         * 抛出不符合预期异常
         *
         * @param notExpected 不符合预期的情况
         * @param ex          异常
         */
        public static <E extends RuntimeException> void check(boolean notExpected, E ex) {
            check(() -> notExpected, ex);
        }

        /**
         * 抛出不符合预期异常
         *
         * @param notExpected 不符合预期的情况
         * @param ex          异常
         */
        public static <E extends RuntimeException> void check(VoidPredicate notExpected, E ex) {
            if (notExpected.test()) {
                throw ex;
            }
        }

    }

}
