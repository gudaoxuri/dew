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

package com.tairanchina.csp.dew.helper;

import com.ecfront.dew.common.$;
import io.kubernetes.client.ApiException;
import io.kubernetes.client.models.*;
import org.apache.maven.plugin.logging.SystemStreamLog;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CountDownLatch;

public class KubeHelperTest {

    @Before
    public void before() throws IOException {
        KubeHelper.init("", new SystemStreamLog(), $.file.readAllByClassPath("kube.config", "UTF-8"));
    }

    @Test
    public void testAll() throws IOException, ApiException, InterruptedException {
        KubeHelper.delete("ns-test", KubeHelper.RES.NAME_SPACE, "");

        Assert.assertFalse(KubeHelper.exist("ns-test", KubeHelper.RES.NAME_SPACE, ""));
        KubeHelper.create($.file.readAllByClassPath("ns-test.yaml", "UTF-8"), "");
        Assert.assertTrue(KubeHelper.exist("ns-test", KubeHelper.RES.NAME_SPACE, ""));
        Assert.assertEquals("Active",
                KubeHelper.read("ns-test", KubeHelper.RES.NAME_SPACE, V1Namespace.class, "").getStatus().getPhase());

        ExtensionsV1beta1Deployment deployment = buildDeployment();
        CountDownLatch cdl = new CountDownLatch(1);
        String watchId = KubeHelper.watch((coreApi, extensionsApi, rbacAuthorizationApi,autoscalingApi)
                        -> extensionsApi.listNamespacedDeploymentCall(deployment.getMetadata().getNamespace(), null, null, null, null, "name=test-nginx", 1, null, null, Boolean.TRUE, null, null),
                resp -> {
                    System.out.printf("%s : %s%n", resp.type, $.json.toJsonString(resp.object.getStatus()));
                    if (resp.object.getStatus().getReadyReplicas() != null
                            && resp.object.getStatus().getReadyReplicas().intValue() == resp.object.getSpec().getReplicas()) {
                        cdl.countDown();
                    }
                },
                ExtensionsV1beta1Deployment.class, "");
        Assert.assertFalse(KubeHelper.exist(deployment.getMetadata().getName(), deployment.getMetadata().getNamespace(), KubeHelper.RES.DEPLOYMENT, ""));
        KubeHelper.apply(deployment, "");
        Assert.assertTrue(KubeHelper.exist(deployment.getMetadata().getName(), deployment.getMetadata().getNamespace(), KubeHelper.RES.DEPLOYMENT, ""));

        Assert.assertEquals(1, KubeHelper.list(
                "",
                deployment.getMetadata().getNamespace(),
                KubeHelper.RES.DEPLOYMENT,
                ExtensionsV1beta1Deployment.class, "").size());
        Assert.assertEquals(0, KubeHelper.list(
                "name=nginx",
                deployment.getMetadata().getNamespace(),
                KubeHelper.RES.DEPLOYMENT,
                ExtensionsV1beta1Deployment.class, "").size());
        Assert.assertEquals(1, KubeHelper.list(
                "name=test-nginx",
                deployment.getMetadata().getNamespace(),
                KubeHelper.RES.DEPLOYMENT,
                ExtensionsV1beta1Deployment.class, "").size());


        KubeHelper.patch("nginx-deployment", new ArrayList<String>() {{
            add("{\"op\":\"replace\",\"path\":\"/spec/replicas\",\"value\":2}");
            add("{\"op\":\"replace\",\"path\":\"/spec/template/spec/containers/0/image\",\"value\":\"nginx:latest\"}");
        }}, "ns-test", KubeHelper.RES.DEPLOYMENT, "");
        ExtensionsV1beta1Deployment fetchedDeployment = KubeHelper.read(deployment.getMetadata().getName(),
                deployment.getMetadata().getNamespace(),
                KubeHelper.RES.DEPLOYMENT,
                ExtensionsV1beta1Deployment.class
                , "");

        Assert.assertEquals(2, fetchedDeployment.getSpec().getReplicas().intValue());
        Assert.assertEquals("nginx:latest", fetchedDeployment.getSpec().getTemplate().getSpec().getContainers().get(0).getImage());
        Assert.assertEquals("test-nginx", fetchedDeployment.getMetadata().getLabels().get("name"));

        cdl.await();

        String podName = KubeHelper.list(
                "app=nginx",
                deployment.getMetadata().getNamespace(),
                KubeHelper.RES.POD,
                V1Pod.class, "").get(0).getMetadata().getName();

        List<String> logs = KubeHelper.log(podName, "ns-test", 2, "");
        logs.forEach(System.out::println);

        KubeHelper.stopWatch(watchId, "");
        KubeHelper.delete("ns-test", KubeHelper.RES.NAME_SPACE, "");
        Assert.assertFalse(KubeHelper.exist("ns-test", KubeHelper.RES.NAME_SPACE, ""));
    }

    private ExtensionsV1beta1Deployment buildDeployment() {
        return new ExtensionsV1beta1DeploymentBuilder()
                .withKind(KubeHelper.RES.DEPLOYMENT.getVal())
                .withApiVersion("extensions/v1beta1")
                .withMetadata(new V1ObjectMetaBuilder()
                        .withName("nginx-deployment")
                        .withNamespace("ns-test")
                        .withLabels(new HashMap<String, String>() {{
                            put("name", "test-nginx");
                        }})
                        .build())
                .withSpec(new ExtensionsV1beta1DeploymentSpecBuilder()
                        .withReplicas(1)
                        .withTemplate(new V1PodTemplateSpecBuilder()
                                .withMetadata(new V1ObjectMetaBuilder()
                                        .withLabels(new HashMap<String, String>() {{
                                            put("app", "nginx");
                                        }})
                                        .build())
                                .withSpec(new V1PodSpecBuilder()
                                        .withContainers(new V1ContainerBuilder()
                                                .withName("nginx")
                                                .withImage("nginx:1.7.9")
                                                .withPorts(new V1ContainerPortBuilder()
                                                        .withContainerPort(80)
                                                        .build()
                                                )
                                                .build())
                                        .build())
                                .build())
                        .build()).build();
    }


}