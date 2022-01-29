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
import group.idealworld.dew.devops.kernel.helper.KubeRES;
import io.kubernetes.client.openapi.models.*;

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
            metrics.add(new V2beta2MetricSpec()
                    .type("Resource")
                    .resource(new V2beta2ResourceMetricSource()
                            .name("cpu")
                            .target(new V2beta2MetricTarget()
                                    .type("Utilization")
                                    .averageUtilization(cpuAvg))));
        }
        return new V2beta2HorizontalPodAutoscaler()
                .kind(KubeRES.HORIZONTAL_POD_AUTOSCALER.getVal())
                .apiVersion("autoscaling/v2beta2")
                .metadata(new V1ObjectMeta()
                        .labels(new HashMap<String, String>() {
                            {
                                put("app", config.getAppName());
                                put("group", config.getAppGroup());
                                put("provider", "dew");
                            }
                        })
                        .name(config.getAppName())
                        .namespace(config.getNamespace()))
                .spec(new V2beta2HorizontalPodAutoscalerSpec()
                        .scaleTargetRef(new V2beta2CrossVersionObjectReference()
                                .apiVersion("apps/v1")
                                .kind("Deployment")
                                .name(config.getAppName()))
                        .minReplicas(minReplicas)
                        .maxReplicas(maxReplicas)
                        .metrics(metrics));
    }

}
