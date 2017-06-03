package com.ecfront.dew.core;


import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@ConfigurationProperties(prefix = "dew")
public class DewConfig {

    private DewBasic basic = new DewBasic();
    private DewCluster cluster = new DewCluster();
    private DewSecurity security = new DewSecurity();

    public static class DewBasic {

        private String name = "";
        private String version = "1.0";
        private String desc = "";
        private String webSite = "";
        private DewDoc doc = new DewDoc();
        private DewEntity entity = new DewEntity();

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

        public static class DewDoc {

            private String basePackage = "";

            public String getBasePackage() {
                return basePackage;
            }

            public void setBasePackage(String basePackage) {
                this.basePackage = basePackage;
            }
        }

        public static class DewEntity {

            private List<String> basePackages = new ArrayList<String>() {{
                add("com.ecfront.dew");
            }};

            public List<String> getBasePackages() {
                return basePackages;
            }

            public void setBasePackages(List<String> basePackages) {
                this.basePackages = basePackages;
            }
        }

        public DewDoc getDoc() {
            return doc;
        }

        public void setDoc(DewDoc doc) {
            this.doc = doc;
        }

        public DewEntity getEntity() {
            return entity;
        }

        public void setEntity(DewEntity entity) {
            this.entity = entity;
        }

    }

    public static class DewCluster {

        private String mq = "redis";
        private String cache = "redis";
        private String dist = "redis";

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
    }

    public static class DewSecurity {

        private DewSecurityCORS cors = new DewSecurityCORS();

        private String tokenFlag = "__dew_token__";

        private boolean tokenInHeader = false;

        public DewSecurityCORS getCors() {
            return cors;
        }

        public void setCors(DewSecurityCORS cors) {
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
    }

    public static class DewSecurityCORS {

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

    public DewBasic getBasic() {
        return basic;
    }


    public void setBasic(DewBasic basic) {
        this.basic = basic;
    }

    public DewCluster getCluster() {
        return cluster;
    }

    public void setCluster(DewCluster cluster) {
        this.cluster = cluster;
    }

    public DewSecurity getSecurity() {
        return security;
    }

    public void setSecurity(DewSecurity security) {
        this.security = security;
    }
}
