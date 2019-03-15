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

package com.tairanchina.csp.dew.core.cluster.ha.dto;

public class HAConfig {

    // 容器环境下请选择持久卷
    private String storagePath = "./";
    private String storageName;
    private String authUsername;
    private String authPassword;

    public String getStoragePath() {
        return storagePath;
    }

    public HAConfig setStoragePath(String storagePath) {
        this.storagePath = storagePath;
        return this;
    }

    public String getStorageName() {
        return storageName;
    }

    public HAConfig setStorageName(String storageName) {
        this.storageName = storageName;
        return this;
    }

    public String getAuthUsername() {
        return authUsername;
    }

    public HAConfig setAuthUsername(String authUsername) {
        this.authUsername = authUsername;
        return this;
    }

    public String getAuthPassword() {
        return authPassword;
    }

    public HAConfig setAuthPassword(String authPassword) {
        this.authPassword = authPassword;
        return this;
    }
}
