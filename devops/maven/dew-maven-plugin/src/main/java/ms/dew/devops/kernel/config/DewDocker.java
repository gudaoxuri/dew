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

package ms.dew.devops.kernel.config;

import ms.dew.devops.exception.ConfigException;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * Dew docker.
 *
 * @author gudaoxuri
 */
public class DewDocker {

    private String host = "";
    private String registryUrl = "";
    private String registryUserName = "";
    private String registryPassword = "";
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
