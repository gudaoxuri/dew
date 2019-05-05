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

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

/**
 * Jvm service build flow.
 *
 * @author gudaoxuri
 */
public class JvmServiceBuildFlow extends DockerBuildFlow {

    protected void preDockerBuild(FinalProjectConfig config, String flowBasePath) throws IOException {
        String preparePath = config.getMvnTargetDirectory() + "dew_prepare" + File.separator;
        Files.move(Paths.get(preparePath + "serv.jar"), Paths.get(flowBasePath + "serv.jar"), StandardCopyOption.REPLACE_EXISTING);
        $.file.copyStreamToPath(Dew.class.getResourceAsStream("/dockerfile/jvm/Dockerfile"), flowBasePath + "Dockerfile");
        $.file.copyStreamToPath(Dew.class.getResourceAsStream("/dockerfile/jvm/run-java.sh"), flowBasePath + "run-java.sh");
        $.file.copyStreamToPath(Dew.class.getResourceAsStream("/dockerfile/jvm/debug-java.sh"), flowBasePath + "debug-java.sh");
    }

}
