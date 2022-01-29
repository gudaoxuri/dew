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

package group.idealworld.dew.devops.it;

import group.idealworld.dew.devops.kernel.helper.DockerHelper;
import group.idealworld.dew.devops.kernel.helper.KubeHelper;
import group.idealworld.dew.devops.kernel.helper.KubeRES;
import group.idealworld.dew.devops.kernel.helper.YamlHelper;
import group.idealworld.dew.devops.kernel.util.DewLog;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.models.*;
import org.junit.Test;

import java.io.IOException;
import java.net.URL;

/**
 * Int and clean it.
 *
 * @author gudaoxuri
 */
public class InitAndCleanIT extends BasicProcessor {

    /**
     * Init and clean.
     *
     * @throws IOException  the io exception
     * @throws ApiException the api exception
     */
    @Test
    public void initAndClean() throws IOException, ApiException {
        logger.info("Init YamlHelper");
        YamlHelper.init(DewLog.build(this.getClass()));
        logger.info("Init KubeHelper");
        KubeHelper.init("", DewLog.build(this.getClass()), kubeConfig);
        logger.info("Init DockerHelper");
        DockerHelper.init("", DewLog.build(this.getClass()),
                dockerHost,
                dockerRegistryUrl,
                dockerRegistryUserName,
                dockerRegistryPassword);
        final String registryHost = new URL(dockerRegistryUrl).getHost();
        logger.info("Clean kubernetes ns by dew-test");
        cleanResources("dew-test");
        logger.info("Clean kubernetes ns by dew-uat");
        cleanResources("dew-uat");
        logger.info("Clean kubernetes ns by dew-prod");
        cleanResources("dew-prod");
        logger.info("Clean docker images");
        DockerHelper.inst("").image.list().stream()
                .filter(image -> image.getRepoTags() != null
                        && image.getRepoTags().length > 0
                        && image.getRepoTags()[0].startsWith(registryHost + "/dew-"))
                .forEach(image -> {
                    DockerHelper.inst("").image.remove(image.getRepoTags()[0]);
                    logger.info("Remove registry image : " + image.getRepoTags()[0]);
                    DockerHelper.inst("").registry.removeImage(image.getRepoTags()[0]);
                });
    }

    private void cleanResources(String namespaces) throws ApiException {
        KubeHelper.inst("").list("", namespaces, KubeRES.SERVICE, V1Service.class)
                .forEach(res -> {
                    try {
                        KubeHelper.inst("").delete(res.getMetadata().getName(), res.getMetadata().getNamespace(), KubeRES.SERVICE);
                    } catch (ApiException e) {
                        e.printStackTrace();
                    }
                });
        KubeHelper.inst("").list("", namespaces, KubeRES.DEPLOYMENT, V1Deployment.class)
                .forEach(res -> {
                    try {
                        KubeHelper.inst("").delete(res.getMetadata().getName(), res.getMetadata().getNamespace(), KubeRES.DEPLOYMENT);
                    } catch (ApiException e) {
                        e.printStackTrace();
                    }
                });
        KubeHelper.inst("").list("", namespaces, KubeRES.REPLICA_SET, V1ReplicaSet.class)
                .forEach(res -> {
                    try {
                        KubeHelper.inst("").delete(res.getMetadata().getName(), res.getMetadata().getNamespace(), KubeRES.REPLICA_SET);
                    } catch (ApiException e) {
                        e.printStackTrace();
                    }
                });
        KubeHelper.inst("").list("", namespaces, KubeRES.POD, V1Pod.class)
                .forEach(res -> {
                    try {
                        KubeHelper.inst("").delete(res.getMetadata().getName(), res.getMetadata().getNamespace(), KubeRES.POD);
                    } catch (ApiException e) {
                        e.printStackTrace();
                    }
                });
        KubeHelper.inst("").list("kind=version", namespaces, KubeRES.CONFIG_MAP, V1ConfigMap.class)
                .forEach(res -> {
                    try {
                        KubeHelper.inst("").delete(res.getMetadata().getName(), res.getMetadata().getNamespace(), KubeRES.CONFIG_MAP);
                    } catch (ApiException e) {
                        e.printStackTrace();
                    }
                });
        KubeHelper.inst("").list("", namespaces, KubeRES.HORIZONTAL_POD_AUTOSCALER, V2beta2HorizontalPodAutoscaler.class)
                .forEach(res -> {
                    try {
                        KubeHelper.inst("").delete(res.getMetadata().getName(), res.getMetadata().getNamespace(), KubeRES.HORIZONTAL_POD_AUTOSCALER);
                    } catch (ApiException e) {
                        e.printStackTrace();
                    }
                });
    }
}
