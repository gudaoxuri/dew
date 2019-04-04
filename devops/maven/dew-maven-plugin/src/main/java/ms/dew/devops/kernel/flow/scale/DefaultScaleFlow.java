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

package ms.dew.devops.kernel.flow.scale;

import ms.dew.devops.helper.KubeHelper;
import ms.dew.devops.helper.KubeRES;
import ms.dew.devops.kernel.Dew;
import ms.dew.devops.kernel.flow.BasicFlow;
import ms.dew.devops.kernel.resource.KubeHorizontalPodAutoscalerBuilder;
import io.kubernetes.client.ApiException;
import org.apache.maven.plugin.MojoExecutionException;

import java.io.IOException;
import java.util.ArrayList;

public class DefaultScaleFlow extends BasicFlow {

    private int replicas;
    private boolean autoScale;
    private int minReplicas;
    private int maxReplicas;
    private int cpuAvg;
    private long tps;

    public DefaultScaleFlow(int replicas, boolean autoScale, int minReplicas, int maxReplicas, int cpuAvg, long tps) {
        this.replicas = replicas;
        this.autoScale = autoScale;
        this.minReplicas = minReplicas;
        this.maxReplicas = maxReplicas;
        this.cpuAvg = cpuAvg;
        this.tps = tps;
    }

    @Override
    protected boolean process(String flowBasePath) throws ApiException, IOException, MojoExecutionException {
        if (!autoScale) {
            Dew.log.info("Change replicas number is " + replicas);
            KubeHelper.inst(Dew.Config.getCurrentProject().getId()).patch(Dew.Config.getCurrentProject().getAppName(), new ArrayList<String>() {{
                add("{\"op\":\"replace\",\"path\":\"/spec/replicas\",\"value\":" + replicas + "}");
            }}, Dew.Config.getCurrentProject().getNamespace(), KubeRES.DEPLOYMENT);
        } else {
            Dew.log.info("Enabled auto scale between " + minReplicas + " and " + maxReplicas);
            KubeHelper.inst(Dew.Config.getCurrentProject().getId()).apply(
                    new KubeHorizontalPodAutoscalerBuilder().build(Dew.Config.getCurrentProject(), minReplicas, maxReplicas, cpuAvg, tps));
        }
        return true;
    }

}
