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

import com.ecfront.dew.common.$;
import ms.dew.devops.kernel.Dew;
import ms.dew.devops.kernel.config.FinalProjectConfig;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

/**
 * Frontend build flow.
 *
 * @author gudaoxuri
 */
public class FrontendBuildFlow extends DockerBuildFlow {

    protected boolean preDockerBuild(FinalProjectConfig config, String flowBasePath) throws IOException {
        String preparePath = config.getMvnTargetDirectory() + "dew_prepare" + File.separator;
        FileUtils.deleteDirectory(new File(flowBasePath + "dist"));
        Files.move(Paths.get(preparePath + "dist"), Paths.get(flowBasePath + "dist"), StandardCopyOption.REPLACE_EXISTING);
        $.file.copyStreamToPath(Dew.class.getResourceAsStream("/dockerfile/frontend/Dockerfile"), flowBasePath + "Dockerfile");
        return true;
    }

}
