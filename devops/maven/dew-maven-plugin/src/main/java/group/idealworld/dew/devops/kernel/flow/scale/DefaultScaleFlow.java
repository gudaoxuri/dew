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

package group.idealworld.dew.devops.kernel.flow.scale;

import group.idealworld.dew.devops.kernel.config.FinalProjectConfig;
import group.idealworld.dew.devops.kernel.flow.BasicFlow;
import group.idealworld.dew.devops.kernel.helper.KubeHelper;
import group.idealworld.dew.devops.kernel.helper.KubeRES;
import group.idealworld.dew.devops.kernel.resource.KubeHorizontalPodAutoscalerBuilder;
import io.kubernetes.client.openapi.ApiException;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Default scale flow.
 *
 * @author gudaoxuri
 */
public class DefaultScaleFlow extends BasicFlow {

    private int replicas;
    private boolean autoScale;
    private int minReplicas;
    private int maxReplicas;
    private int cpuAvg;

    /**
     * Instantiates a new Default scale flow.
     *
     * @param replicas    the replicas
     * @param autoScale   the auto scale
     * @param minReplicas the min replicas
     * @param maxReplicas the max replicas
     * @param cpuAvg      the cpu avg
     */
    public DefaultScaleFlow(int replicas, boolean autoScale, int minReplicas, int maxReplicas, int cpuAvg) {
        this.replicas = replicas;
        this.autoScale = autoScale;
        this.minReplicas = minReplicas;
        this.maxReplicas = maxReplicas;
        this.cpuAvg = cpuAvg;
    }

    @Override
    protected void process(FinalProjectConfig config, String flowBasePath) throws ApiException, IOException {
        if (!autoScale) {
            logger.info("Change replicas number is " + replicas);
            KubeHelper.inst(config.getId()).patch(config.getAppName(), new ArrayList<>() {
                {
                    add("{\"op\":\"replace\",\"path\":\"/spec/replicas\",\"value\":" + replicas + "}");
                }
            }, config.getNamespace(), KubeRES.DEPLOYMENT);
        } else {
            logger.info("Enabled auto scale between " + minReplicas + " and " + maxReplicas);
            KubeHelper.inst(config.getId()).apply(
                    new KubeHorizontalPodAutoscalerBuilder().build(config, minReplicas, maxReplicas, cpuAvg));
        }
    }

}
