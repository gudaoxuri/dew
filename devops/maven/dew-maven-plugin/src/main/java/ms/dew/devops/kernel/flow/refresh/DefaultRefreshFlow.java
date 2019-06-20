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

package ms.dew.devops.kernel.flow.refresh;

import io.kubernetes.client.ApiException;
import io.kubernetes.client.models.*;
import ms.dew.devops.kernel.config.FinalProjectConfig;
import ms.dew.devops.kernel.flow.BasicFlow;
import ms.dew.devops.kernel.helper.KubeHelper;
import ms.dew.devops.kernel.helper.KubeRES;
import ms.dew.devops.kernel.resource.KubeDeploymentBuilder;

import java.io.IOException;
import java.util.*;

public class DefaultRefreshFlow extends BasicFlow {

    @Override
    protected void process(FinalProjectConfig config, String flowBasePath) throws ApiException, IOException {

        logger.info("Restarting pods ... ");
        KubeHelper.inst(config.getId()).patch(config.getAppName(), new ArrayList<String>() {
            {
                List<ExtensionsV1beta1Deployment> deploymentList = KubeHelper.inst(
                        config.getId()).list("app=" + config.getAppName(), config.getNamespace(),
                        KubeRES.DEPLOYMENT, ExtensionsV1beta1Deployment.class);

                for (ExtensionsV1beta1Deployment deployment : deploymentList) {
                    if (config.getAppName().equals(deployment.getMetadata().getName())) {
                        deployment.getSpec().getTemplate().getSpec().getContainers().forEach(c -> {
                                    if (c.getName().equals(KubeDeploymentBuilder.FLAG_CONTAINER_NAME)) {
                                        if (c.getEnv() == null || c.getEnv().isEmpty()) {
                                            add("{ \"op\": \"add\", \"path\": \"/spec/template/spec/containers/0/env\", " +
                                                    "\"value\": [{\"name\":\"DEW_RESTART_DATE\",\"value\":\"" + new Date() + "\"}] }");
                                        } else {
                                            add("{ \"op\": \"add\", \"path\": \"/spec/template/spec/containers/0/env/0\", " +
                                                    "\"value\": {\"name\":\"DEW_RESTART_DATE\",\"value\":\"" + new Date() + "\"} }");
                                        }
                                    }
                                }
                        );
                    }
                }
            }
        }, config.getNamespace(), KubeRES.DEPLOYMENT);
    }
}
