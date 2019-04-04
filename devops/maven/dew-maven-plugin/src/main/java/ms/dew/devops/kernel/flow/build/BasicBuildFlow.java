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

package ms.dew.devops.kernel.flow.build;

import io.kubernetes.client.ApiException;
import ms.dew.devops.helper.DockerHelper;
import ms.dew.devops.kernel.Dew;
import ms.dew.devops.kernel.flow.BasicFlow;
import org.apache.maven.plugin.MojoExecutionException;

import java.io.IOException;

public abstract class BasicBuildFlow extends BasicFlow {

    protected boolean preDockerBuild(String flowBasePath) throws ApiException, IOException, MojoExecutionException {
        return true;
    }

    @Override
    protected boolean process(String flowBasePath) throws ApiException, IOException, MojoExecutionException {
        // 先判断是否存在
        if (!DockerHelper.inst(Dew.Config.getCurrentProject().getId()).registry.exist(Dew.Config.getCurrentProject().getCurrImageName())) {
            Dew.log.info("Building image : " + Dew.Config.getCurrentProject().getCurrImageName());
            if (!preDockerBuild(flowBasePath)) {
                Dew.log.debug("Finished,because [preDockerBuild] is false");
                return false;
            }
            buildImage(flowBasePath);
            if (Dew.Config.getCurrentProject().getDocker().getRegistryUrl() == null
                    || Dew.Config.getCurrentProject().getDocker().getRegistryUrl().isEmpty()) {
                Dew.log.warn("Not found docker registry url and push is ignored, which is mostly used for stand-alone testing");
            } else {
                Dew.log.info("Pushing image : " + Dew.Config.getCurrentProject().getCurrImageName());
                pushImage();
            }
        } else {
            Dew.log.info("Ignore build, because image " + Dew.Config.getCurrentProject().getCurrImageName() + " already exist");
        }
        return true;
    }

    private void buildImage(String flowBasePath) {
        DockerHelper.inst(Dew.Config.getCurrentProject().getId()).image.build(Dew.Config.getCurrentProject().getCurrImageName(), flowBasePath);
    }

    private void pushImage() {
        DockerHelper.inst(Dew.Config.getCurrentProject().getId()).image.push(Dew.Config.getCurrentProject().getCurrImageName(), true);
    }

}
