/*
 * Copyright 2020. the original author or authors.
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

package group.idealworld.dew.sdkgen.maven;

import group.idealworld.dew.sdkgen.helper.MavenHelper;
import lombok.extern.slf4j.Slf4j;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.BuildPluginManager;
import org.apache.maven.project.MavenProject;

import java.io.File;
import java.util.HashMap;

/**
 * The type Deploy process.
 *
 * @author gudaoxuri
 */
@Slf4j
public class DeployProcess {

    public static void process(MavenProject mavenProject, MavenSession mavenSession, BuildPluginManager pluginManager, File output) {
        log.info("Deploy SDK from : {}", output.getPath());
        MavenHelper.invoke("org.apache.maven.plugins", "maven-invoker-plugin", null,
                "run", new HashMap<>() {
                    {
                        put("projectsDirectory", output.getParent());
                        put("goals", new HashMap<>() {
                            {
                                put("goal", "-P release");
                            }
                        });
                        put("mavenOpts", "");
                    }
                }, mavenProject, mavenSession, pluginManager);
    }

}
