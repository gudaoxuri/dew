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
