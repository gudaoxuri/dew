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
import org.apache.maven.plugin.MojoExecutionException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;

public class FrontendPrepareFlow extends BasicPrepareFlow {

    protected boolean prePrepareBuild(String flowBasePath) throws IOException, MojoExecutionException {
        String buildCmd = Dew.Config.getCurrentProject().getApp().getBuildCmd();
        if (buildCmd == null || buildCmd.trim().isEmpty()) {
            buildCmd = "npm install && npm run build:" + Dew.Config.getCurrentProject().getProfile();
        }
        Dew.log.debug("Build frontend, cmd : " + buildCmd);
        AtomicBoolean isSuccess = new AtomicBoolean(false);
        Future<Void> execF = $.shell.execute(buildCmd, "build complete", null, false, false, new ReportHandler() {
            @Override
            public void errorlog(String line) {
                Dew.log.error(line);
            }

            @Override
            public void outputlog(String line) {
                Dew.log.debug(line);
            }

            @Override
            public void success() {
                isSuccess.set(true);
            }
        });
        try {
            execF.get();
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(ex);
        } catch (ExecutionException ex) {
            throw new RuntimeException(ex);
        }
        if (!isSuccess.get()) {
            throw new MojoExecutionException("Build frontend error, cmd : " + buildCmd);
        }
        Files.move(Paths.get(Dew.Config.getCurrentProject().getMvnDirectory() + "dist"), Paths.get(flowBasePath + "dist"), StandardCopyOption.REPLACE_EXISTING);
        return true;
    }

}
