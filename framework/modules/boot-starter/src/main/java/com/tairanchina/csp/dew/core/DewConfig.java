/*
 * Copyright 2019. the original author or authors.
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

package com.tairanchina.csp.dew.core;


import com.tairanchina.csp.dew.core.cluster.ha.dto.HAConfig;
import com.tairanchina.csp.dew.notification.NotifyConfig;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.HashMap;
import java.util.Map;

@ConfigurationProperties(prefix = "dew")
public class DewConfig {

    private Basic basic = new Basic();
    private Cluster cluster = new Cluster();
    private Security security = new Security();
    private Metric metric = new Metric();
    private Map<String, NotifyConfig> notifies = new HashMap<>();

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

            private Contact contact = null;

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

            public Contact getContact() {
                return contact;
            }

            public void setContact(Contact contact) {
                this.contact = contact;
            }

            public static class Contact {
                private String name;
                private String url;
                private String email;

                public String getName() {
                    return name;
                }

                public void setName(String name) {
                    this.name = name;
                }

                public String getUrl() {
                    return url;
                }

                public void setUrl(String url) {
                    this.url = url;
                }

                public String getEmail() {
                    return email;
                }

                public void setEmail(String email) {
                    this.email = email;
                }
            }

        }

        public static class Format {

            private boolean useUnityError = true;

            private String errorFlag = "__DEW_ERROR__";

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
            private HAConfig ha = new HAConfig();

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

            public HAConfig getHa() {
                return ha;
            }

            public Config setHa(HAConfig ha) {
                this.ha = ha;
                return this;
            }
        }
    }

    public static class Security {

        private SecurityCORS cors = new SecurityCORS();

        private String tokenFlag = "__dew_token__";

        private boolean tokenInHeader = true;

        private boolean tokenHash = false;

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

    public Map<String, NotifyConfig> getNotifies() {
        return notifies;
    }

    public void setNotifies(Map<String, NotifyConfig> notifies) {
        this.notifies = notifies;
    }
}
