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

import com.ecfront.dew.common.$;
import com.ecfront.dew.common.ReportHandler;
import ms.dew.devops.kernel.Dew;
import ms.dew.devops.kernel.config.FinalProjectConfig;
import ms.dew.devops.kernel.exception.ProcessException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Frontend prepare flow.
 *
 * @author gudaoxuri
 */
public class FrontendPrepareFlow extends BasicPrepareFlow {

    protected boolean prePrepareBuild(FinalProjectConfig config, String flowBasePath) throws IOException {
        String buildCmd = config.getApp().getBuildCmd();
        if (buildCmd == null || buildCmd.trim().isEmpty()) {
            // 使用默认命令
            buildCmd = "npm install && npm run build:" + config.getProfile();
        }
        buildCmd = "cd " + Dew.Config.getCurrentMavenProject().getBasedir() + " && " + buildCmd;
        Dew.log.debug("Build frontend, cmd : " + buildCmd);
        AtomicBoolean isSuccess = new AtomicBoolean(true);
        Future<Void> execF = $.shell.execute(buildCmd, null, null, false, false, new ReportHandler() {
            @Override
            public void errorlog(String line) {
                Dew.log.error(line);
                isSuccess.set(false);
            }

            @Override
            public void outputlog(String line) {
                Dew.log.debug(line);
            }

            @Override
            public void fail(String message) {
                Dew.log.error(message);
                isSuccess.set(false);
            }
        });
        try {
            execF.get();
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            throw new ProcessException("Frontend prepare flow error", ex);
        } catch (ExecutionException ex) {
            throw new ProcessException("Frontend prepare flow error", ex);
        }
        if (!isSuccess.get()) {
            throw new ProcessException("Build frontend error, cmd : " + buildCmd);
        }
        Files.move(Paths.get(config.getMvnDirectory() + "dist"), Paths.get(flowBasePath + "dist"), StandardCopyOption.REPLACE_EXISTING);
        return true;
    }

}
