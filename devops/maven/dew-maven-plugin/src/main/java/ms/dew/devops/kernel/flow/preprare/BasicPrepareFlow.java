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

package ms.dew.devops.kernel.flow.preprare;

import io.kubernetes.client.ApiException;
import ms.dew.devops.helper.DockerHelper;
import ms.dew.devops.kernel.Dew;
import ms.dew.devops.kernel.flow.BasicFlow;
import org.apache.maven.plugin.MojoExecutionException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public abstract class BasicPrepareFlow extends BasicFlow {

    protected boolean prePrepareBuild(String flowBasePath) throws ApiException, IOException, MojoExecutionException {
        return true;
    }

    @Override
    protected boolean process(String flowBasePath) throws ApiException, IOException, MojoExecutionException {
        // 先判断是否存在
        if (!DockerHelper.inst(Dew.Config.getCurrentProject().getId()).registry.exist(Dew.Config.getCurrentProject().getCurrImageName())) {
            if (!prePrepareBuild(flowBasePath)) {
                Dew.log.debug("Finished,because [prePrepareBuild] is false");
                return false;
            }
        }
        return true;
    }

}
