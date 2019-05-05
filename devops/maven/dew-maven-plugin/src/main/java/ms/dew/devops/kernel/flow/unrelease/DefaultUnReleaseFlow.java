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

package ms.dew.devops.kernel.flow.unrelease;

import io.kubernetes.client.ApiException;
import io.kubernetes.client.models.V1Pod;
import io.kubernetes.client.models.V1beta1ReplicaSet;
import ms.dew.devops.helper.KubeHelper;
import ms.dew.devops.helper.KubeRES;
import ms.dew.devops.kernel.config.FinalProjectConfig;
import ms.dew.devops.kernel.flow.BasicFlow;

import java.io.IOException;
import java.util.List;

/**
 * Default un-release flow.
 *
 * @author gudaoxuri
 */
public class DefaultUnReleaseFlow extends BasicFlow {

    protected void process(FinalProjectConfig config, String flowBasePath) throws ApiException, IOException {
        // 删除 service
        KubeHelper.inst(config.getId()).delete(config.getAppName(), config.getNamespace(), KubeRES.SERVICE);
        // 删除 deployment
        KubeHelper.inst(config.getId()).delete(config.getAppName(), config.getNamespace(), KubeRES.DEPLOYMENT);
        // 删除 ReplicaSet
        List<V1beta1ReplicaSet> rsList = KubeHelper.inst(
                config.getId()).list("app=" + config.getAppName() + ",group=" + config.getAppGroup() + ",version=" + config.getGitCommit(),
                config.getNamespace(), KubeRES.REPLICA_SET, V1beta1ReplicaSet.class);
        for (V1beta1ReplicaSet rs : rsList) {
            KubeHelper.inst(config.getId()).delete(rs.getMetadata().getName(), rs.getMetadata().getNamespace(), KubeRES.REPLICA_SET);
        }
        // 删除 pod
        List<V1Pod> pods = KubeHelper.inst(
                config.getId()).list(
                "app=" + config.getAppName() + ",group=" + config.getAppGroup() + ",version=" + config.getGitCommit(),
                config.getNamespace(), KubeRES.POD, V1Pod.class);
        for (V1Pod rs : pods) {
            KubeHelper.inst(config.getId()).delete(rs.getMetadata().getName(), rs.getMetadata().getNamespace(), KubeRES.POD);
        }
    }

}
