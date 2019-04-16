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

package ms.dew.devops.kernel.flow.prepare;

import ms.dew.devops.kernel.config.FinalProjectConfig;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Optional;

/**
 * Frontend prepare flow.
 *
 * @author gudaoxuri
 */
public class FrontendPrepareFlow extends BasicPrepareFlow {

    @Override
    protected Optional<String> getErrorProcessPackageCmd(FinalProjectConfig config, String currentPath) {
        String cmd = config.getApp().getErrorProcessPackageCmd();
        if (cmd == null || cmd.trim().isEmpty()) {
            // 使用默认命令
            cmd = "npm install";
        }
        cmd = "cd " + currentPath + " && " + cmd;
        return Optional.of(cmd);
    }

    @Override
    protected Optional<String> getPackageCmd(FinalProjectConfig config, String currentPath) {
        String cmd = config.getApp().getPackageCmd();
        if (cmd == null || cmd.trim().isEmpty()) {
            // 使用默认命令
            cmd = "npm run build:" + config.getProfile();
        }
        cmd = "cd " + currentPath + " && " + cmd;
        return Optional.of(cmd);
    }

    protected boolean prePrepareBuild(FinalProjectConfig config, String flowBasePath) throws IOException {
        Files.move(Paths.get(config.getMvnDirectory() + "dist"), Paths.get(flowBasePath + "dist"), StandardCopyOption.REPLACE_EXISTING);
        return true;
    }

}
