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

package group.idealworld.dew.devops.kernel.config;

/**
 * Dew kubernetes.
 *
 * @author gudaoxuri
 */
public class DewKube {

    // Kubernetes Base64 后的配置，使用 ``echo $(cat ~/.kube/config | base64) | tr -d " "`` 获取
    private String base64Config = "";

    /**
     * Gets base 64 config.
     *
     * @return the base 64 config
     */
    public String getBase64Config() {
        return base64Config;
    }

    /**
     * Sets base 64 config.
     *
     * @param base64Config the base 64 config
     */
    public void setBase64Config(String base64Config) {
        this.base64Config = base64Config;
    }
}
