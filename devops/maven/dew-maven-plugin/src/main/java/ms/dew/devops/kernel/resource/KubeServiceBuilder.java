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

import ms.dew.devops.helper.KubeHelper;
import ms.dew.devops.helper.KubeOpt;
import ms.dew.devops.kernel.config.FinalProjectConfig;
import ms.dew.devops.kernel.flow.BasicFlow;
import io.kubernetes.client.custom.IntOrString;
import io.kubernetes.client.models.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class KubeServiceBuilder implements KubeResourceBuilder<V1Service> {

    @Override
    public V1Service build(FinalProjectConfig config) {

        Map<String, String> annotations = new HashMap<>();
        annotations.put(BasicFlow.FLAG_KUBE_RESOURCE_GIT_COMMIT, config.getGitCommit());
        annotations.put("dew.ms/scm-url", config.getScmUrl());
        annotations.put("dew.ms/git-branch", config.getGitBranch());
        annotations.put("prometheus.io/port", config.getApp().getMetricPort() + "");
        annotations.put("prometheus.io/scrape", "true");
        if (config.getApp().isTraceLogEnabled()) {
            annotations.put("inject-jaeger-agent", "true");
            annotations.put("sidecar.jaegertracing.io/inject", "true");
        }

        Map<String, String> labels = new HashMap<>();
        labels.put("app", config.getAppName());
        labels.put("version", config.getGitCommit());
        labels.put("group", config.getAppGroup());
        labels.put("provider", "dew");
        labels.put("expose", "true");

        Map<String, String> selectorLabels = new HashMap<>(labels);
        selectorLabels.remove("version");
        selectorLabels.remove("expose");
        selectorLabels.remove("provider");

        V1ServiceBuilder builder = new V1ServiceBuilder();
        builder.withKind(KubeOpt.RES.SERVICE.getVal())
                .withApiVersion("v1")
                .withMetadata(new V1ObjectMetaBuilder()
                        .withAnnotations(annotations)
                        .withLabels(labels)
                        .withName(config.getAppName())
                        .withNamespace(config.getNamespace())
                        .build())
                .withSpec(new V1ServiceSpecBuilder()
                        .withSelector(selectorLabels)
                        .withPorts(new V1ServicePortBuilder()
                                .withName("http")
                                .withPort(config.getApp().getPort())
                                .withProtocol("TCP")
                                .withTargetPort(new IntOrString(config.getApp().getPort()))
                                .build())
                        .build())
                .build();
        return builder.build();
    }

    public List<String> buildPatch(V1Service service) {
        List<String> patcher = new ArrayList<>();
        service.getMetadata().getAnnotations().forEach((key, value) -> patcher.add("{\"op\":\"replace\",\"path\":\"/metadata/annotations/" + key.replaceAll("\\/", "~1") + "\",\"value\":\"" + value + "\"}"));
        service.getMetadata().getLabels().forEach((key, value) -> patcher.add("{\"op\":\"replace\",\"path\":\"/metadata/labels/" + key.replaceAll("\\/", "~1") + "\",\"value\":\"" + value + "\"}"));
        return patcher;
    }
}
