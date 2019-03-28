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

package ms.dew.kernel.flow.build;

import ms.dew.helper.DockerHelper;
import ms.dew.kernel.Dew;
import ms.dew.kernel.flow.BasicFlow;
import io.kubernetes.client.ApiException;
import org.apache.maven.plugin.MojoExecutionException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public abstract class BasicBuildFlow extends BasicFlow {

    protected boolean preDockerBuild(String buildBasePath) throws ApiException, IOException, MojoExecutionException {
        return true;
    }

    @Override
    protected boolean process() throws ApiException, IOException, MojoExecutionException {
        // 先判断是否存在
        if (!DockerHelper.Registry.exist(Dew.Config.getCurrentProject().getCurrImageName(), Dew.Config.getCurrentProject().getId())) {
            String buildBasePath = Dew.Config.getCurrentProject().getMvnTargetDirectory() + "dew_build" + File.separator;
            Files.createDirectories(Paths.get(buildBasePath));
            Dew.log.info("Building image : " + Dew.Config.getCurrentProject().getCurrImageName());
            if (!preDockerBuild(buildBasePath)) {
                Dew.log.debug("Finished,because [preDockerBuild] is false");
                return false;
            }
            buildImage(buildBasePath);
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

    private void buildImage(String buildBasePath) {
        DockerHelper.Image.build(Dew.Config.getCurrentProject().getCurrImageName(), buildBasePath, Dew.Config.getCurrentProject().getId());
    }

    private void pushImage() {
        DockerHelper.Image.push(Dew.Config.getCurrentProject().getCurrImageName(), true, Dew.Config.getCurrentProject().getId());
    }

}
