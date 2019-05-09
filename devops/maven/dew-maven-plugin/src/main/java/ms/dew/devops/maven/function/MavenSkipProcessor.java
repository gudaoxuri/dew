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

package ms.dew.devops.maven.function;

import ms.dew.devops.kernel.DevOps;
import ms.dew.devops.kernel.config.FinalProjectConfig;
import ms.dew.devops.kernel.util.ExecuteOnceProcessor;
import ms.dew.devops.maven.MavenDevOps;

/**
 * Maven跳过处理器.
 *
 * @author gudaoxuri
 */
public class MavenSkipProcessor {

    /**
     * Process.
     */
    public static void process() {
        if (ExecuteOnceProcessor.executedCheck(MavenSkipProcessor.class)) {
            return;
        }
        for (FinalProjectConfig config : DevOps.Config.getFinalConfig().getProjects().values()) {
            if (config.getDisableReuseVersion() != null && !config.getDisableReuseVersion()) {
                // 重用版本模式下强制跳过单元测试，不需要部署
                MavenDevOps.Config.setMavenProperty(config.getId(), "maven.test.skip", "true");
                MavenDevOps.Config.setMavenProperty(config.getId(), "maven.install.skip", "true");
                MavenDevOps.Config.setMavenProperty(config.getId(), "maven.deploy.skip", "true");
                continue;
            }
            if (config.getSkip()) {
                MavenDevOps.Config.setMavenProperty(config.getId(), "maven.install.skip", "true");
                MavenDevOps.Config.setMavenProperty(config.getId(), "maven.deploy.skip", "true");
                continue;
            }
            if (config.getDeployPlugin().useMavenProcessingMode()) {
                MavenDevOps.Config.setMavenProperty(config.getId(), "maven.install.skip", "false");
                MavenDevOps.Config.setMavenProperty(config.getId(), "maven.deploy.skip", "false");
            } else {
                MavenDevOps.Config.setMavenProperty(config.getId(), "maven.install.skip", "true");
                MavenDevOps.Config.setMavenProperty(config.getId(), "maven.deploy.skip", "true");
            }
        }
    }

}
