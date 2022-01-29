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

package group.idealworld.dew.devops.kernel.flow.refresh;

import group.idealworld.dew.devops.kernel.config.FinalProjectConfig;
import group.idealworld.dew.devops.kernel.flow.BasicFlow;
import group.idealworld.dew.devops.kernel.function.VersionController;
import group.idealworld.dew.devops.kernel.helper.KubeHelper;
import group.idealworld.dew.devops.kernel.helper.KubeRES;
import group.idealworld.dew.devops.kernel.resource.KubeDeploymentBuilder;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.models.V1Deployment;

import java.util.Date;
import java.util.stream.Collectors;

/**
 * Default refresh flow.
 *
 * @author Sun
 */
public class DefaultRefreshFlow extends BasicFlow {

    @Override
    protected void process(FinalProjectConfig config, String flowBasePath) throws ApiException {

        logger.info("Restarting pods ... ");
        KubeHelper.inst(config.getId()).patch(config.getAppName(),
                KubeHelper.inst(
                        config.getId()).list("app=" + config.getAppName()
                                + ",version=" + VersionController.getAppCurrentVersion(config), config.getNamespace(),
                        KubeRES.DEPLOYMENT, V1Deployment.class)
                        .stream()
                        .filter(deploy -> config.getAppName().equals(deploy.getMetadata().getName()))
                        .flatMap(deploy -> deploy.getSpec().getTemplate().getSpec().getContainers()
                                .stream()
                                .filter(container -> container.getName().equals(KubeDeploymentBuilder.FLAG_CONTAINER_NAME))
                        )
                        .map(container -> {
                            if (container.getEnv() == null || container.getEnv().isEmpty()) {
                                return "{ \"op\": \"add\", \"path\": \"/spec/template/spec/containers/0/env\", "
                                        + "\"value\": [{\"name\":\"DEW_RESTART_DATE\",\"value\":\"" + new Date() + "\"}] }";
                            } else {
                                return "{ \"op\": \"add\", \"path\": \"/spec/template/spec/containers/0/env/0\", "
                                        + "\"value\": {\"name\":\"DEW_RESTART_DATE\",\"value\":\"" + new Date() + "\"} }";
                            }
                        })
                        .collect(Collectors.toList()), config.getNamespace(), KubeRES.DEPLOYMENT);
    }
}
