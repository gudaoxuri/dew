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

package ms.dew.devops.mojo;

import ms.dew.devops.kernel.Dew;
import ms.dew.devops.kernel.flow.scale.DefaultScaleFlow;
import io.kubernetes.client.ApiException;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import java.io.IOException;

@Mojo(name = "scale")
public class ScaleMojo extends BasicMojo {

    private static final String FLAG_DEW_DEVOPS_SCALE_REPLICAS = "dew.devops.scale.replicas";

    private static final String FLAG_DEW_DEVOPS_SCALE_AUTO = "dew.devops.scale.auto";
    private static final String FLAG_DEW_DEVOPS_SCALE_AUTO_REPLICAS_MIN = "dew.devops.scale.auto.minReplicas";
    private static final String FLAG_DEW_DEVOPS_SCALE_AUTO_REPLICAS_MAX = "dew.devops.scale.auto.maxReplicas";
    private static final String FLAG_DEW_DEVOPS_SCALE_AUTO_CPU_AVG = "dew.devops.scale.auto.cpu.averageUtilization";
    private static final String FLAG_DEW_DEVOPS_SCALE_AUTO_TPS = "dew.devops.scale.auto.cpu.tps";

    @Parameter(property = FLAG_DEW_DEVOPS_SCALE_REPLICAS)
    private int replicas;

    @Parameter(property = FLAG_DEW_DEVOPS_SCALE_AUTO)
    private boolean autoScale;

    @Parameter(property = FLAG_DEW_DEVOPS_SCALE_AUTO_REPLICAS_MIN)
    private int minReplicas;

    @Parameter(property = FLAG_DEW_DEVOPS_SCALE_AUTO_REPLICAS_MAX)
    private int maxReplicas;

    @Parameter(property = FLAG_DEW_DEVOPS_SCALE_AUTO_CPU_AVG)
    private int cpuAvg;

    @Parameter(property = FLAG_DEW_DEVOPS_SCALE_AUTO_TPS)
    private long tps;

    @Override
    protected boolean executeInternal() throws MojoExecutionException, MojoFailureException, IOException, ApiException {
        if (!autoScale && replicas == 0) {
            Dew.log.error("Parameter error, When autoScale disabled, " + FLAG_DEW_DEVOPS_SCALE_REPLICAS + " can't be 0");
            return false;
        }
        if (autoScale && (minReplicas == 0 || maxReplicas == 0 || minReplicas >= maxReplicas || (cpuAvg == 0 && tps == 0L))) {
            Dew.log.error("Parameter error, Current mode is autoScale model");
            return false;
        }
        return new DefaultScaleFlow(replicas, autoScale, minReplicas, maxReplicas, cpuAvg, tps).exec(getMojoName());
    }

}
