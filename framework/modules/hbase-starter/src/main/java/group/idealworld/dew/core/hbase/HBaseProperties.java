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

package group.idealworld.dew.core.hbase;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * HBase Settings.
 *
 * @author 迹_Jason
 */
@ConfigurationProperties(prefix = "spring.hbase")
public class HBaseProperties {

    /**
     * Zookeeper Quorum.
     */
    private String zkQuorum;
    /**
     * Zookeeper Port.  Default value is  [2181].
     */
    private int zkPort = 2181;
    /**
     * Zookeeper znode parent. Default value is  [/hbase-unsecure].
     */
    private String znodeParent = "/hbase";
    /**
     * HBase authentication setting.
     */
    private Auth auth = new Auth();

    /**
     * Gets zk quorum.
     *
     * @return the zk quorum
     */
    public String getZkQuorum() {
        return zkQuorum;
    }

    /**
     * Sets zk quorum.
     *
     * @param zkQuorum the zk quorum
     * @return the zk quorum
     */
    public HBaseProperties setZkQuorum(String zkQuorum) {
        this.zkQuorum = zkQuorum;
        return this;
    }

    /**
     * Gets zk port.
     *
     * @return the zk port
     */
    public int getZkPort() {
        return zkPort;
    }

    /**
     * Sets zk port.
     *
     * @param zkPort the zk port
     * @return the zk port
     */
    public HBaseProperties setZkPort(int zkPort) {
        this.zkPort = zkPort;
        return this;
    }

    /**
     * Gets znode parent.
     *
     * @return the znode parent
     */
    public String getZnodeParent() {
        return znodeParent;
    }

    /**
     * Sets znode parent.
     *
     * @param znodeParent the znode parent
     * @return the znode parent
     */
    public HBaseProperties setZnodeParent(String znodeParent) {
        this.znodeParent = znodeParent;
        return this;
    }

    /**
     * Gets auth.
     *
     * @return the auth
     */
    public Auth getAuth() {
        return auth;
    }

    /**
     * Sets auth.
     *
     * @param auth the auth
     * @return the auth
     */
    public HBaseProperties setAuth(Auth auth) {
        this.auth = auth;
        return this;
    }

    /**
     * The type Auth.
     */
    public static class Auth {
        /**
         * Authentication type. Default value is [simple].
         */
        private String type = "simple";
        /**
         * [krb5.conf] file path. Default value is  [/etc/krb5.conf].
         */
        private String krb5 = "/etc/krb5.conf";
        /**
         * The principal of kerberos.
         */
        private String principal;
        /**
         * The keytab of kerberos.
         */
        private String keytab;
        /**
         * The principal of the hbase master.
         */
        private String hbaseMasterPrincipal;
        /**
         * The principal of the hbase region server.
         */
        private String hbaseRegionServerPrincipal;
        /**
         * The client retries number of the hbase. Default value is  [5].
         */
        private int hbaseClientRetriesNumber = 5;
        /**
         * The client operation time out of the hbase. Default value is  [300000].
         */
        private long hbaseClientOperationTimeout = 300000;
        /**
         * The client scanner time out of the hbase. Default value is  [60000].
         */
        private long hbaseClientScannerTimeoutPeriod = 60000;
        /**
         * The client pause of the hbase. Default value is  [30].
         */
        private int hbaseClientPause = 30;

        /**
         * Gets type.
         *
         * @return the type
         */
        public String getType() {
            return type;
        }

        /**
         * Sets type.
         *
         * @param type the type
         * @return the type
         */
        public Auth setType(String type) {
            this.type = type;
            return this;
        }

        /**
         * Gets hbase master principal.
         *
         * @return the hbase master principal
         */
        public String getHbaseMasterPrincipal() {
            return hbaseMasterPrincipal;
        }

        /**
         * Sets hbase master principal.
         *
         * @param hbaseMasterPrincipal the hbase master principal
         * @return the hbase master principal
         */
        public Auth setHbaseMasterPrincipal(String hbaseMasterPrincipal) {
            this.hbaseMasterPrincipal = hbaseMasterPrincipal;
            return this;
        }

        /**
         * Gets hbase region server principal.
         *
         * @return the hbase region server principal
         */
        public String getHbaseRegionServerPrincipal() {
            return hbaseRegionServerPrincipal;
        }

        /**
         * Sets hbase region server principal.
         *
         * @param hbaseRegionServerPrincipal the hbase region server principal
         * @return the hbase region server principal
         */
        public Auth setHbaseRegionServerPrincipal(String hbaseRegionServerPrincipal) {
            this.hbaseRegionServerPrincipal = hbaseRegionServerPrincipal;
            return this;
        }

        /**
         * Gets hbase client retries number.
         *
         * @return the hbase client retries number
         */
        public int getHbaseClientRetriesNumber() {
            return hbaseClientRetriesNumber;
        }

        /**
         * Sets hbase client retries number.
         *
         * @param hbaseClientRetriesNumber the hbase client retries number
         * @return the hbase client retries number
         */
        public Auth setHbaseClientRetriesNumber(int hbaseClientRetriesNumber) {
            this.hbaseClientRetriesNumber = hbaseClientRetriesNumber;
            return this;
        }

        /**
         * Gets hbase client operation timeout.
         *
         * @return the hbase client operation timeout
         */
        public long getHbaseClientOperationTimeout() {
            return hbaseClientOperationTimeout;
        }

        /**
         * Sets hbase client operation timeout.
         *
         * @param hbaseClientOperationTimeout the hbase client operation timeout
         * @return the hbase client operation timeout
         */
        public Auth setHbaseClientOperationTimeout(long hbaseClientOperationTimeout) {
            this.hbaseClientOperationTimeout = hbaseClientOperationTimeout;
            return this;
        }

        /**
         * Gets hbase client scanner timeout period.
         *
         * @return the hbase client scanner timeout period
         */
        public long getHbaseClientScannerTimeoutPeriod() {
            return hbaseClientScannerTimeoutPeriod;
        }

        /**
         * Sets hbase client scanner timeout period.
         *
         * @param hbaseClientScannerTimeoutPeriod the hbase client scanner timeout period
         * @return the hbase client scanner timeout period
         */
        public Auth setHbaseClientScannerTimeoutPeriod(long hbaseClientScannerTimeoutPeriod) {
            this.hbaseClientScannerTimeoutPeriod = hbaseClientScannerTimeoutPeriod;
            return this;
        }

        /**
         * Gets hbase client pause.
         *
         * @return the hbase client pause
         */
        public int getHbaseClientPause() {
            return hbaseClientPause;
        }

        /**
         * Sets hbase client pause.
         *
         * @param hbaseClientPause the hbase client pause
         * @return the hbase client pause
         */
        public Auth setHbaseClientPause(int hbaseClientPause) {
            this.hbaseClientPause = hbaseClientPause;
            return this;
        }

        /**
         * Gets krb 5.
         *
         * @return the krb 5
         */
        public String getKrb5() {
            return krb5;
        }

        /**
         * Sets krb 5.
         *
         * @param krb5 the krb 5
         * @return the krb 5
         */
        public Auth setKrb5(String krb5) {
            this.krb5 = krb5;
            return this;
        }

        /**
         * Gets principal.
         *
         * @return the principal
         */
        public String getPrincipal() {
            return principal;
        }

        /**
         * Sets principal.
         *
         * @param principal the principal
         * @return the principal
         */
        public Auth setPrincipal(String principal) {
            this.principal = principal;
            return this;
        }

        /**
         * Gets keytab.
         *
         * @return the keytab
         */
        public String getKeytab() {
            return keytab;
        }

        /**
         * Sets keytab.
         *
         * @param keytab the keytab
         * @return the keytab
         */
        public Auth setKeytab(String keytab) {
            this.keytab = keytab;
            return this;
        }
    }
}
