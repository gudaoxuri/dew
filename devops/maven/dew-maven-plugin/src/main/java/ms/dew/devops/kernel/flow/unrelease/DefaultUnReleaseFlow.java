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

import ms.dew.devops.helper.KubeHelper;
import ms.dew.devops.kernel.Dew;
import ms.dew.devops.kernel.flow.BasicFlow;
import io.kubernetes.client.ApiException;
import io.kubernetes.client.models.V1Pod;
import io.kubernetes.client.models.V1beta1ReplicaSet;
import org.apache.maven.plugin.MojoExecutionException;

import java.io.IOException;
import java.util.List;

public class DefaultUnReleaseFlow extends BasicFlow {

    protected boolean process() throws ApiException, IOException, MojoExecutionException {
        if (KubeHelper.exist(Dew.Config.getCurrentProject().getAppName(), Dew.Config.getCurrentProject().getNamespace(), KubeHelper.RES.SERVICE, Dew.Config.getCurrentProject().getId())) {
            KubeHelper.delete(Dew.Config.getCurrentProject().getAppName(), Dew.Config.getCurrentProject().getNamespace(), KubeHelper.RES.SERVICE, Dew.Config.getCurrentProject().getId());
        }
        if (KubeHelper.exist(Dew.Config.getCurrentProject().getAppName(), Dew.Config.getCurrentProject().getNamespace(), KubeHelper.RES.DEPLOYMENT, Dew.Config.getCurrentProject().getId())) {
            KubeHelper.delete(Dew.Config.getCurrentProject().getAppName(), Dew.Config.getCurrentProject().getNamespace(), KubeHelper.RES.DEPLOYMENT, Dew.Config.getCurrentProject().getId());
        }
        List<V1beta1ReplicaSet> rsList = KubeHelper.list("app=" + Dew.Config.getCurrentProject().getAppName() + ",group=" + Dew.Config.getCurrentProject().getAppGroup() + ",version=" + Dew.Config.getCurrentProject().getAppVersion(),
                Dew.Config.getCurrentProject().getNamespace(), KubeHelper.RES.REPLICA_SET, V1beta1ReplicaSet.class, Dew.Config.getCurrentProject().getId());
        for (V1beta1ReplicaSet rs : rsList) {
            KubeHelper.delete(rs.getMetadata().getName(), rs.getMetadata().getNamespace(), KubeHelper.RES.REPLICA_SET, Dew.Config.getCurrentProject().getId());
        }
        List<V1Pod> pods = KubeHelper.list("app=" + Dew.Config.getCurrentProject().getAppName() + ",group=" + Dew.Config.getCurrentProject().getAppGroup() + ",version=" + Dew.Config.getCurrentProject().getAppVersion(),
                Dew.Config.getCurrentProject().getNamespace(), KubeHelper.RES.POD, V1Pod.class, Dew.Config.getCurrentProject().getId());
        for (V1Pod rs : pods) {
            KubeHelper.delete(rs.getMetadata().getName(), rs.getMetadata().getNamespace(), KubeHelper.RES.POD, Dew.Config.getCurrentProject().getId());
        }
        return true;
    }

}
