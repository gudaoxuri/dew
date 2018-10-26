package com.tairanchina.csp.dew.core;


import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.*;

@ConfigurationProperties(prefix = "dew")
public class DewConfig {

    private Basic basic = new Basic();
    private Cluster cluster = new Cluster();
    private Security security = new Security();
    private Metric metric = new Metric();
    private Map<String, Notify> notifies = new HashMap<>();

    public static class Basic {

        private String name = "";
        private String version = "1.0";
        private String desc = "";
        private String webSite = "";

        private Doc doc = new Doc();
        private Format format = new Format();
        private Map<String, ErrorMapping> errorMapping = new HashMap<>();

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getDesc() {
            return desc;
        }

        public void setDesc(String desc) {
            this.desc = desc;
        }

        public String getVersion() {
            return version;
        }

        public void setVersion(String version) {
            this.version = version;
        }

        public String getWebSite() {
            return webSite;
        }

        public void setWebSite(String webSite) {
            this.webSite = webSite;
        }

        public static class Doc {

            private boolean enabled = true;

            private String basePackage = "";

            public boolean isEnabled() {
                return enabled;
            }

            public void setEnabled(boolean enabled) {
                this.enabled = enabled;
            }

            public String getBasePackage() {
                return basePackage;
            }

            public void setBasePackage(String basePackage) {
                this.basePackage = basePackage;
            }
        }

        public static class Format {

            private boolean useUnityError = true;

            private String errorFlag="__DEW_ERROR__";

            public String getErrorFlag() {
                return errorFlag;
            }

            public void setErrorFlag(String errorFlag) {
                this.errorFlag = errorFlag;
            }

            public boolean isUseUnityError() {
                return useUnityError;
            }

            public void setUseUnityError(boolean useUnityError) {
                this.useUnityError = useUnityError;
            }
        }

        public static class ErrorMapping {

            private int httpCode;
            private String businessCode;
            private String message;

            public int getHttpCode() {
                return httpCode;
            }

            public void setHttpCode(int httpCode) {
                this.httpCode = httpCode;
            }

            public String getBusinessCode() {
                return businessCode;
            }

            public void setBusinessCode(String businessCode) {
                this.businessCode = businessCode;
            }

            public String getMessage() {
                return message;
            }

            public void setMessage(String message) {
                this.message = message;
            }
        }

        public Doc getDoc() {
            return doc;
        }

        public void setDoc(Doc doc) {
            this.doc = doc;
        }

        public Format getFormat() {
            return format;
        }

        public void setFormat(Format format) {
            this.format = format;
        }

        public Map<String, ErrorMapping> getErrorMapping() {
            return errorMapping;
        }

        public void setErrorMapping(Map<String, ErrorMapping> errorMapping) {
            this.errorMapping = errorMapping;
        }
    }

    public static class Cluster {

        private String mq = "redis";
        private String cache = "redis";
        private String lock = "redis";
        private String map = "redis";
        private String election = "redis";

        private Config config = new Config();

        public String getMq() {
            return mq;
        }

        public void setMq(String mq) {
            this.mq = mq;
        }

        public String getCache() {
            return cache;
        }

        public void setCache(String cache) {
            this.cache = cache;
        }

        public String getLock() {
            return lock;
        }

        public void setLock(String lock) {
            this.lock = lock;
        }

        public String getMap() {
            return map;
        }

        public void setMap(String map) {
            this.map = map;
        }

        public String getElection() {
            return election;
        }

        public void setElection(String election) {
            this.election = election;
        }

        public Config getConfig() {
            return config;
        }

        public void setConfig(Config config) {
            this.config = config;
        }

        public static class Config {

            private int electionPeriodSec = 60;
            private boolean haEnabled = true;

            public int getElectionPeriodSec() {
                return electionPeriodSec;
            }

            public void setElectionPeriodSec(int electionPeriodSec) {
                this.electionPeriodSec = electionPeriodSec;
            }

            public boolean isHaEnabled() {
                return haEnabled;
            }

            public void setHaEnabled(boolean haEnabled) {
                this.haEnabled = haEnabled;
            }
        }
    }

    public static class Security {

        private SecurityCORS cors = new SecurityCORS();

        private String tokenFlag = "__dew_token__";

        private boolean tokenInHeader = true;

        private boolean tokenHash = false;

        private List<String> includeServices;
        private List<String> excludeServices;

        public List<String> getIncludeServices() {
            return includeServices;
        }

        public void setIncludeServices(List<String> includeServices) {
            this.includeServices = includeServices;
        }

        public List<String> getExcludeServices() {
            return excludeServices;
        }

        public void setExcludeServices(List<String> excludeServices) {
            this.excludeServices = excludeServices;
        }

        public SecurityCORS getCors() {
            return cors;
        }

        public void setCors(SecurityCORS cors) {
            this.cors = cors;
        }

        public String getTokenFlag() {
            return tokenFlag;
        }

        public void setTokenFlag(String tokenFlag) {
            this.tokenFlag = tokenFlag;
        }

        public boolean isTokenInHeader() {
            return tokenInHeader;
        }

        public void setTokenInHeader(boolean tokenInHeader) {
            this.tokenInHeader = tokenInHeader;
        }

        public boolean isTokenHash() {
            return tokenHash;
        }

        public void setTokenHash(boolean tokenHash) {
            this.tokenHash = tokenHash;
        }

        public static class SecurityCORS {

            private String allowOrigin = "*";
            private String allowMethods = "POST,GET,OPTIONS,PUT,DELETE,HEAD";
            private String allowHeaders = "x-requested-with,content-type";

            public String getAllowOrigin() {
                return allowOrigin;
            }

            public void setAllowOrigin(String allowOrigin) {
                this.allowOrigin = allowOrigin;
            }

            public String getAllowMethods() {
                return allowMethods;
            }

            public void setAllowMethods(String allowMethods) {
                this.allowMethods = allowMethods;
            }

            public String getAllowHeaders() {
                return allowHeaders;
            }

            public void setAllowHeaders(String allowHeaders) {
                this.allowHeaders = allowHeaders;
            }
        }

    }

    public static class Metric {

        private boolean enabled = true;

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }

    }

    public static class Notify {

        private String type = "DD"; // DD->钉钉 MAIL->邮件 HTTP->自定义HTTP Hook
        private Set<String> defaultReceivers = new HashSet<>();
        private Set<String> dndTimeReceivers = new HashSet<>();
        private Map<String, Object> args = new HashMap<>();
        private Strategy strategy = new Strategy();

        public static class Strategy {

            private int minIntervalSec = 0;
            private String dndTime = "";
            private int forceSendTimes = 3;

            public int getMinIntervalSec() {
                return minIntervalSec;
            }

            public void setMinIntervalSec(int minIntervalSec) {
                this.minIntervalSec = minIntervalSec;
            }

            public String getDndTime() {
                return dndTime;
            }

            public void setDndTime(String dndTime) {
                this.dndTime = dndTime;
            }

            public int getForceSendTimes() {
                return forceSendTimes;
            }

            public void setForceSendTimes(int forceSendTimes) {
                this.forceSendTimes = forceSendTimes;
            }

        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public Set<String> getDefaultReceivers() {
            return defaultReceivers;
        }

        public void setDefaultReceivers(Set<String> defaultReceivers) {
            this.defaultReceivers = defaultReceivers;
        }

        public Set<String> getDndTimeReceivers() {
            return dndTimeReceivers;
        }

        public void setDndTimeReceivers(Set<String> dndTimeReceivers) {
            this.dndTimeReceivers = dndTimeReceivers;
        }

        public Map<String, Object> getArgs() {
            return args;
        }

        public void setArgs(Map<String, Object> args) {
            this.args = args;
        }

        public Strategy getStrategy() {
            return strategy;
        }

        public void setStrategy(Strategy strategy) {
            this.strategy = strategy;
        }

    }

    public Basic getBasic() {
        return basic;
    }

    public void setBasic(Basic basic) {
        this.basic = basic;
    }

    public Cluster getCluster() {
        return cluster;
    }

    public void setCluster(Cluster cluster) {
        this.cluster = cluster;
    }

    public Security getSecurity() {
        return security;
    }

    public void setSecurity(Security security) {
        this.security = security;
    }

    public Metric getMetric() {
        return metric;
    }

    public void setMetric(Metric metric) {
        this.metric = metric;
    }

    public Map<String, Notify> getNotifies() {
        return notifies;
    }

    public void setNotifies(Map<String, Notify> notifies) {
        this.notifies = notifies;
    }
}
