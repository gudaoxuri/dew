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

package ms.dew.devops.kernel.flow.log;

import io.kubernetes.client.ApiException;
import io.kubernetes.client.models.V1Pod;
import ms.dew.devops.helper.KubeHelper;
import ms.dew.devops.helper.KubeOpt;
import ms.dew.devops.kernel.Dew;
import ms.dew.devops.kernel.flow.BasicFlow;
import ms.dew.devops.kernel.resource.KubeDeploymentBuilder;
import org.apache.maven.plugin.MojoExecutionException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class DefaultLogFlow extends BasicFlow {

    private String podName;
    private boolean follow;

    public DefaultLogFlow(String podName, boolean follow) {
        this.podName = podName;
        this.follow = follow;
    }

    protected boolean process(String flowBasePath) throws ApiException, IOException, MojoExecutionException {
        if (podName == null) {
            AtomicInteger idx = new AtomicInteger(0);
            Map<Integer, V1Pod> pods = KubeHelper.inst(Dew.Config.getCurrentProject().getId())
                    .list("app=" + Dew.Config.getCurrentProject().getAppName() + ",group=" + Dew.Config.getCurrentProject().getAppGroup() + ",version=" + Dew.Config.getCurrentProject().getGitCommit(),
                            Dew.Config.getCurrentProject().getNamespace(), KubeOpt.RES.POD, V1Pod.class)
                    .stream()
                    .filter(pod -> pod.getStatus().getContainerStatuses().stream()
                            .anyMatch(container -> container.getName().equalsIgnoreCase(KubeDeploymentBuilder.FLAG_CONTAINER_NAME)))
                    .collect(Collectors.toMap(pod -> idx.incrementAndGet(), pod -> pod));
            if (pods.size() > 1) {
                Dew.log.info("\r\n------------------ Found multiple pods, please select number: ------------------\r\n" +
                        pods.entrySet().stream()
                                .map(pod -> " < " + pod.getKey() + " > " + pod.getValue().getMetadata().getName() + " | Pod IP:" + pod.getValue().getStatus().getPodIP() + " | Node:" + pod.getValue().getStatus().getHostIP())
                                .collect(Collectors.joining("\r\n")));
                BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
                int selected = Integer.valueOf(reader.readLine().trim());
                podName = pods.get(selected).getMetadata().getName();
            } else if (pods.size() == 1) {
                podName = pods.get(1).getMetadata().getName();
            } else {
                throw new IOException("Can't found pod with name = " + KubeDeploymentBuilder.FLAG_CONTAINER_NAME);
            }
        }
        Dew.log.info("--------- Show pod : " + podName + " logs ---------");
        if (follow) {
            new Thread(() -> {
                try {
                    KubeHelper.inst(Dew.Config.getCurrentProject().getId()).log(podName, KubeDeploymentBuilder.FLAG_CONTAINER_NAME, Dew.Config.getCurrentProject().getNamespace(), System.out::println);
                } catch (ApiException e) {
                    Dew.log.error("Log error", e);
                }
            }).start();
            try {
                new CountDownLatch(1).await();
            } catch (InterruptedException ignore) {
                Thread.currentThread().interrupt();
            }
        } else {
            StringBuffer sb = new StringBuffer();
            KubeHelper.inst(Dew.Config.getCurrentProject().getId()).log(podName, KubeDeploymentBuilder.FLAG_CONTAINER_NAME, Dew.Config.getCurrentProject().getNamespace())
                    .forEach(lineMsg -> {
                        System.out.println(lineMsg);
                        sb.append(lineMsg).append("\r\n");
                    });
            Files.write(Paths.get(flowBasePath + "tail.log"), sb.toString().getBytes(StandardCharsets.UTF_8));
        }
        return true;
    }

}
