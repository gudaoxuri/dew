/*
 * Copyright 2020. the original author or authors.
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

import io.kubernetes.client.models.*;
import group.idealworld.dew.devops.kernel.helper.KubeRES;
import group.idealworld.dew.devops.kernel.config.FinalProjectConfig;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Kubernetes horizontal pod auto scaler builder.
 *
 * @author gudaoxuri
 */
public class KubeHorizontalPodAutoscalerBuilder implements KubeResourceBuilder<V2beta2HorizontalPodAutoscaler> {

    @Override
    public V2beta2HorizontalPodAutoscaler build(FinalProjectConfig config) {
        return null;
    }

    /**
     * Build horizontal pod auto scaler.
     *
     * @param config      the project config
     * @param minReplicas the min replicas
     * @param maxReplicas the max replicas
     * @param cpuAvg      the cpu avg
     * @return the horizontal pod auto scaler
     */
    public V2beta2HorizontalPodAutoscaler build(FinalProjectConfig config, int minReplicas, int maxReplicas, int cpuAvg) {
        List<V2beta2MetricSpec> metrics = new ArrayList<>();
        if (cpuAvg != 0) {
            metrics.add(new V2beta2MetricSpecBuilder()
                    .withType("Resource")
                    .withResource(new V2beta2ResourceMetricSourceBuilder()
                            .withName("cpu")
                            .withTarget(new V2beta2MetricTargetBuilder()
                                    .withType("Utilization")
                                    .withAverageUtilization(cpuAvg)
                                    .build())
                            .build())
                    .build());
        }
        V2beta2HorizontalPodAutoscalerBuilder builder = new V2beta2HorizontalPodAutoscalerBuilder();
        builder.withKind(KubeRES.HORIZONTAL_POD_AUTOSCALER.getVal())
                .withApiVersion("autoscaling/v2beta2")
                .withMetadata(new V1ObjectMetaBuilder()
                        .withLabels(new HashMap<String, String>() {
                            {
                                put("app", config.getAppName());
                                put("group", config.getAppGroup());
                                put("provider", "dew");
                            }
                        })
                        .withName(config.getAppName())
                        .withNamespace(config.getNamespace())
                        .build())
                .withSpec(new V2beta2HorizontalPodAutoscalerSpecBuilder()
                        .withScaleTargetRef(new V2beta2CrossVersionObjectReferenceBuilder()
                                .withApiVersion("apps/v1")
                                .withKind("Deployment")
                                .withName(config.getAppName())
                                .build())
                        .withMinReplicas(minReplicas)
                        .withMaxReplicas(maxReplicas)
                        .withMetrics(metrics)
                        .build())
                .build();
        return builder.build();
    }

}
