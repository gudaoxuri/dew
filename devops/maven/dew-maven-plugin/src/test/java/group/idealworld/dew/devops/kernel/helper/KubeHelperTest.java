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

package group.idealworld.dew.devops.kernel.helper;

import com.ecfront.dew.common.$;
import group.idealworld.dew.devops.BasicTest;
import group.idealworld.dew.devops.kernel.util.DewLog;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.models.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.stream.Collectors;

/**
 * Kube helper test.
 *
 * @author gudaoxuri
 */
public class KubeHelperTest extends BasicTest {

    /**
     * Before.
     */
    @BeforeEach
    public void before() {
        KubeHelper.init("", DewLog.build(this.getClass()), defaultKubeConfig);
    }

    /*
     *//**
     * Test.
     *
     * @throws IOException          the io exception
     * @throws ApiException         the api exception
     * @throws InterruptedException the interrupted exception
     *//*
    @Test
    public void test() throws IOException, ApiException, InterruptedException {
        KubeHelper.inst("")
                .exec("helloworld-backend-79989646f9-bs5r4", "dew-app", "dew-test",
                        new String[]{
                                "./debug-java.sh"
                        }, System.out::println);
        Thread.sleep(10000);
        KubeHelper.inst("")
                .forward("helloworld-backend-79989646f9-bs5r4", "dew-test", 5005, 9999);
        new CountDownLatch(1).await();
    }*/

    /**
     * Test all.
     *
     * @throws IOException          the io exception
     * @throws ApiException         the api exception
     * @throws InterruptedException the interrupted exception
     */
    @Test
    public void testAll() throws IOException, ApiException, InterruptedException {
        KubeHelper.inst("").delete("ns-test", KubeRES.NAME_SPACE);

        Assertions.assertFalse(KubeHelper.inst("").exist("ns-test", KubeRES.NAME_SPACE));
        KubeHelper.inst("").create($.file.readAllByClassPath("ns-test.yaml", "UTF-8"));
        Assertions.assertTrue(KubeHelper.inst("").exist("ns-test", KubeRES.NAME_SPACE));
        Assertions.assertEquals("Active",
                KubeHelper.inst("").read("ns-test", KubeRES.NAME_SPACE, V1Namespace.class).getStatus().getPhase());

        V1Deployment deployment = buildDeployment();
        CountDownLatch cdl = new CountDownLatch(1);
        final String watchId = KubeHelper.inst("").watch((coreApi, appApi, extensionsApi, rbacAuthorizationApi, autoscalingApi)
                        -> appApi
                        .listNamespacedDeploymentCall(
                                deployment.getMetadata().getNamespace(),
                                null, null, null, null, "name=test-nginx", 1, null, null, null, null,null),
                resp -> {
                    System.out.printf("%s : %s%n", resp.type, $.json.toJsonString(resp.object.getStatus()));
                    if (resp.object.getStatus().getReadyReplicas() != null
                            && resp.object.getStatus().getAvailableReplicas() != null
                            && resp.object.getStatus().getReadyReplicas() == 2
                            && resp.object.getStatus().getAvailableReplicas() == 2) {
                        try {
                            long runningPodSize = KubeHelper.inst("")
                                    .list("name=nginx", "ns-test", KubeRES.POD, V1Pod.class)
                                    .stream().filter(pod ->
                                            pod.getStatus().getPhase().equalsIgnoreCase("Running")
                                                    && pod.getStatus().getContainerStatuses().stream().allMatch(V1ContainerStatus::getReady)
                                    )
                                    .count();
                            if (2 != runningPodSize) {
                                // 之前版本没有销毁
                                Thread.sleep(1000);
                            }
                        } catch (ApiException | InterruptedException e) {
                            e.printStackTrace();
                        }
                        cdl.countDown();
                    }

                },
                V1Deployment.class);
        Assertions.assertFalse(
                KubeHelper.inst("").exist(deployment.getMetadata().getName(), deployment.getMetadata().getNamespace(), KubeRES.DEPLOYMENT));
        KubeHelper.inst("").apply(deployment);
        Assertions.assertTrue(
                KubeHelper.inst("").exist(deployment.getMetadata().getName(), deployment.getMetadata().getNamespace(), KubeRES.DEPLOYMENT));

        Assertions.assertEquals(1, KubeHelper.inst("").list(
                "",
                deployment.getMetadata().getNamespace(),
                KubeRES.DEPLOYMENT,
                V1Deployment.class).size());
        Assertions.assertEquals(0, KubeHelper.inst("").list(
                "name=nginx",
                deployment.getMetadata().getNamespace(),
                KubeRES.DEPLOYMENT,
                V1Deployment.class).size());
        Assertions.assertEquals(1, KubeHelper.inst("").list(
                "name=test-nginx",
                deployment.getMetadata().getNamespace(),
                KubeRES.DEPLOYMENT,
                V1Deployment.class).size());

        // 避免各pod的startTime相同
        Thread.sleep(1000);
        KubeHelper.inst("").patch("nginx-deployment", new ArrayList<>() {
            {
                add("{\"op\":\"replace\",\"path\":\"/spec/replicas\",\"value\":2}");
                add("{\"op\":\"replace\",\"path\":\"/spec/template/spec/containers/0/image\",\"value\":\"nginx:latest\"}");
            }
        }, "ns-test", KubeRES.DEPLOYMENT);
        V1Deployment fetchedDeployment = KubeHelper.inst("").read(deployment.getMetadata().getName(),
                deployment.getMetadata().getNamespace(),
                KubeRES.DEPLOYMENT,
                V1Deployment.class);

        Assertions.assertEquals(2, fetchedDeployment.getSpec().getReplicas().intValue());
        Assertions.assertEquals("nginx:latest", fetchedDeployment.getSpec().getTemplate().getSpec().getContainers().get(0).getImage());
        Assertions.assertEquals("test-nginx", fetchedDeployment.getMetadata().getLabels().get("name"));

        cdl.await();

        List<V1Pod> pods = KubeHelper.inst("").list(
                "app=nginx",
                deployment.getMetadata().getNamespace(),
                KubeRES.POD,
                V1Pod.class)
                .stream()
                .filter(pod -> pod.getStatus().getPhase().equalsIgnoreCase("Running"))
                .sorted((m1, m2) ->
                        Long.compare(m2.getStatus().getStartTime().getMillis(), m1.getStatus().getStartTime().getMillis()))
                .collect(Collectors.toList());
        String podName = pods.get(0).getMetadata().getName();
        List<String> execResult = KubeHelper.inst("").exec(podName, null, deployment.getMetadata().getNamespace(),
                new String[]{
                        "sh",
                        "-c",
                        "ls -l"
                });
        Assertions.assertTrue(execResult.get(0).contains("total"));

        /* Closeable closeable = KubeHelper.inst("").forward(podName, deployment.getMetadata().getNamespace(), 80, 8081);

        Assertions.assertTrue($.http.get("http://127.0.0.1:8081").contains("Welcome to nginx!"));
        Assertions.assertTrue($.http.get("http://127.0.0.1:8081").contains("Welcome to nginx!"));

        closeable.close();
        */
        // TODO
        /*
        Ngnix没有日志输出，程序会一直等待
        List<String> logs = KubeHelper.inst("").log(podName, "ns-test");
        logs.forEach(System.out::println);
        */


        KubeHelper.inst("").stopWatch(watchId);
        KubeHelper.inst("").delete("ns-test", KubeRES.NAME_SPACE);
        Assertions.assertFalse(KubeHelper.inst("").exist("ns-test", KubeRES.NAME_SPACE));
    }

    private V1Deployment buildDeployment() {
        return new V1DeploymentBuilder()
                .withKind(KubeRES.DEPLOYMENT.getVal())
                .withApiVersion("apps/v1")
                .withMetadata(new V1ObjectMetaBuilder()
                        .withName("nginx-deployment")
                        .withNamespace("ns-test")
                        .withLabels(new HashMap<>() {
                            {
                                put("name", "test-nginx");
                            }
                        })
                        .build())
                .withSpec(new V1DeploymentSpecBuilder()
                        .withReplicas(1)
                        .withSelector(new V1LabelSelectorBuilder()
                                .withMatchLabels(new HashMap<>() {
                                    {
                                        put("app", "nginx");
                                    }
                                }).build())
                .withTemplate(new V1PodTemplateSpecBuilder()
                        .withMetadata(new V1ObjectMetaBuilder()
                                .withLabels(new HashMap<>() {
                                    {
                                        put("app", "nginx");
                                    }
                                })
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
