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

package group.idealworld.dew.devops.agent;

import java.io.File;
import java.lang.instrument.Instrumentation;
import java.util.HashSet;

/**
 * Agent Launcher.
 *
 * @author gudaoxuri
 */
public class MavenAgent {

    /**
     * Premain.
     *
     * @param agentArgs the agent args
     * @param inst      the inst
     */
    public static void premain(String agentArgs, Instrumentation inst) {
        String mavenHome = System.getProperty("maven.home");
        if (mavenHome == null || mavenHome.isEmpty()) {
            throw new RuntimeException("Can't find [maven.home] configuration in system property");
        }
        File mavenHomePath = new File(mavenHome);
        File mavenLibPath = new File(mavenHomePath.getPath() + File.separator + "lib");
        File mavenCoreFile = mavenLibPath.listFiles(pathname -> pathname.getName().contains("maven-core-"))[0];
        switch (mavenCoreFile.getName()) {
            case "maven-core-3.6.0.jar":
                inst.addTransformer(new Transformer("3.6.0", new HashSet<>() {
                    {
                        add("org/apache/maven/lifecycle/internal/builder/singlethreaded/SingleThreadedBuilder");
                        add("org/apache/maven/lifecycle/internal/MojoExecutor");
                    }
                }));
                break;
            case "maven-core-3.6.1.jar":
                inst.addTransformer(new Transformer("3.6.1", new HashSet<>() {
                    {
                        add("org/apache/maven/lifecycle/internal/builder/singlethreaded/SingleThreadedBuilder");
                        add("org/apache/maven/lifecycle/internal/MojoExecutor");
                    }
                }));
                break;
            case "maven-core-3.5.2.jar":
                inst.addTransformer(new Transformer("3.5.2", new HashSet<>() {
                    {
                        add("org/apache/maven/lifecycle/internal/builder/singlethreaded/SingleThreadedBuilder");
                        add("org/apache/maven/lifecycle/internal/MojoExecutor");
                    }
                }));
                break;
            case "maven-core-3.3.9.jar":
                inst.addTransformer(new Transformer("3.3.9", new HashSet<>() {
                    {
                        add("org/apache/maven/lifecycle/internal/builder/singlethreaded/SingleThreadedBuilder");
                        add("org/apache/maven/lifecycle/internal/MojoExecutor");
                    }
                }));
                break;
            case "maven-core-3.3.3.jar":
                inst.addTransformer(new Transformer("3.3.3", new HashSet<>() {
                    {
                        add("org/apache/maven/lifecycle/internal/builder/singlethreaded/SingleThreadedBuilder");
                        add("org/apache/maven/lifecycle/internal/MojoExecutor");
                    }
                }));
                break;
            case "maven-core-3.2.5.jar":
                inst.addTransformer(new Transformer("3.2.5", new HashSet<>() {
                    {
                        add("org/apache/maven/lifecycle/internal/builder/singlethreaded/SingleThreadedBuilder");
                        add("org/apache/maven/lifecycle/internal/MojoExecutor");
                    }
                }));
                break;
            case "maven-core-3.0.5.jar":
                inst.addTransformer(new Transformer("3.0.5", new HashSet<>() {
                    {
                        add("org/apache/maven/lifecycle/internal/LifecycleStarter");
                        add("org/apache/maven/lifecycle/internal/MojoExecutor");
                    }
                }));
                break;
            default:
                throw new RuntimeException("Unsupported versions : " + mavenCoreFile.getName());
        }

    }

}
