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

package ms.dew.devops.it;

import io.kubernetes.client.ApiException;
import io.kubernetes.client.models.*;
import ms.dew.devops.helper.*;
import org.apache.maven.plugin.logging.SystemStreamLog;
import org.junit.Test;

import java.io.IOException;
import java.net.URL;

/**
 * @author gudaoxuri
 */
public class IntAndCleanIT extends BasicProcessor {

    @Test
    public void initAndClean() throws IOException, ApiException {
        YamlHelper.init(new SystemStreamLog());
        KubeHelper.init("", new SystemStreamLog(), kubeConfig);
        DockerHelper.init("", new SystemStreamLog(),
                dockerHost,
                dockerRegistryUrl,
                dockerRegistryUserName,
                dockerRegistryPassword);
        String namespaces = "dew-test";
        String registryHost = new URL(dockerRegistryUrl).getHost();
        KubeHelper.inst("").list("", namespaces, KubeRES.SERVICE, V1Service.class)
                .forEach(res -> {
                    try {
                        KubeHelper.inst("").delete(res.getMetadata().getName(), res.getMetadata().getNamespace(), KubeRES.SERVICE);
                    } catch (ApiException e) {
                        e.printStackTrace();
                    }
                });
        KubeHelper.inst("").list("", namespaces, KubeRES.DEPLOYMENT, ExtensionsV1beta1Deployment.class)
                .forEach(res -> {
                    try {
                        KubeHelper.inst("").delete(res.getMetadata().getName(), res.getMetadata().getNamespace(), KubeRES.DEPLOYMENT);
                    } catch (ApiException e) {
                        e.printStackTrace();
                    }
                });
        KubeHelper.inst("").list("", namespaces, KubeRES.REPLICA_SET, V1beta1ReplicaSet.class)
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
        DockerHelper.inst("").image.list().stream()
                .filter(image -> image.getRepoTags() != null
                        && image.getRepoTags().length > 0
                        && image.getRepoTags()[0].startsWith(registryHost))
                .forEach(image -> {
                    DockerHelper.inst("").image.remove(image.getRepoTags()[0]);
                    try {
                        DockerHelper.inst("").registry.remove(image.getRepoTags()[0]);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
    }
}
