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

package group.idealworld.dew.devops.maven.mojo;

import group.idealworld.dew.devops.kernel.DevOps;
import io.kubernetes.client.openapi.ApiException;
import org.apache.maven.plugins.annotations.Mojo;

import java.io.IOException;

/**
 * Scale mojo.
 *
 * @author gudaoxuri
 */
@Mojo(name = "scale")
public class ScaleMojo extends BasicMojo {

    @Override
    protected boolean executeInternal() throws IOException, ApiException {
        if (!autoScale && replicas == 0) {
            logger.error("Parameter error, When autoScale disabled, dew_devops_scale_replicas can't be 0");
            return false;
        }
        if (autoScale && (minReplicas == 0 || maxReplicas == 0 || minReplicas >= maxReplicas || cpuAvg == 0)) {
            logger.error("Parameter error, Current mode is autoScale model");
            return false;
        }
        return DevOps.Config.getProjectConfig(mavenProject.getId()).getAppKindPlugin()
                .scaleFlow(replicas, autoScale, minReplicas, maxReplicas, cpuAvg)
                .exec(mavenProject.getId(), getMojoName());
    }

}
