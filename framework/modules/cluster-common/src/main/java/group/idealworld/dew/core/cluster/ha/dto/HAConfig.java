package group.idealworld.dew.core.cluster.ha.dto;

/**
 * HA配置类.
 *
 * @author gudaoxuri
 */
public class HAConfig {

    // 容器环境下请选择持久卷
    private String storagePath = "./";
    private String storageName;
    private String authUsername;
    private String authPassword;

    /**
     * Gets storage path.
     *
     * @return the storage path
     */
    public String getStoragePath() {
        return storagePath;
    }

    /**
     * Sets storage path.
     *
     * @param storagePath the storage path
     * @return the storage path
     */
    public HAConfig setStoragePath(String storagePath) {
        this.storagePath = storagePath;
        return this;
    }

    /**
     * Gets storage name.
     *
     * @return the storage name
     */
    public String getStorageName() {
        return storageName;
    }

    /**
     * Sets storage name.
     *
     * @param storageName the storage name
     * @return the storage name
     */
    public HAConfig setStorageName(String storageName) {
        this.storageName = storageName;
        return this;
    }

    /**
     * Gets auth username.
     *
     * @return the auth username
     */
    public String getAuthUsername() {
        return authUsername;
    }

    /**
     * Sets auth username.
     *
     * @param authUsername the auth username
     * @return the auth username
     */
    public HAConfig setAuthUsername(String authUsername) {
        this.authUsername = authUsername;
        return this;
    }

    /**
     * Gets auth password.
     *
     * @return the auth password
     */
    public String getAuthPassword() {
        return authPassword;
    }

    /**
     * Sets auth password.
     *
     * @param authPassword the auth password
     * @return the auth password
     */
    public HAConfig setAuthPassword(String authPassword) {
        this.authPassword = authPassword;
        return this;
    }
}
