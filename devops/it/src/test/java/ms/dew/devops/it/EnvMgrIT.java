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
import io.kubernetes.client.models.ExtensionsV1beta1Deployment;
import io.kubernetes.client.models.V1Pod;
import io.kubernetes.client.models.V1Service;
import io.kubernetes.client.models.V1beta1ReplicaSet;
import ms.dew.devops.helper.DockerHelper;
import ms.dew.devops.helper.KubeHelper;
import ms.dew.devops.helper.YamlHelper;
import org.apache.maven.plugin.logging.SystemStreamLog;
import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.Properties;

/**
 * @author gudaoxuri
 */
public class EnvMgrIT {

    @Test
    public void test() throws IOException, ApiException {
        System.out.println(">>>>>>>>>>>>>>>>>>>test");
    }

    //@Test
    public void initAndClean() throws IOException, ApiException {
        Properties properties = new Properties();
        properties.load(new FileInputStream(Paths.get("").toFile().getAbsolutePath() + File.separator + "devops.properties"));
        YamlHelper.init(new SystemStreamLog());
        KubeHelper.init("", new SystemStreamLog(), properties.getProperty("dew.devops.kube.config"));
        DockerHelper.init("", new SystemStreamLog(),
                properties.getProperty("dew.devops.docker.host"),
                properties.getProperty("dew.devops.docker.registry.url"),
                properties.getProperty("dew.devops.docker.registry.username"),
                properties.getProperty("dew.devops.docker.registry.password"));
        String namespaces = "dew-it";
        String registryHost = new URL(properties.getProperty("dew.devops.docker.registry.url")).getHost();
        KubeHelper.list("", namespaces, KubeHelper.RES.SERVICE, V1Service.class, "")
                .forEach(res -> {
                    try {
                        KubeHelper.delete(res.getMetadata().getName(), res.getMetadata().getNamespace(), KubeHelper.RES.SERVICE, "");
                    } catch (ApiException e) {
                        e.printStackTrace();
                    }
                });
        KubeHelper.list("", namespaces, KubeHelper.RES.DEPLOYMENT, ExtensionsV1beta1Deployment.class, "")
                .forEach(res -> {
                    try {
                        KubeHelper.delete(res.getMetadata().getName(), res.getMetadata().getNamespace(), KubeHelper.RES.DEPLOYMENT, "");
                    } catch (ApiException e) {
                        e.printStackTrace();
                    }
                });
        KubeHelper.list("", namespaces, KubeHelper.RES.REPLICA_SET, V1beta1ReplicaSet.class, "")
                .forEach(res -> {
                    try {
                        KubeHelper.delete(res.getMetadata().getName(), res.getMetadata().getNamespace(), KubeHelper.RES.REPLICA_SET, "");
                    } catch (ApiException e) {
                        e.printStackTrace();
                    }
                });
        KubeHelper.list("", namespaces, KubeHelper.RES.POD, V1Pod.class, "")
                .forEach(res -> {
                    try {
                        KubeHelper.delete(res.getMetadata().getName(), res.getMetadata().getNamespace(), KubeHelper.RES.POD, "");
                    } catch (ApiException e) {
                        e.printStackTrace();
                    }
                });
        KubeHelper.delete(namespaces, KubeHelper.RES.NAME_SPACE, "");
        DockerHelper.Image.list("").stream()
                .filter(image -> image.getRepoTags()[0].startsWith(registryHost))
                .forEach(image -> {
                    DockerHelper.Image.remove(image.getRepoTags()[0], "");
                    DockerHelper.Image.remove(image.getRepoTags()[0], "");
                });
    }
}
