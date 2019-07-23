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

package ms.dew.devops.kernel.plugin.deploy.kubernetes;

import com.ecfront.dew.common.Resp;
import io.kubernetes.client.ApiException;
import io.kubernetes.client.models.V1Service;
import ms.dew.devops.kernel.config.FinalProjectConfig;
import ms.dew.devops.kernel.exception.ProjectProcessException;
import ms.dew.devops.kernel.flow.release.DockerBuildFlow;
import ms.dew.devops.kernel.function.VersionController;
import ms.dew.devops.kernel.helper.KubeHelper;
import ms.dew.devops.kernel.helper.KubeRES;
import ms.dew.devops.kernel.plugin.deploy.DeployPlugin;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Kubernetes deploy plugin.
 *
 * @author gudaoxuri
 */
public class KubernetesDeployPlugin implements DeployPlugin {

    @Override
    public Resp<String> deployAble(FinalProjectConfig projectConfig) {
        if (projectConfig.getKube().getBase64Config() == null
                || projectConfig.getKube().getBase64Config().isEmpty()) {
            return Resp.badRequest("Kubernetes config NOT found");
        }
        return Resp.success("");
    }

    @Override
    public Optional<String> fetchLastDeployedVersion(String projectId, String appName, String namespace) throws ProjectProcessException {
        V1Service service;
        try {
            service = KubeHelper.inst(projectId).read(appName, namespace, KubeRES.SERVICE, V1Service.class);
            if (service == null) {
                return Optional.empty();
            }
            return Optional.of(VersionController.getAppVersion(service));
        } catch (ApiException e) {
            throw new ProjectProcessException(e.getMessage(), e);
        }
    }

    @Override
    public Optional<String> fetchLastDeployedVersionByReuseProfile(FinalProjectConfig config) throws IOException {
        return DockerBuildFlow.ReuseVersionProcessorFactory.getReuseCommit(config);
    }

    @Override
    public Map<String, String> getEnv(FinalProjectConfig projectConfig) {
        return new HashMap<>();
    }

    @Override
    public boolean useMavenProcessingMode() {
        return false;
    }

}
