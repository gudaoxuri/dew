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

package group.idealworld.dew.devops.kernel.plugin.deploy;

import com.ecfront.dew.common.Resp;
import group.idealworld.dew.devops.kernel.config.FinalProjectConfig;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;

/**
 * 部署插件定义.
 *
 * @author gudaoxuri
 */
public interface DeployPlugin {

    /**
     * 是否可以部署判断.
     *
     * @param projectConfig the project config
     * @return the resp
     */
    Resp<String> deployAble(FinalProjectConfig projectConfig);

    /**
     * 获取部署的最新的版本.
     *
     * @param projectId the project id
     * @param appName   the app name
     * @param namespace the namespace
     * @return the optional
     */
    Optional<String> fetchLastDeployedVersion(String projectId, String appName, String namespace);

    /**
     * 获取重用环境部署的最新的版本.
     *
     * @param projectConfig the project config
     * @return 最新的版本
     * @throws IOException the IOException
     */
    Optional<String> fetchLastDeployedVersionByReuseProfile(FinalProjectConfig projectConfig) throws IOException;

    /**
     * 获取环境变量.
     *
     * @param projectConfig the project config
     * @return the env
     */
    Map<String, String> getEnv(FinalProjectConfig projectConfig);

    /**
     * 是否使用Maven自身的处理机制.
     * <p>
     * 比如自身的 install deploy 方式
     *
     * @return the result
     */
    boolean useMavenProcessingMode();

}
