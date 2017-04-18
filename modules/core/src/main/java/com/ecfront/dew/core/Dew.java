package com.ecfront.dew.core;

import com.ecfront.dew.common.TimerHelper;
import com.ecfront.dew.core.cluster.Cluster;
import com.ecfront.dew.core.fun.VoidExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Component
public class Dew {

    public static class Constant {
        // token存储key
        public static final String TOKEN_INFO_FLAG = "dew:auth:token:info:";
        // 前端传入的token标识
        public static final String TOKEN_VIEW_FLAG = "__dew_token__";

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
        public static String instance = Dew.Util.createUUID();

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

    public static Cluster cluster=new Cluster();

    public static ApplicationContext applicationContext;

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
                return url + "&" + Constant.TOKEN_VIEW_FLAG + "=" + token;
            } else {
                return url + "?" + Constant.TOKEN_VIEW_FLAG + "=" + token;
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
            TimerHelper.periodic(initialDelaySec, periodSec, () -> {
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
            TimerHelper.timer(delaySec, () -> {
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

        private static String[] chars = new String[]{"a", "b", "c", "d", "e", "f",
                "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s",
                "t", "u", "v", "w", "x", "y", "z", "0", "1", "2", "3", "4", "5",
                "6", "7", "8", "9", "A", "B", "C", "D", "E", "F", "G", "H", "I",
                "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V",
                "W", "X", "Y", "Z"};

        public static String createUUID() {
            return UUID.randomUUID().toString().replace("-", "");
        }

        public static String createShortUUID() {
            StringBuffer shortBuffer = new StringBuffer();
            String uuid = createUUID();
            for (int i = 0; i < 8; i++) {
                String str = uuid.substring(i * 4, i * 4 + 4);
                int x = Integer.parseInt(str, 16);
                shortBuffer.append(chars[x % 0x3E]);
            }
            return shortBuffer.toString();
        }

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

        private static final String REGEX_EMAIL = "\\w+(\\.\\w)*@\\w+(\\.\\w{2,3}){1,3}";

        public static boolean checkEmail(String email) {
            return email.matches(REGEX_EMAIL);
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

    @Autowired
    public void setSpringContext(ApplicationContext applicationContext) {
        Dew.applicationContext = applicationContext;
    }

}
