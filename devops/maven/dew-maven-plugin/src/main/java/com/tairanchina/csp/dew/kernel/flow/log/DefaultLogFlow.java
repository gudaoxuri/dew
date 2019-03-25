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

package com.tairanchina.csp.dew.kernel.flow.log;

import com.tairanchina.csp.dew.helper.KubeHelper;
import com.tairanchina.csp.dew.kernel.Dew;
import com.tairanchina.csp.dew.kernel.flow.BasicFlow;
import com.tairanchina.csp.dew.kernel.resource.KubeDeploymentBuilder;
import io.kubernetes.client.ApiException;
import io.kubernetes.client.models.V1Pod;
import org.apache.maven.plugin.MojoExecutionException;

import java.io.IOException;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;

public class DefaultLogFlow extends BasicFlow {

    private String podName;
    private boolean follow;

    public DefaultLogFlow(String podName, boolean follow) {
        this.podName = podName;
        this.follow = follow;
    }

    public boolean process() throws ApiException, IOException, MojoExecutionException {
        if (podName == null) {
            Optional<V1Pod> podOpt = KubeHelper.list("app=" + Dew.Config.getCurrentProject().getAppName() + ",group=" + Dew.Config.getCurrentProject().getAppGroup() + ",version=" + Dew.Config.getCurrentProject().getAppVersion(),
                    Dew.Config.getCurrentProject().getNamespace(), KubeHelper.RES.POD, V1Pod.class, Dew.Config.getCurrentProject().getId())
                    .stream()
                    .filter(pod -> pod.getStatus().getContainerStatuses().stream()
                            .anyMatch(container -> container.getName().equalsIgnoreCase(KubeDeploymentBuilder.FLAG_CONTAINER_NAME)))
                    .findAny();
            if (podOpt.isPresent()) {
                podName = podOpt.get().getMetadata().getName();
            } else {
                throw new IOException("Can't found pod with name = " + KubeDeploymentBuilder.FLAG_CONTAINER_NAME);
            }
        }
        Dew.log.info("--------- Show pod : " + podName + " logs ---------");
        if (follow) {
            new Thread(() -> {
                try {
                    KubeHelper.log(podName, KubeDeploymentBuilder.FLAG_CONTAINER_NAME, Dew.Config.getCurrentProject().getNamespace(), System.out::println, Dew.Config.getCurrentProject().getId());
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
            KubeHelper.log(podName, KubeDeploymentBuilder.FLAG_CONTAINER_NAME, Dew.Config.getCurrentProject().getNamespace(), Dew.Config.getCurrentProject().getId())
                    .forEach(System.out::println);
        }
        return true;
    }

}
