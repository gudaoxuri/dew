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

package group.idealworld.dew.devops.maven.function;

import group.idealworld.dew.devops.kernel.DevOps;
import group.idealworld.dew.devops.kernel.util.ExecuteOnceProcessor;
import group.idealworld.dew.devops.maven.MavenDevOps;
import group.idealworld.dew.devops.kernel.config.FinalProjectConfig;
import group.idealworld.dew.devops.kernel.flow.release.DockerBuildFlow;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.project.MavenProject;

/**
 * Maven跳过处理器.
 *
 * @author gudaoxuri
 */
public class MavenSkipProcessor {

    /**
     * Process.
     *
     * @param mavenSession the mavenSession
     */
    public static synchronized void process(MavenSession mavenSession) {
        if (ExecuteOnceProcessor.executedCheck(MavenSkipProcessor.class)) {
            return;
        }
        // 跳过未被装配的模块
        mavenSession.getProjects()
                .stream()
                .filter(project -> !DevOps.Config.getFinalConfig().getProjects().containsKey(project.getId()))
                .forEach(MavenSkipProcessor::disabledDefaultBehavior);
        // 已装配模块是否处理判断
        for (FinalProjectConfig config : DevOps.Config.getFinalConfig().getProjects().values()) {
            if (config.getDisableReuseVersion() != null && !config.getDisableReuseVersion()
                    && DockerBuildFlow.existsReuseVersion(config)) {
                // 重用版本模式下强制跳过单元测试，不需要部署
                disabledDefaultBehavior(config.getId());
                continue;
            }
            if (config.getSkip()) {
                disabledDefaultBehavior(config.getId());
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

    /**
     * Disabled default behavior.
     *
     * @param projectId the project id
     */
    public static void disabledDefaultBehavior(String projectId) {
        MavenDevOps.Config.setMavenProperty(projectId, "dew.devops.skip", "true");
        MavenDevOps.Config.setMavenProperty(projectId, "maven.javadoc.skip", "true");
        MavenDevOps.Config.setMavenProperty(projectId, "maven.source.skip", "true");
        MavenDevOps.Config.setMavenProperty(projectId, "checkstyle.skip", "true");
        MavenDevOps.Config.setMavenProperty(projectId, "assembly.skipAssembly", "true");
        MavenDevOps.Config.setMavenProperty(projectId, "mdep.analyze.skip", "true");
        MavenDevOps.Config.setMavenProperty(projectId, "maven.main.skip", "true");
        MavenDevOps.Config.setMavenProperty(projectId, "gpg.skip", "true");
        MavenDevOps.Config.setMavenProperty(projectId, "maven.war.skip", "true");
        MavenDevOps.Config.setMavenProperty(projectId, "maven.resources.skip", "true");
        MavenDevOps.Config.setMavenProperty(projectId, "maven.test.skip", "true");
        MavenDevOps.Config.setMavenProperty(projectId, "maven.install.skip", "true");
        MavenDevOps.Config.setMavenProperty(projectId, "maven.deploy.skip", "true");
        MavenDevOps.Config.setMavenProperty(projectId, "maven.site.skip", "true");
    }

    /**
     * Disabled default behavior.
     *
     * @param project the project
     */
    public static void disabledDefaultBehavior(MavenProject project) {
        project.getProperties().put("dew.devops.skip", "true");
        project.getProperties().put("maven.javadoc.skip", "true");
        project.getProperties().put("maven.source.skip", "true");
        project.getProperties().put("checkstyle.skip", "true");
        project.getProperties().put("assembly.skipAssembly", "true");
        project.getProperties().put("mdep.analyze.skip", "true");
        project.getProperties().put("maven.main.skip", "true");
        project.getProperties().put("gpg.skip", "true");
        project.getProperties().put("maven.war.skip", "true");
        project.getProperties().put("maven.resources.skip", "true");
        project.getProperties().put("maven.test.skip", "true");
        project.getProperties().put("maven.install.skip", "true");
        project.getProperties().put("maven.deploy.skip", "true");
        project.getProperties().put("maven.site.skip", "true");
    }

}
