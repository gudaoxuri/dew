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

import org.apache.hadoop.fs.CommonConfigurationKeysPublic;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.HConstants;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.ConnectionFactory;
import org.apache.hadoop.security.UserGroupInformation;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * HBase auto configuration.
 *
 * @author 迹_Jason
 */
@Configuration
@ConditionalOnClass(HBaseTemplate.class)
@EnableConfigurationProperties(HBaseProperties.class)
public class HBaseAutoConfiguration {

    /**
     *  To build the HBase configuration.
     *
     * @param hbaseProperties the hbase properties
     * @return hbase configuration
     */
    @Bean
    public org.apache.hadoop.conf.Configuration configuration(HBaseProperties hbaseProperties) {
        org.apache.hadoop.conf.Configuration conf = HBaseConfiguration.create();
        conf.set(HConstants.ZOOKEEPER_QUORUM, hbaseProperties.getZkQuorum());
        conf.set(HConstants.ZOOKEEPER_ZNODE_PARENT, hbaseProperties.getZnodeParent());
        conf.setInt(HConstants.ZOOKEEPER_CLIENT_PORT, hbaseProperties.getZkPort());
        conf.set(CommonConfigurationKeysPublic.HADOOP_SECURITY_AUTHENTICATION, hbaseProperties.getAuth().getType());
        conf.set("hbase.security.authentication", hbaseProperties.getAuth().getType());
        conf.setInt(HConstants.HBASE_CLIENT_RETRIES_NUMBER, hbaseProperties.getAuth().getHbaseClientRetriesNumber());
        conf.setInt(HConstants.HBASE_CLIENT_PAUSE, hbaseProperties.getAuth().getHbaseClientPause());
        conf.setLong(HConstants.HBASE_CLIENT_OPERATION_TIMEOUT, hbaseProperties.getAuth().getHbaseClientOperationTimeout());
        conf.setLong(HConstants.HBASE_CLIENT_SCANNER_TIMEOUT_PERIOD, hbaseProperties.getAuth().getHbaseClientScannerTimeoutPeriod());
        if ("kerberos".equalsIgnoreCase(hbaseProperties.getAuth().getType())) {
            conf.set("hbase.master.kerberos.principal", hbaseProperties.getAuth().getHbaseMasterPrincipal());
            conf.set("hbase.regionserver.kerberos.principal", hbaseProperties.getAuth().getHbaseRegionServerPrincipal());
        }
        return conf;
    }

    /**
     * Init HBase connection.
     *
     * @param hbaseProperties hbase settings properties
     * @return HBase connection
     * @throws IOException IOException
     */
    @Bean
    public Connection connection(HBaseProperties hbaseProperties, org.apache.hadoop.conf.Configuration conf) throws IOException {
        if ("kerberos".equalsIgnoreCase(hbaseProperties.getAuth().getType())) {
            System.setProperty("java.security.krb5.conf", hbaseProperties.getAuth().getKrb5());
            UserGroupInformation.setConfiguration(conf);
            UserGroupInformation.loginUserFromKeytab(hbaseProperties.getAuth().getPrincipal(), hbaseProperties.getAuth().getKeytab());
        }
        ThreadPoolExecutor poolExecutor = new ThreadPoolExecutor(200, Integer.MAX_VALUE, 60L, TimeUnit.SECONDS, new SynchronousQueue<>());
        poolExecutor.prestartCoreThread();
        return ConnectionFactory.createConnection(conf, poolExecutor);
    }

    /**
     * Init HBase Template.
     *
     * @param connection hbase connection
     * @return hbase template
     */
    @Bean
    public HBaseTemplate hBaseTemplate(Connection connection) {
        return new HBaseTemplate(connection);
    }
}
