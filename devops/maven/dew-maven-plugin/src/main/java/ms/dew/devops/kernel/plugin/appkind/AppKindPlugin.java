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

package ms.dew.devops.kernel.plugin.appkind;

import ms.dew.devops.kernel.config.FinalProjectConfig;
import ms.dew.devops.kernel.flow.BasicFlow;

import java.util.Map;

/**
 * App类型插件定义.
 *
 * @author gudaoxuri
 */
public interface AppKindPlugin {

    /**
     * Gets name.
     *
     * @return the name
     */
    String getName();

    /**
     * Custom config.
     *
     * @param projectConfig the project config
     */
    void customConfig(FinalProjectConfig projectConfig);

    /**
     * Prepare flow basic flow.
     *
     * @return the basic flow
     */
    BasicFlow prepareFlow();

    /**
     * Build flow basic flow.
     *
     * @return the basic flow
     */
    BasicFlow buildFlow();

    /**
     * Release flow basic flow.
     *
     * @return the basic flow
     */
    BasicFlow releaseFlow();

    /**
     * 获取环境变量.
     *
     * @param projectConfig the project config
     * @return the env
     */
    Map<String, String> getEnv(FinalProjectConfig projectConfig);

}
