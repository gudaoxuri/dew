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

package ms.dew.auth.sdk;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

@Component
@ConfigurationProperties(prefix = "dew.component.auth.sdk")
public class AuthSDKConfig {

    public static final String HTTP_ACCESS_TOKEN = "X-Access-Token";
    public static final String HTTP_USER_TOKEN = "X-User-Token";
    public static final String HTTP_URI = "X-Uri";

    private String serverUrl = "";

    private Set<String> whiteList = new HashSet<>();

    public String getServerUrl() {
        return serverUrl;
    }

    public AuthSDKConfig setServerUrl(String serverUrl) {
        this.serverUrl = serverUrl;
        return this;
    }

    public Set<String> getWhiteList() {
        if (!whiteList.contains("POST@/dew-auth/basic/access-token")) {
            whiteList.add("POST@/dew-auth/basic/access-token");
        }
        if (!whiteList.contains("GET@/dew-auth/basic/auth/validate")) {
            whiteList.add("GET@/dew-auth/basic/auth/validate");
        }
        return whiteList;
    }

    public AuthSDKConfig setWhiteList(Set<String> whiteList) {
        this.whiteList = whiteList;
        return this;
    }

}
