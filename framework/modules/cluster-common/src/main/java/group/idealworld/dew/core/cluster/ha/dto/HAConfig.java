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
