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
import io.kubernetes.client.openapi.models.V1ObjectMeta;
import io.kubernetes.client.openapi.models.V1Service;
import io.kubernetes.client.openapi.models.V1ServicePort;
import io.kubernetes.client.openapi.models.V1ServiceSpec;
import org.codehaus.plexus.util.ReflectionUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Kubernetes service builder.
 *
 * @author gudaoxuri
 */
public class KubeServiceBuilder implements KubeResourceBuilder<V1Service> {

    @Override
    public V1Service build(FinalProjectConfig config) {

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
        labels.put("expose", "true");
        labels.putAll(config.getApp().getLabels());

        Map<String, String> selectorLabels = new HashMap<>(labels);
        selectorLabels.remove("version");
        selectorLabels.remove("expose");
        selectorLabels.remove("provider");

        List<V1ServicePort> ports = new ArrayList<>();
        ports.add(new V1ServicePort()
                .name("http")
                .port(config.getApp().getPort())
                .protocol("TCP")
                .targetPort(new IntOrString(config.getApp().getPort())));
        if (config.getApp().getExtendedPorts() != null) {
            config.getApp().getExtendedPorts().forEach(port -> {
                ports.add(new V1ServicePort()
                        .name(port.getName())
                        .port(port.getContainerPort())
                        .protocol(port.getProtocol())
                        .targetPort(new IntOrString(port.getContainerPort())));
            });
        }
        return new V1Service()
                .kind(KubeRES.SERVICE.getVal())
                .apiVersion("v1")
                .metadata(new V1ObjectMeta()
                        .annotations(annotations)
                        .labels(labels)
                        .name(config.getAppName())
                        .namespace(config.getNamespace()))
                .spec(new V1ServiceSpec()
                        .selector(selectorLabels)
                        .ports(ports));
    }

    /**
     * Build patch list.
     * <p>
     * 用于更新Service,Service对象由于持有IP信息，故无法使用replace做整体替换
     *
     * @param service the service
     * @return the list
     */
    public List<String> buildPatch(V1Service service) {
        List<String> patcher = new ArrayList<>();
        service.getMetadata().getAnnotations().forEach((key, value) ->
                patcher.add(
                        "{\"op\":\"replace\",\"path\":\"/metadata/annotations/"
                                + key.replaceAll("\\/", "~1")
                                + "\",\"value\":\"" + value + "\"}"));
        service.getMetadata().getLabels().forEach((key, value) ->
                patcher.add(
                        "{\"op\":\"replace\",\"path\":\"/metadata/labels/"
                                + key.replaceAll("\\/", "~1")
                                + "\",\"value\":\"" + value + "\"}"));
        service.getSpec().getPorts().forEach(port -> {
            try {
                ReflectionUtils.getVariablesAndValuesIncludingSuperclasses(port).forEach((k, v) ->
                        patcher.add("{\"op\":\"replace\",\"path\":\"/spec/ports/" + service.getSpec().getPorts().indexOf(port) + "/" + k + "\", "
                                + "\"value\": " + v + "}"));
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        });
        return patcher;
    }

    /**
     * Build debug port.
     * <p>
     *
     * @param service     the service
     * @param forwardPort the forward port
     * @param debugPort   the target port
     */
    public void buildDebugPort(V1Service service, int debugPort, int forwardPort) {
        service.getSpec().setType("NodePort");
        if (service.getSpec().getPorts().stream().noneMatch(v1ServicePort -> v1ServicePort.getPort() == forwardPort)) {
            service.getSpec().getPorts().add(new V1ServicePort()
                    .name("http-debug")
                    .port(forwardPort)
                    .protocol("TCP")
                    .targetPort(new IntOrString(forwardPort)));
        }
        if (service.getSpec().getPorts().stream().noneMatch(v1ServicePort -> v1ServicePort.getPort() == debugPort)) {
            service.getSpec().getPorts().add(new V1ServicePort()
                    .name("http-new")
                    .port(debugPort)
                    .protocol("TCP")
                    .targetPort(new IntOrString(debugPort)));
        }
    }

    /**
     * Clear debug port.
     * <p>
     *
     * @param service     the service
     * @param forwardPort the forward port
     * @param debugPort   the target port
     */
    public void clearDebugPort(V1Service service, int debugPort, int forwardPort) {
        service.getSpec().setType("ClusterIP");
        service.getSpec().getPorts().removeIf(v1ServicePort -> v1ServicePort.getPort() == debugPort || v1ServicePort.getPort() == forwardPort);
        service.getSpec().getPorts().forEach(v1ServicePort -> v1ServicePort.setNodePort(null));
    }
}
