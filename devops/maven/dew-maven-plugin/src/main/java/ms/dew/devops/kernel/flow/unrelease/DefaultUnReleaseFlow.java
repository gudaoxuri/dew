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
import ms.dew.devops.kernel.Dew;
import ms.dew.devops.kernel.flow.BasicFlow;
import org.apache.maven.plugin.MojoExecutionException;

import java.io.IOException;
import java.util.List;

public class DefaultUnReleaseFlow extends BasicFlow {

    protected boolean process(String flowBasePath) throws ApiException, IOException, MojoExecutionException {
        KubeHelper.inst(Dew.Config.getCurrentProject().getId()).delete(Dew.Config.getCurrentProject().getAppName(), Dew.Config.getCurrentProject().getNamespace(), KubeRES.SERVICE);
        KubeHelper.inst(Dew.Config.getCurrentProject().getId()).delete(Dew.Config.getCurrentProject().getAppName(), Dew.Config.getCurrentProject().getNamespace(), KubeRES.DEPLOYMENT);
        List<V1beta1ReplicaSet> rsList = KubeHelper.inst(
                Dew.Config.getCurrentProject().getId()).list("app=" + Dew.Config.getCurrentProject().getAppName() + ",group=" + Dew.Config.getCurrentProject().getAppGroup() + ",version=" + Dew.Config.getCurrentProject().getGitCommit(),
                Dew.Config.getCurrentProject().getNamespace(), KubeRES.REPLICA_SET, V1beta1ReplicaSet.class);
        for (V1beta1ReplicaSet rs : rsList) {
            KubeHelper.inst(Dew.Config.getCurrentProject().getId()).delete(rs.getMetadata().getName(), rs.getMetadata().getNamespace(), KubeRES.REPLICA_SET);
        }
        List<V1Pod> pods = KubeHelper.inst(
                Dew.Config.getCurrentProject().getId()).list(
                "app=" + Dew.Config.getCurrentProject().getAppName() + ",group=" + Dew.Config.getCurrentProject().getAppGroup() + ",version=" + Dew.Config.getCurrentProject().getGitCommit(),
                Dew.Config.getCurrentProject().getNamespace(), KubeRES.POD, V1Pod.class);
        for (V1Pod rs : pods) {
            KubeHelper.inst(Dew.Config.getCurrentProject().getId()).delete(rs.getMetadata().getName(), rs.getMetadata().getNamespace(), KubeRES.POD);
        }
        return true;
    }

}
