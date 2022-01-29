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

package group.idealworld.dew.devops.kernel.function;

import group.idealworld.dew.devops.kernel.config.FinalProjectConfig;
import group.idealworld.dew.devops.kernel.exception.ProjectProcessException;
import group.idealworld.dew.devops.kernel.helper.KubeHelper;
import group.idealworld.dew.devops.kernel.helper.KubeRES;
import group.idealworld.dew.devops.kernel.resource.KubeDeploymentBuilder;
import group.idealworld.dew.devops.kernel.util.DewLog;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.models.V1Pod;
import org.slf4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * Pod selector.
 *
 * @author gudaoxuri
 */
public class PodSelector {

    private static Logger logger = DewLog.build(PodSelector.class);


    /**
     * Select one pod.
     *
     * @param config         the config
     * @param defaultPodName the default pod name
     * @return selected pod name
     * @throws ApiException the api exception
     * @throws IOException  the io exception
     */
    public static String select(FinalProjectConfig config, Optional<String> defaultPodName) throws ApiException, IOException {
        if (defaultPodName.isPresent()) {
            return defaultPodName.get();
        }
        // 传入的pod name不存在，如果只有一个pod则直接使用，反之提示用户选择
        AtomicInteger idx = new AtomicInteger(0);
        Map<Integer, V1Pod> pods = KubeHelper.inst(config.getId())
                .list("app=" + config.getAppName() + ",group=" + config.getAppGroup(),
                        config.getNamespace(), KubeRES.POD, V1Pod.class)
                .stream()
                .filter(pod -> pod.getStatus().getContainerStatuses().stream()
                        .anyMatch(container -> container.getName().equalsIgnoreCase(KubeDeploymentBuilder.FLAG_CONTAINER_NAME)))
                .collect(Collectors.toMap(pod -> idx.incrementAndGet(), pod -> pod));
        if (pods.size() > 1) {
            int selected;
            if (config.getMavenSession().getGoals().stream().map(String::toLowerCase)
                    .anyMatch(s ->
                            s.contains("group.idealworld.dew:dew-maven-plugin:debug")
                                    || s.contains("dew:debug"))) {
                selected = new Random().nextInt(pods.size()) + 1;
            } else {
                logger.info("\r\n------------------ Found multiple pods, please select number: ------------------\r\n"
                        + pods.entrySet().stream()
                        .map(pod -> " < " + pod.getKey() + " > "
                                + pod.getValue().getMetadata().getName()
                                + " | Pod IP:" + pod.getValue().getStatus().getPodIP()
                                + " | Node:" + pod.getValue().getStatus().getHostIP())
                        .collect(Collectors.joining("\r\n")));
                BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
                selected = Integer.valueOf(reader.readLine().trim());
            }
            return pods.get(selected).getMetadata().getName();
        } else if (pods.size() == 1) {
            return pods.get(1).getMetadata().getName();
        } else {
            throw new ProjectProcessException("Can't found pod with label : " + "app=" + config.getAppName() + ",group=" + config.getAppGroup());
        }
    }
}
