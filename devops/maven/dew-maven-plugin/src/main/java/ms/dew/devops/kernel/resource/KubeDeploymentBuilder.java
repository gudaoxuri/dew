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

package ms.dew.devops.kernel.resource;

import io.kubernetes.client.custom.IntOrString;
import io.kubernetes.client.models.*;
import ms.dew.devops.kernel.config.FinalProjectConfig;
import ms.dew.devops.kernel.function.VersionController;
import ms.dew.devops.kernel.helper.KubeRES;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Kubernetes deployment builder.
 *
 * @author gudaoxuri
 */
public class KubeDeploymentBuilder implements KubeResourceBuilder<ExtensionsV1beta1Deployment> {

    public static final String FLAG_CONTAINER_NAME = "dew-app";

    @Override
    public ExtensionsV1beta1Deployment build(FinalProjectConfig config) {

        Map<String, String> annotations = new HashMap<>();
        annotations.put(VersionController.FLAG_KUBE_RESOURCE_GIT_COMMIT, config.getGitCommit());
        annotations.put("dew.ms/scm-url", config.getScmUrl());
        if (config.getApp().getTraceLogEnabled()) {
            annotations.put("sidecar.jaegertracing.io/inject", "true");
        }

        Map<String, String> labels = new HashMap<>();
        labels.put("app", config.getAppName());
        labels.put(VersionController.FLAG_KUBE_RESOURCE_APP_VERSION, config.getAppVersion());
        labels.put("group", config.getAppGroup());
        labels.put("provider", "dew");

        Map<String, String> selectorLabels = new HashMap<>(labels);
        selectorLabels.remove("version");
        selectorLabels.remove("provider");

        Map<String, String> nodeSelectors = config.getApp().getNodeSelector();
        if (config.getApp().getNodeSelector().isEmpty()) {
            nodeSelectors.put("group", "app");
        }

        V1ResourceRequirements re = new V1ResourceRequirements();
        re.setRequests(config.getApp().getContainerResourcesRequests());
        V1ContainerBuilder containerBuilder = new V1ContainerBuilder()
                .withName(FLAG_CONTAINER_NAME)
                .withImage(config.getCurrImageName())
                .withImagePullPolicy("IfNotPresent")
                .withPorts(new V1ContainerPortBuilder()
                        .withContainerPort(config.getApp().getPort())
                        .withName("http")
                        .withProtocol("TCP")
                        .build())
                .withResources(new V1ResourceRequirements()
                        .requests(config.getApp().getContainerResourcesRequests())
                        .limits(config.getApp().getContainerResourcesLimits()));
        if (!config.getApp().getVolumeMounts().isEmpty()) {
            // 装配volumeMounts配置
            containerBuilder.withVolumeMounts(new ArrayList<V1VolumeMount>() {
                {
                    config.getApp().getVolumeMounts().forEach(map -> add(new V1VolumeMountBuilder()
                            .withMountPath(map.get("mountPath"))
                            .withName(map.get("name")).build())
                    );
                }
            });
        }
        Map<String, String> env = config.getAppKindPlugin().getEnv(config);
        config.getDeployPlugin().getEnv(config).forEach((k, v) -> {
            if (env.containsKey(k)) {
                env.put(k, env.get(k) + " " + v);
            } else {
                env.put(k, v);
            }
        });
        env.putAll(config.getDeployPlugin().getEnv(config));
        if (!env.isEmpty()) {
            containerBuilder.withEnv(env.entrySet().stream().map(e ->
                    new V1EnvVarBuilder()
                            .withName(e.getKey())
                            .withValue(e.getValue())
                            .build()
            ).collect(Collectors.toList()));
        }
        // 装配volume配置
        List<V1Volume> volumes = null;
        if (!config.getApp().getVolumes().isEmpty()) {
             volumes = new ArrayList<V1Volume>() {{
                config.getApp().getVolumes().forEach(map -> add(new V1VolumeBuilder()
                        .withName(map.get("name"))
                        .withPersistentVolumeClaim(new V1PersistentVolumeClaimVolumeSource().claimName(map.get("claimName")))
                        .build())
                );
            }};
        }

        if (config.getApp().getHealthCheckEnabled()) {
            containerBuilder.withLivenessProbe(new V1ProbeBuilder()
                    .withHttpGet(new V1HTTPGetActionBuilder()
                            .withPath(config.getApp().getLivenessPath())
                            .withPort(new IntOrString(config.getApp().getPort()))
                            .withScheme("HTTP")
                            .build())
                    .withInitialDelaySeconds(config.getApp().getLivenessInitialDelaySeconds())
                    .withPeriodSeconds(config.getApp().getLivenessPeriodSeconds())
                    .withFailureThreshold(config.getApp().getLivenessFailureThreshold())
                    .build())
                    .withReadinessProbe(new V1ProbeBuilder()
                            .withHttpGet(new V1HTTPGetActionBuilder()
                                    .withPath(config.getApp().getReadinessPath())
                                    .withPort(new IntOrString(config.getApp().getPort()))
                                    .withScheme("HTTP")
                                    .build())
                            .withInitialDelaySeconds(config.getApp().getReadinessInitialDelaySeconds())
                            .withPeriodSeconds(config.getApp().getReadinessPeriodSeconds())
                            .withFailureThreshold(config.getApp().getReadinessFailureThreshold())
                            .build());
        }
        ExtensionsV1beta1DeploymentBuilder builder = new ExtensionsV1beta1DeploymentBuilder();
        builder.withKind(KubeRES.DEPLOYMENT.getVal())
                .withApiVersion("extensions/v1beta1")
                .withMetadata(new V1ObjectMetaBuilder()
                        .withAnnotations(annotations)
                        .withLabels(labels)
                        .withName(config.getAppName())
                        .withNamespace(config.getNamespace())
                        .build())
                .withSpec(new ExtensionsV1beta1DeploymentSpecBuilder()
                        .withReplicas(config.getApp().getReplicas())
                        .withRevisionHistoryLimit(config.getApp().getRevisionHistoryLimit())
                        .withSelector(new V1LabelSelectorBuilder()
                                .withMatchLabels(selectorLabels)
                                .build())
                        .withTemplate(new V1PodTemplateSpecBuilder()
                                .withMetadata(new V1ObjectMetaBuilder()
                                        .withAnnotations(annotations)
                                        .withLabels(labels)
                                        .build())
                                .withSpec(new V1PodSpecBuilder()
                                        .withContainers(containerBuilder.build())
                                        .withNodeSelector(nodeSelectors)
                                        .withVolumes(volumes)
                                        .build())
                                .build())
                        .build())
                .build();
        return builder.build();
    }

}
