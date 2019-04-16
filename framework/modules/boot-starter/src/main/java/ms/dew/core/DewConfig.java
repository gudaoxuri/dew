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

package ms.dew.core;


import ms.dew.core.cluster.ha.dto.HAConfig;
import ms.dew.notification.NotifyConfig;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Dew 核心配置.
 *
 * @author gudaoxuri
 * @author gjason
 */
@ConfigurationProperties(prefix = "dew")
public class DewConfig {

    private Basic basic = new Basic();
    private Cluster cluster = new Cluster();
    private Security security = new Security();
    private Metric metric = new Metric();
    private Map<String, NotifyConfig> notifies = new HashMap<>();

    /**
     * Gets basic.
     *
     * @return the basic
     */
    public Basic getBasic() {
        return basic;
    }

    /**
     * Sets basic.
     *
     * @param basic the basic
     */
    public void setBasic(Basic basic) {
        this.basic = basic;
    }

    /**
     * Gets cluster.
     *
     * @return the cluster
     */
    public Cluster getCluster() {
        return cluster;
    }

    /**
     * Sets cluster.
     *
     * @param cluster the cluster
     */
    public void setCluster(Cluster cluster) {
        this.cluster = cluster;
    }

    /**
     * Gets security.
     *
     * @return the security
     */
    public Security getSecurity() {
        return security;
    }

    /**
     * Sets security.
     *
     * @param security the security
     */
    public void setSecurity(Security security) {
        this.security = security;
    }

    /**
     * Gets metric.
     *
     * @return the metric
     */
    public Metric getMetric() {
        return metric;
    }

    /**
     * Sets metric.
     *
     * @param metric the metric
     */
    public void setMetric(Metric metric) {
        this.metric = metric;
    }

    /**
     * Gets notifies.
     *
     * @return the notifies
     */
    public Map<String, NotifyConfig> getNotifies() {
        return notifies;
    }

    /**
     * Sets notifies.
     *
     * @param notifies the notifies
     */
    public void setNotifies(Map<String, NotifyConfig> notifies) {
        this.notifies = notifies;
    }

    /**
     * Basic.
     */
    public static class Basic {

        private String name = "";
        private String version = "1.0";
        private String desc = "";
        private String webSite = "";

        private Doc doc = new Doc();
        private Format format = new Format();
        private Map<String, ErrorMapping> errorMapping = new HashMap<>();

        /**
         * Gets name.
         *
         * @return the name
         */
        public String getName() {
            return name;
        }

        /**
         * Sets name.
         *
         * @param name the name
         */
        public void setName(String name) {
            this.name = name;
        }

        /**
         * Gets desc.
         *
         * @return the desc
         */
        public String getDesc() {
            return desc;
        }

        /**
         * Sets desc.
         *
         * @param desc the desc
         */
        public void setDesc(String desc) {
            this.desc = desc;
        }

        /**
         * Gets version.
         *
         * @return the version
         */
        public String getVersion() {
            return version;
        }

        /**
         * Sets version.
         *
         * @param version the version
         */
        public void setVersion(String version) {
            this.version = version;
        }

        /**
         * Gets web site.
         *
         * @return the web site
         */
        public String getWebSite() {
            return webSite;
        }

        /**
         * Sets web site.
         *
         * @param webSite the web site
         */
        public void setWebSite(String webSite) {
            this.webSite = webSite;
        }

        /**
         * Gets doc.
         *
         * @return the doc
         */
        public Doc getDoc() {
            return doc;
        }

        /**
         * Sets doc.
         *
         * @param doc the doc
         */
        public void setDoc(Doc doc) {
            this.doc = doc;
        }

        /**
         * Gets format.
         *
         * @return the format
         */
        public Format getFormat() {
            return format;
        }

        /**
         * Sets format.
         *
         * @param format the format
         */
        public void setFormat(Format format) {
            this.format = format;
        }

        /**
         * Gets error mapping.
         *
         * @return the error mapping
         */
        public Map<String, ErrorMapping> getErrorMapping() {
            return errorMapping;
        }

        /**
         * Sets error mapping.
         *
         * @param errorMapping the error mapping
         */
        public void setErrorMapping(Map<String, ErrorMapping> errorMapping) {
            this.errorMapping = errorMapping;
        }

        /**
         * Doc.
         */
        public static class Doc {

            private boolean enabled = true;

            private Contact contact = null;

            private String basePackage = "";

            /**
             * Is enabled boolean.
             *
             * @return the boolean
             */
            public boolean isEnabled() {
                return enabled;
            }

            /**
             * Sets enabled.
             *
             * @param enabled the enabled
             */
            public void setEnabled(boolean enabled) {
                this.enabled = enabled;
            }

            /**
             * Gets base package.
             *
             * @return the base package
             */
            public String getBasePackage() {
                return basePackage;
            }

            /**
             * Sets base package.
             *
             * @param basePackage the base package
             */
            public void setBasePackage(String basePackage) {
                this.basePackage = basePackage;
            }

            /**
             * Gets contact.
             *
             * @return the contact
             */
            public Contact getContact() {
                return contact;
            }

            /**
             * Sets contact.
             *
             * @param contact the contact
             */
            public void setContact(Contact contact) {
                this.contact = contact;
            }

            /**
             * Contact.
             */
            public static class Contact {
                private String name;
                private String url;
                private String email;

                /**
                 * Gets name.
                 *
                 * @return the name
                 */
                public String getName() {
                    return name;
                }

                /**
                 * Sets name.
                 *
                 * @param name the name
                 */
                public void setName(String name) {
                    this.name = name;
                }

                /**
                 * Gets url.
                 *
                 * @return the url
                 */
                public String getUrl() {
                    return url;
                }

                /**
                 * Sets url.
                 *
                 * @param url the url
                 */
                public void setUrl(String url) {
                    this.url = url;
                }

                /**
                 * Gets email.
                 *
                 * @return the email
                 */
                public String getEmail() {
                    return email;
                }

                /**
                 * Sets email.
                 *
                 * @param email the email
                 */
                public void setEmail(String email) {
                    this.email = email;
                }
            }

        }

        /**
         * Format.
         */
        public static class Format {

            private boolean useUnityError = true;

            private String errorFlag = "__DEW_ERROR__";

            /**
             * Gets error flag.
             *
             * @return the error flag
             */
            public String getErrorFlag() {
                return errorFlag;
            }

            /**
             * Sets error flag.
             *
             * @param errorFlag the error flag
             */
            public void setErrorFlag(String errorFlag) {
                this.errorFlag = errorFlag;
            }

            /**
             * Is use unity error boolean.
             *
             * @return the boolean
             */
            public boolean isUseUnityError() {
                return useUnityError;
            }

            /**
             * Sets use unity error.
             *
             * @param useUnityError the use unity error
             */
            public void setUseUnityError(boolean useUnityError) {
                this.useUnityError = useUnityError;
            }
        }

        /**
         * Error mapping.
         */
        public static class ErrorMapping {

            private int httpCode;
            private String businessCode;
            private String message;

            /**
             * Gets http code.
             *
             * @return the http code
             */
            public int getHttpCode() {
                return httpCode;
            }

            /**
             * Sets http code.
             *
             * @param httpCode the http code
             */
            public void setHttpCode(int httpCode) {
                this.httpCode = httpCode;
            }

            /**
             * Gets business code.
             *
             * @return the business code
             */
            public String getBusinessCode() {
                return businessCode;
            }

            /**
             * Sets business code.
             *
             * @param businessCode the business code
             */
            public void setBusinessCode(String businessCode) {
                this.businessCode = businessCode;
            }

            /**
             * Gets message.
             *
             * @return the message
             */
            public String getMessage() {
                return message;
            }

            /**
             * Sets message.
             *
             * @param message the message
             */
            public void setMessage(String message) {
                this.message = message;
            }
        }
    }

    /**
     * Cluster.
     */
    public static class Cluster {

        private String mq = "redis";
        private String cache = "redis";
        private String lock = "redis";
        private String map = "redis";
        private String election = "redis";

        private Config config = new Config();

        /**
         * Gets mq.
         *
         * @return the mq
         */
        public String getMq() {
            return mq;
        }

        /**
         * Sets mq.
         *
         * @param mq the mq
         */
        public void setMq(String mq) {
            this.mq = mq;
        }

        /**
         * Gets cache.
         *
         * @return the cache
         */
        public String getCache() {
            return cache;
        }

        /**
         * Sets cache.
         *
         * @param cache the cache
         */
        public void setCache(String cache) {
            this.cache = cache;
        }

        /**
         * Gets lock.
         *
         * @return the lock
         */
        public String getLock() {
            return lock;
        }

        /**
         * Sets lock.
         *
         * @param lock the lock
         */
        public void setLock(String lock) {
            this.lock = lock;
        }

        /**
         * Gets map.
         *
         * @return the map
         */
        public String getMap() {
            return map;
        }

        /**
         * Sets map.
         *
         * @param map the map
         */
        public void setMap(String map) {
            this.map = map;
        }

        /**
         * Gets election.
         *
         * @return the election
         */
        public String getElection() {
            return election;
        }

        /**
         * Sets election.
         *
         * @param election the election
         */
        public void setElection(String election) {
            this.election = election;
        }

        /**
         * Gets config.
         *
         * @return the config
         */
        public Config getConfig() {
            return config;
        }

        /**
         * Sets config.
         *
         * @param config the config
         */
        public void setConfig(Config config) {
            this.config = config;
        }

        /**
         * Config.
         */
        public static class Config {

            private int electionPeriodSec = 60;
            private boolean haEnabled = false;
            private HAConfig ha = new HAConfig();

            /**
             * Gets election period sec.
             *
             * @return the election period sec
             */
            public int getElectionPeriodSec() {
                return electionPeriodSec;
            }

            /**
             * Sets election period sec.
             *
             * @param electionPeriodSec the election period sec
             */
            public void setElectionPeriodSec(int electionPeriodSec) {
                this.electionPeriodSec = electionPeriodSec;
            }

            /**
             * Is ha enabled boolean.
             *
             * @return the boolean
             */
            public boolean isHaEnabled() {
                return haEnabled;
            }

            /**
             * Sets ha enabled.
             *
             * @param haEnabled the ha enabled
             */
            public void setHaEnabled(boolean haEnabled) {
                this.haEnabled = haEnabled;
            }

            /**
             * Gets ha.
             *
             * @return the ha
             */
            public HAConfig getHa() {
                return ha;
            }

            /**
             * Sets ha.
             *
             * @param ha the ha
             * @return the ha
             */
            public Config setHa(HAConfig ha) {
                this.ha = ha;
                return this;
            }
        }
    }

    /**
     * Security.
     */
    public static class Security {

        private SecurityCORS cors = new SecurityCORS();

        private String tokenFlag = "__dew_token__";

        private boolean tokenInHeader = true;

        private boolean tokenHash = false;

        private Router router = new Router();

        private long optExpiration = 86400L;

        /**
         * Gets cors.
         *
         * @return the cors
         */
        public SecurityCORS getCors() {
            return cors;
        }

        /**
         * Sets cors.
         *
         * @param cors the cors
         */
        public void setCors(SecurityCORS cors) {
            this.cors = cors;
        }

        /**
         * Gets token flag.
         *
         * @return the token flag
         */
        public String getTokenFlag() {
            return tokenFlag;
        }

        /**
         * Sets token flag.
         *
         * @param tokenFlag the token flag
         */
        public void setTokenFlag(String tokenFlag) {
            this.tokenFlag = tokenFlag;
        }

        /**
         * Is token in header boolean.
         *
         * @return the boolean
         */
        public boolean isTokenInHeader() {
            return tokenInHeader;
        }

        /**
         * Sets token in header.
         *
         * @param tokenInHeader the token in header
         */
        public void setTokenInHeader(boolean tokenInHeader) {
            this.tokenInHeader = tokenInHeader;
        }

        /**
         * Is token hash boolean.
         *
         * @return the boolean
         */
        public boolean isTokenHash() {
            return tokenHash;
        }

        /**
         * Sets token hash.
         *
         * @param tokenHash the token hash
         */
        public void setTokenHash(boolean tokenHash) {
            this.tokenHash = tokenHash;
        }


        /**
         * Get route urls.
         *
         * @return the Router
         */
        public Router getRouter() {
            return router;
        }

        /**
         * Sets route urls.
         *
         * @param router the router info
         */
        public void setRouter(Router router) {
            this.router = router;
        }

        /**
         * Gets optExpiration.
         *
         * @return the expiration
         */
        public long getOptExpiration() {
            return optExpiration;
        }

        /**
         * Sets expiration.
         *
         * @param optExpiration the expiration
         */
        public void setOptExpiration(long optExpiration) {
            this.optExpiration = optExpiration;
        }


        /**
         * Security cors.
         */
        public static class SecurityCORS {

            private String allowOrigin = "*";
            private String allowMethods = "POST,GET,OPTIONS,PUT,DELETE,HEAD";
            private String allowHeaders = "x-requested-with,content-type";

            /**
             * Gets allow origin.
             *
             * @return the allow origin
             */
            public String getAllowOrigin() {
                return allowOrigin;
            }

            /**
             * Sets allow origin.
             *
             * @param allowOrigin the allow origin
             */
            public void setAllowOrigin(String allowOrigin) {
                this.allowOrigin = allowOrigin;
            }

            /**
             * Gets allow methods.
             *
             * @return the allow methods
             */
            public String getAllowMethods() {
                return allowMethods;
            }

            /**
             * Sets allow methods.
             *
             * @param allowMethods the allow methods
             */
            public void setAllowMethods(String allowMethods) {
                this.allowMethods = allowMethods;
            }

            /**
             * Gets allow headers.
             *
             * @return the allow headers
             */
            public String getAllowHeaders() {
                return allowHeaders;
            }

            /**
             * Sets allow headers.
             *
             * @param allowHeaders the allow headers
             */
            public void setAllowHeaders(String allowHeaders) {
                this.allowHeaders = allowHeaders;
            }
        }

        /**
         * URL Router.
         */
        public static class Router {

            private boolean enabled = false;
            private Map<String, List<String>> blackUri = new LinkedHashMap<>();

            /**
             * Is enabled boolean.
             *
             * @return the boolean
             */
            public boolean isEnabled() {
                return enabled;
            }

            /**
             * Sets enabled.
             *
             * @param enabled the enabled
             */
            public void setEnabled(boolean enabled) {
                this.enabled = enabled;
            }

            /**
             * Get black uri list.
             *
             * @return Map The request url method with black urs
             */
            public Map<String, List<String>> getBlackUri() {
                return blackUri;
            }

            /**
             * Sets black uris.
             *
             * @param blackUri black uri list
             */
            public void setBlackUri(Map<String, List<String>> blackUri) {
                this.blackUri = blackUri;
            }
        }

    }

    /**
     * Metric.
     */
    public static class Metric {

        private boolean enabled = true;

        /**
         * Is enabled boolean.
         *
         * @return the boolean
         */
        public boolean isEnabled() {
            return enabled;
        }

        /**
         * Sets enabled.
         *
         * @param enabled the enabled
         */
        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }

    }
}
