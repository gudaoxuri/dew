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

package group.idealworld.dew.core.cluster.spi.hazelcast;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.List;

/**
 * Hazelcast config.
 *
 * @author gudaoxuri
 */
@ConfigurationProperties(prefix = "spring.hazelcast")
public class HazelcastConfig {

    private String username;
    private String password;
    private List<String> addresses = new ArrayList<>();
    // Timeout value in milliseconds for nodes to accept client connection requests.
    private int connectionTimeout = 5000;
    private int connectionAttemptLimit = 2;
    private int connectionAttemptPeriod = 3000;

    /**
     * Gets username.
     *
     * @return the username
     */
    public String getUsername() {
        return username;
    }

    /**
     * Sets username.
     *
     * @param username the username
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Gets password.
     *
     * @return the password
     */
    public String getPassword() {
        return password;
    }

    /**
     * Sets password.
     *
     * @param password the password
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Gets addresses.
     *
     * @return the addresses
     */
    public List<String> getAddresses() {
        return addresses;
    }

    /**
     * Sets addresses.
     *
     * @param addresses the addresses
     */
    public void setAddresses(List<String> addresses) {
        this.addresses = addresses;
    }

    /**
     * Gets connection timeout.
     *
     * @return the connection timeout
     */
    public int getConnectionTimeout() {
        return connectionTimeout;
    }

    /**
     * Sets connection timeout.
     *
     * @param connectionTimeout the connection timeout
     */
    public void setConnectionTimeout(int connectionTimeout) {
        this.connectionTimeout = connectionTimeout;
    }

    /**
     * Gets connection attempt limit.
     *
     * @return the connection attempt limit
     */
    public int getConnectionAttemptLimit() {
        return connectionAttemptLimit;
    }

    /**
     * Sets connection attempt limit.
     *
     * @param connectionAttemptLimit the connection attempt limit
     */
    public void setConnectionAttemptLimit(int connectionAttemptLimit) {
        this.connectionAttemptLimit = connectionAttemptLimit;
    }

    /**
     * Gets connection attempt period.
     *
     * @return the connection attempt period
     */
    public int getConnectionAttemptPeriod() {
        return connectionAttemptPeriod;
    }

    /**
     * Sets connection attempt period.
     *
     * @param connectionAttemptPeriod the connection attempt period
     */
    public void setConnectionAttemptPeriod(int connectionAttemptPeriod) {
        this.connectionAttemptPeriod = connectionAttemptPeriod;
    }
}
