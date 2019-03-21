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

import java.lang.reflect.InvocationTargetException;

public class ConfigBuilder {

    public static FinalConfig build(AppKind appKind, String profile, DewConfig dewConfig,
                                    Boolean skip, String dockerHost, String dockerRegistryUrl, String dockerRegistryUserName, String dockerRegistryPassword, String kubeBase64Config) throws InvocationTargetException, IllegalAccessException {
        FinalConfig finalConfig = new FinalConfig();
        finalConfig.setAppKind(appKind);
        if (profile.toLowerCase().trim().equals("default")) {
            $.bean.copyProperties(finalConfig, dewConfig);
        } else {
            $.bean.copyProperties(finalConfig, dewConfig.getProfiles().get(profile));
        }
        finalConfig.setProfile(profile);
        if (skip != null) {
            finalConfig.setSkip(skip);
        }
        if (finalConfig.isSkip()) {
            return finalConfig;
        }
        if (dockerHost != null && !dockerHost.trim().isEmpty()) {
            finalConfig.getDocker().setHost(dockerHost.trim());
        }
        if (dockerRegistryUrl != null && !dockerRegistryUrl.trim().isEmpty()) {
            finalConfig.getDocker().setRegistryUrl(dockerRegistryUrl.trim());
        }
        if (dockerRegistryUserName != null && !dockerRegistryUserName.trim().isEmpty()) {
            finalConfig.getDocker().setRegistryUserName(dockerRegistryUserName.trim());
        }
        if (dockerRegistryPassword != null && !dockerRegistryPassword.trim().isEmpty()) {
            finalConfig.getDocker().setRegistryPassword(dockerRegistryPassword.trim());
        }
        if (kubeBase64Config != null && !kubeBase64Config.trim().isEmpty()) {
            finalConfig.getKube().setBase64Config(kubeBase64Config.trim());
        }
        Plugin.fillApp(finalConfig);
        Plugin.fillGit(finalConfig);
        return finalConfig;
    }

    public static class Plugin {

        static void fillApp(FinalConfig finalConfig) {
            finalConfig.setAppGroup(Dew.mavenProject.getGroupId());
            finalConfig.setAppName(Dew.mavenProject.getArtifactId());
            finalConfig.setAppVersion(Dew.mavenProject.getVersion());
        }

        static void fillGit(FinalConfig finalConfig) {
            // TODO submodule处理
            finalConfig.setScmUrl(GitHelper.getScmUrl(Dew.rootDirectory));
            finalConfig.setGitBranch(GitHelper.getCurrentBranch(Dew.rootDirectory));
            finalConfig.setGitCommit(GitHelper.getCurrentCommit(Dew.rootDirectory));
        }

    }
}
