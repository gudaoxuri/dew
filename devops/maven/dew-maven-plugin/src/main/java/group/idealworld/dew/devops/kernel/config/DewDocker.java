package group.idealworld.dew.devops.kernel.config;

import group.idealworld.dew.devops.kernel.exception.ConfigException;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * Dew docker.
 *
 * @author gudaoxuri
 */
public class DewDocker {

    // Dockerd的Host e.g. tcp://dockerd.dew.idealworld.group:2375
    private String host = "";
    // Docker Registry Url e.g. https://harbor.dew.idealworld.group/v2
    private String registryUrl = "";
    // Docker Registry 用户名
    private String registryUserName = "";
    // Docker Registry 密码
    private String registryPassword = "";
    // Docker 镜像
    // 前端项目默认使用 nginx:alpine
    // 后端服务项目默认使用 openjdk:8-alpine
    private String image = "";

    /**
     * Gets registry host.
     *
     * @return the registry host
     */
    public String getRegistryHost() {
        try {
            return new URL(registryUrl).getHost();
        } catch (MalformedURLException e) {
            throw new ConfigException("Registry host parse error", e);
        }
    }

    /**
     * Gets host.
     *
     * @return the host
     */
    public String getHost() {
        return host;
    }

    /**
     * Sets host.
     *
     * @param host the host
     */
    public void setHost(String host) {
        this.host = host;
    }

    /**
     * Gets registry url.
     *
     * @return the registry url
     */
    public String getRegistryUrl() {
        return registryUrl;
    }

    /**
     * Sets registry url.
     *
     * @param registryUrl the registry url
     */
    public void setRegistryUrl(String registryUrl) {
        this.registryUrl = registryUrl;
    }

    /**
     * Gets registry user name.
     *
     * @return the registry user name
     */
    public String getRegistryUserName() {
        return registryUserName;
    }

    /**
     * Sets registry user name.
     *
     * @param registryUserName the registry user name
     */
    public void setRegistryUserName(String registryUserName) {
        this.registryUserName = registryUserName;
    }

    /**
     * Gets registry password.
     *
     * @return the registry password
     */
    public String getRegistryPassword() {
        return registryPassword;
    }

    /**
     * Sets registry password.
     *
     * @param registryPassword the registry password
     */
    public void setRegistryPassword(String registryPassword) {
        this.registryPassword = registryPassword;
    }

    /**
     * Gets image.
     *
     * @return the image
     */
    public String getImage() {
        return image;
    }

    /**
     * Sets image.
     *
     * @param image the image
     */
    public void setImage(String image) {
        this.image = image;
    }
}
