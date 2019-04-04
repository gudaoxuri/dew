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
import ms.dew.devops.helper.KubeOpt;
import ms.dew.devops.kernel.config.FinalProjectConfig;
import ms.dew.devops.kernel.flow.BasicFlow;

import java.util.HashMap;
import java.util.Map;

public class KubeDeploymentBuilder implements KubeResourceBuilder<ExtensionsV1beta1Deployment> {

    public static final String FLAG_CONTAINER_NAME = "dew-app";

    @Override
    public ExtensionsV1beta1Deployment build(FinalProjectConfig config) {

        Map<String, String> annotations = new HashMap<>();
        annotations.put(BasicFlow.FLAG_KUBE_RESOURCE_GIT_COMMIT, config.getGitCommit());
        annotations.put("dew.ms/scm-url", config.getScmUrl());
        annotations.put("dew.ms/git-branch", config.getGitBranch());
        if (config.getApp().isTraceLogEnabled()) {
            annotations.put("inject-jaeger-agent", "true");
            annotations.put("sidecar.jaegertracing.io/inject", "true");
        }

        Map<String, String> labels = new HashMap<>();
        labels.put("app", config.getAppName());
        labels.put("version", config.getGitCommit());
        labels.put("group", config.getAppGroup());
        labels.put("provider", "dew");

        Map<String, String> selectorLabels = new HashMap<>(labels);
        selectorLabels.remove("version");
        selectorLabels.remove("provider");

        V1ContainerBuilder containerBuilder = null;
        switch (config.getKind()) {
            case JVM_SERVICE:
                containerBuilder = new V1ContainerBuilder()
                        .withName(FLAG_CONTAINER_NAME)
                        .withImage(config.getCurrImageName())
                        .withImagePullPolicy("IfNotPresent")
                        .withPorts(new V1ContainerPortBuilder()
                                        .withContainerPort(config.getApp().getPort())
                                        .withName("http")
                                        .withProtocol("TCP")
                                        .build(),
                                new V1ContainerPortBuilder()
                                        .withContainerPort(config.getApp().getMetricPort())
                                        .withName("prometheus")
                                        .withProtocol("TCP")
                                        .build())
                        .withEnv(new V1EnvVarBuilder()
                                .withName("JAVA_OPTIONS")
                                .withValue(config.getApp().getRunOptions() + " -Dspring.profiles.active=" + config.getProfile())
                                .build())
                        .withLivenessProbe(new V1ProbeBuilder()
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
                break;
            case FRONTEND:
                containerBuilder = new V1ContainerBuilder()
                        .withName(FLAG_CONTAINER_NAME)
                        .withImage(config.getCurrImageName())
                        .withImagePullPolicy("IfNotPresent")
                        .withPorts(new V1ContainerPortBuilder()
                                .withContainerPort(config.getApp().getPort())
                                .withName("http")
                                .withProtocol("TCP")
                                .build());
                break;
            // TODO
        }

        ExtensionsV1beta1DeploymentBuilder builder = new ExtensionsV1beta1DeploymentBuilder();
        builder.withKind(KubeOpt.RES.DEPLOYMENT.getVal())
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
                                        .build())
                                .build())
                        .build())
                .build();
        return builder.build();
    }
}
