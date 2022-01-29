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

package group.idealworld.dew.devops.kernel.flow.unrelease;

import group.idealworld.dew.devops.kernel.config.FinalProjectConfig;
import group.idealworld.dew.devops.kernel.flow.BasicFlow;
import group.idealworld.dew.devops.kernel.helper.KubeHelper;
import group.idealworld.dew.devops.kernel.helper.KubeRES;
import io.kubernetes.client.openapi.ApiException;

import java.io.IOException;

/**
 * Default un-release flow.
 *
 * @author gudaoxuri
 */
public class DefaultUnReleaseFlow extends BasicFlow {

    protected void process(FinalProjectConfig config, String flowBasePath) throws ApiException, IOException {
        // 删除 service
        KubeHelper.inst(config.getId()).delete(config.getAppName(), config.getNamespace(), KubeRES.SERVICE);
        // 删除 Deployment,ReplicaSet,Pod
        KubeHelper.inst(config.getId()).delete(config.getAppName(), config.getNamespace(), KubeRES.DEPLOYMENT);
    }

}
