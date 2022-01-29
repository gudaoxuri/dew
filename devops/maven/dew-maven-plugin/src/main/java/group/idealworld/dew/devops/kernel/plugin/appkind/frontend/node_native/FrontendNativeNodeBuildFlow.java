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

package group.idealworld.dew.devops.kernel.plugin.appkind.frontend.node_native;

import com.ecfront.dew.common.$;
import group.idealworld.dew.devops.kernel.DevOps;
import group.idealworld.dew.devops.kernel.config.FinalProjectConfig;
import group.idealworld.dew.devops.kernel.flow.release.DockerBuildFlow;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

/**
 * Native Node frontend build flow.
 *
 * @author Liuhongcheng
 */
public class FrontendNativeNodeBuildFlow extends DockerBuildFlow {

    protected void preDockerBuild(FinalProjectConfig config, String flowBasePath) throws IOException {
        if (config.getApp().getServerConfig() != null && !config.getApp().getServerConfig().isEmpty()) {
            Files.write(Paths.get(flowBasePath + "custom.conf"), config.getApp().getServerConfig().getBytes());
        } else {
            Files.write(Paths.get(flowBasePath + "custom.conf"), "".getBytes());
        }
        $.file.copyStreamToPath(DevOps.class.getResourceAsStream("/dockerfile/frontend/node_native/Dockerfile"), flowBasePath + "Dockerfile");
    }

    @Override
    protected Map<String, String> packageDockerFileArg(FinalProjectConfig config) {
        return new HashMap<>() {{
            String packageCmd = config.getApp().getPackageCmd();
            String cmd = StringUtils.isNotBlank(packageCmd) ? packageCmd : " npm run " + config.getProfile();
            put("startCmd", cmd);
        }};
    }
}
