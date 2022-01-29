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

package group.idealworld.dew.devops.kernel.resource;

import group.idealworld.dew.devops.kernel.config.FinalProjectConfig;
import group.idealworld.dew.devops.kernel.function.VersionController;
import group.idealworld.dew.devops.kernel.helper.KubeRES;
import io.kubernetes.client.custom.IntOrString;
import io.kubernetes.client.openapi.models.*;

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
public class KubeDeploymentBuilder implements KubeResourceBuilder<V1Deployment> {

    public static final String FLAG_CONTAINER_NAME = "dew-app";

    @Override
    public V1Deployment build(FinalProjectConfig config) {

        Map<String, String> annotations = new HashMap<>();
        annotations.put(VersionController.FLAG_KUBE_RESOURCE_GIT_COMMIT, config.getGitCommit());
        annotations.put("dew.idealworld.group/scm-url", config.getScmUrl());
        if (config.getApp().getTraceLogEnabled()) {
            annotations.put("sidecar.jaegertracing.io/inject", "true");
        }
        annotations.putAll(config.getApp().getAnnotations());

        Map<String, String> labels = new HashMap<>();
        labels.put("app", config.getAppName());
        labels.put(VersionController.FLAG_KUBE_RESOURCE_APP_VERSION, config.getAppVersion());
        labels.put("group", config.getAppGroup());
        labels.put("provider", "dew");
        labels.putAll(config.getApp().getLabels());

        Map<String, String> selectorLabels = new HashMap<>(labels);
        selectorLabels.remove("version");
        selectorLabels.remove("provider");

        Map<String, String> nodeSelectors = config.getApp().getNodeSelector();
        if (config.getApp().getNodeSelector().isEmpty()) {
            nodeSelectors.put("group", "app");
        }

        List<V1ContainerPort> ports = new ArrayList<>();
        ports.add(new V1ContainerPort()
                .containerPort(config.getApp().getPort())
                .name("http")
                .protocol("TCP"));
        if (config.getApp().getExtendedPorts() != null) {
            config.getApp().getExtendedPorts().forEach(port -> {
                if (port.getName() == null || port.getName().isEmpty()) {
                    port.setName("http-" + port.getContainerPort());
                }
                if (port.getProtocol() == null || port.getProtocol().isEmpty()) {
                    port.setProtocol("TCP");
                }
                ports.add(port);
            });
        }

        var container = new V1Container()
                .command(config.getApp().getContainerCmd())
                .name(FLAG_CONTAINER_NAME)
                .image(config.getCurrImageName())
                .imagePullPolicy("IfNotPresent")
                .ports(ports)
                .resources(new V1ResourceRequirements()
                        .requests(config.getApp().getContainerResourcesRequests())
                        .limits(config.getApp().getContainerResourcesLimits()));
        if (!config.getApp().getVolumeMounts().isEmpty()) {
            // 装配volumeMounts配置
            container.volumeMounts(new ArrayList<>() {
                {
                    config.getApp().getVolumeMounts().forEach(map -> add(new V1VolumeMount()
                            .mountPath(map.get("mountPath"))
                            .name(map.get("name")))
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
        env.putAll(config.getApp().getEnv());
        if (!env.isEmpty()) {
            container.env(env.entrySet().stream().map(e ->
                    new V1EnvVar()
                            .name(e.getKey())
                            .value(e.getValue())
            ).collect(Collectors.toList()));
        }
        // 装配volume配置
        List<V1Volume> volumes = null;
        if (!config.getApp().getVolumes().isEmpty()) {
            volumes = new ArrayList<>() {{
                config.getApp().getVolumes().forEach(map -> add(new V1Volume()
                        .name(map.get("name"))
                        .persistentVolumeClaim(new V1PersistentVolumeClaimVolumeSource().claimName(map.get("claimName"))))
                );
            }};
        }

        if (config.getApp().getHealthCheckEnabled()) {
            container.livenessProbe(new V1Probe()
                            .httpGet(new V1HTTPGetAction()
                                    .path(config.getApp().getLivenessPath())
                                    .port(new IntOrString(config.getApp().getHealthCheckPort()))
                                    .scheme("HTTP"))
                            .initialDelaySeconds(config.getApp().getLivenessInitialDelaySeconds())
                            .periodSeconds(config.getApp().getLivenessPeriodSeconds())
                            .failureThreshold(config.getApp().getLivenessFailureThreshold()))
                    .readinessProbe(new V1Probe()
                            .httpGet(new V1HTTPGetAction()
                                    .path(config.getApp().getReadinessPath())
                                    .port(new IntOrString(config.getApp().getHealthCheckPort()))
                                    .scheme("HTTP"))
                            .initialDelaySeconds(config.getApp().getReadinessInitialDelaySeconds())
                            .periodSeconds(config.getApp().getReadinessPeriodSeconds())
                            .failureThreshold(config.getApp().getReadinessFailureThreshold()));
        }
        return new V1Deployment()
                .kind(KubeRES.DEPLOYMENT.getVal())
                .apiVersion("apps/v1")
                .metadata(new V1ObjectMeta()
                        .annotations(annotations)
                        .labels(labels)
                        .name(config.getAppName())
                        .namespace(config.getNamespace()))
                .spec(new V1DeploymentSpec()
                        .replicas(config.getApp().getReplicas())
                        .revisionHistoryLimit(config.getApp().getRevisionHistoryLimit())
                        .selector(new V1LabelSelector()
                                .matchLabels(selectorLabels))
                        .template(new V1PodTemplateSpec()
                                .metadata(new V1ObjectMeta()
                                        .annotations(annotations)
                                        .labels(labels))
                                .spec(new V1PodSpec()
                                        .containers(new ArrayList<>() {
                                            {
                                                add(container);
                                            }
                                        })
                                        .nodeSelector(nodeSelectors)
                                        .volumes(volumes))));
    }

}
