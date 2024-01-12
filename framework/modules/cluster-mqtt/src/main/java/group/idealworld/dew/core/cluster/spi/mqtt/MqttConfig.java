package group.idealworld.dew.core.cluster.spi.mqtt;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Dew Mqtt config.
 *
 * @author gudaoxuri
 */
@Configuration
@ConfigurationProperties(prefix = "dew.mw.mqtt")
public class MqttConfig {

    private String broker;
    private String clientId;
    private String persistence = "";
    private String userName = "";
    private String password = "";
    private Integer timeoutSec;
    private Integer keepAliveIntervalSec;
    private Boolean cleanSession = true;

    public String getBroker() {
        return broker;
    }

    public void setBroker(String broker) {
        this.broker = broker;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getPersistence() {
        return persistence;
    }

    public void setPersistence(String persistence) {
        this.persistence = persistence;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Integer getTimeoutSec() {
        return timeoutSec;
    }

    public void setTimeoutSec(Integer timeoutSec) {
        this.timeoutSec = timeoutSec;
    }

    public Integer getKeepAliveIntervalSec() {
        return keepAliveIntervalSec;
    }

    public void setKeepAliveIntervalSec(Integer keepAliveIntervalSec) {
        this.keepAliveIntervalSec = keepAliveIntervalSec;
    }

    public Boolean getCleanSession() {
        return cleanSession;
    }

    public void setCleanSession(Boolean cleanSession) {
        this.cleanSession = cleanSession;
    }
}
