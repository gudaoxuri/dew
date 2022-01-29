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

package group.idealworld.dew.devops.maven.mojo;

import group.idealworld.dew.devops.kernel.DevOps;
import group.idealworld.dew.devops.kernel.function.StatusReporter;
import io.kubernetes.client.openapi.ApiException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.ResolutionScope;

import java.io.IOException;

/**
 * Release mojo.
 *
 * @author gudaoxuri
 */
@Mojo(name = "release", defaultPhase = LifecyclePhase.DEPLOY,
        requiresDependencyResolution = ResolutionScope.COMPILE_PLUS_RUNTIME,
        threadSafe = true)
public class ReleaseMojo extends BasicMojo {

    @Override
    protected boolean executeInternal() throws IOException, ApiException {
        if (mavenSession.isParallel()) {
            StatusReporter.report();
        }
        DevOps.Config.getProjectConfig(mavenProject.getId()).getAppKindPlugin()
                .prepareFlow().exec(mavenProject.getId(), getMojoName());

        DevOps.Config.getProjectConfig(mavenProject.getId()).getAppKindPlugin()
                .buildFlow().exec(mavenProject.getId(), getMojoName());

        return DevOps.Config.getProjectConfig(mavenProject.getId()).getAppKindPlugin()
                .releaseFlow().exec(mavenProject.getId(), getMojoName());
    }
}
