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

package com.tairanchina.csp.dew.kernel.config;

import com.ecfront.dew.common.$;
import com.tairanchina.csp.dew.helper.GitHelper;
import com.tairanchina.csp.dew.kernel.Dew;
import com.tairanchina.csp.dew.mojo.BasicMojo;
import org.apache.maven.project.MavenProject;

import java.io.File;
import java.lang.reflect.InvocationTargetException;

public class ConfigBuilder {

    public static FinalProjectConfig buildProject(AppKind appKind, String profile, DewConfig dewConfig, MavenProject mavenProject,
                                                  String dockerHost, String dockerRegistryUrl, String dockerRegistryUserName, String dockerRegistryPassword, String kubeBase64Config) throws InvocationTargetException, IllegalAccessException {
        FinalProjectConfig finalProjectConfig = new FinalProjectConfig();
        if (profile.equalsIgnoreCase(BasicMojo.FLAG_DEW_DEVOPS_DEFAULT_PROFILE)) {
            $.bean.copyProperties(finalProjectConfig, dewConfig);
        } else {
            $.bean.copyProperties(finalProjectConfig, dewConfig.getProfiles().get(profile));
        }
        finalProjectConfig.setId(mavenProject.getId());
        finalProjectConfig.setAppKind(appKind);
        finalProjectConfig.setProfile(profile);
        if (dockerHost != null && !dockerHost.trim().isEmpty()) {
            finalProjectConfig.getDocker().setHost(dockerHost.trim());
        }
        if (dockerRegistryUrl != null && !dockerRegistryUrl.trim().isEmpty()) {
            finalProjectConfig.getDocker().setRegistryUrl(dockerRegistryUrl.trim());
        }
        if (dockerRegistryUserName != null && !dockerRegistryUserName.trim().isEmpty()) {
            finalProjectConfig.getDocker().setRegistryUserName(dockerRegistryUserName.trim());
        }
        if (dockerRegistryPassword != null && !dockerRegistryPassword.trim().isEmpty()) {
            finalProjectConfig.getDocker().setRegistryPassword(dockerRegistryPassword.trim());
        }
        if (kubeBase64Config != null && !kubeBase64Config.trim().isEmpty()) {
            finalProjectConfig.getKube().setBase64Config(kubeBase64Config.trim());
        }
        Plugin.fillMaven(finalProjectConfig, mavenProject);
        Plugin.fillApp(finalProjectConfig, mavenProject);
        Plugin.fillGit(finalProjectConfig, mavenProject);
        return finalProjectConfig;
    }

    public static class Plugin {

        static void fillMaven(FinalProjectConfig finalProjectConfig, MavenProject mavenProject) {
            finalProjectConfig.setMvnGroupId(mavenProject.getGroupId());
            finalProjectConfig.setMvnArtifactId(mavenProject.getArtifactId());
            finalProjectConfig.setMvnDirectory(mavenProject.getBasedir().getPath() + File.separator);
            finalProjectConfig.setMvnTargetDirectory(finalProjectConfig.getMvnDirectory() + "target" + File.separator);
        }

        static void fillApp(FinalProjectConfig finalProjectConfig, MavenProject mavenProject) {
            finalProjectConfig.setAppGroup(mavenProject.getGroupId());
            finalProjectConfig.setAppName(mavenProject.getArtifactId());
            finalProjectConfig.setAppVersion(mavenProject.getVersion());
        }

        static void fillGit(FinalProjectConfig finalProjectConfig, MavenProject mavenProject) {
            // FIXME submodule处理
            finalProjectConfig.setScmUrl(GitHelper.getScmUrl(Dew.basicDirectory));
            finalProjectConfig.setGitBranch(GitHelper.getCurrentBranch(Dew.basicDirectory));
            finalProjectConfig.setGitCommit(GitHelper.getCurrentCommit(Dew.basicDirectory));
        }

    }
}
