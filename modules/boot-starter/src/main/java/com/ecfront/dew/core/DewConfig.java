package com.ecfront.dew.core;


import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@ConfigurationProperties(prefix = "dew")
public class DewConfig {

    private Basic basic = new Basic();
    private Cluster cluster = new Cluster();
    private Security security = new Security();
    private Metric metric = new Metric();

    public static class Metric {

        private boolean enabled = true;

        private long periodSec = 600;

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }

        public long getPeriodSec() {
            return periodSec;
        }

        public void setPeriodSec(long periodSec) {
            this.periodSec = periodSec;
        }

    }

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
            private boolean reuseHttpState = false;
            // 兼容原系统设置
            private String messageFieldName = "message";

            private String codeFieldName = "code";

            public String getCodeFieldName() {
                return codeFieldName;
            }

            public void setCodeFieldName(String codeFieldName) {
                this.codeFieldName = codeFieldName;
            }

            public boolean isUseUnityError() {
                return useUnityError;
            }

            public void setUseUnityError(boolean useUnityError) {
                this.useUnityError = useUnityError;
            }

            public boolean isReuseHttpState() {
                return reuseHttpState;
            }

            public void setReuseHttpState(boolean reuseHttpState) {
                this.reuseHttpState = reuseHttpState;
            }

            public String getMessageFieldName() {
                return messageFieldName;
            }

            public void setMessageFieldName(String messageFieldName) {
                this.messageFieldName = messageFieldName;
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
        private String dist = "redis";
        private String election = "eureka";

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

        public String getDist() {
            return dist;
        }

        public void setDist(String dist) {
            this.dist = dist;
        }

        public String getElection() {
            return election;
        }

        public void setElection(String election) {
            this.election = election;
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
}
