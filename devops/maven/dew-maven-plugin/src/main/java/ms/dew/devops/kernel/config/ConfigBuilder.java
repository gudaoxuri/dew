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

package ms.dew.devops.kernel.config;

import com.ecfront.dew.common.$;
import ms.dew.devops.helper.GitHelper;
import ms.dew.devops.helper.YamlHelper;
import ms.dew.devops.mojo.BasicMojo;
import org.apache.maven.project.MavenProject;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.LinkedHashMap;
import java.util.Map;

public class ConfigBuilder {

    public static FinalProjectConfig buildProject(String profile, DewConfig dewConfig, MavenProject mavenProject,
                                                  String dockerHost, String dockerRegistryUrl,
                                                  String dockerRegistryUserName, String dockerRegistryPassword, String kubeBase64Config)
            throws InvocationTargetException, IllegalAccessException {
        FinalProjectConfig finalProjectConfig = new FinalProjectConfig();
        if (profile.equalsIgnoreCase(BasicMojo.FLAG_DEW_DEVOPS_DEFAULT_PROFILE)) {
            $.bean.copyProperties(finalProjectConfig, dewConfig);
        } else {
            $.bean.copyProperties(finalProjectConfig, dewConfig.getProfiles().get(profile));
        }
        finalProjectConfig.setId(mavenProject.getId());
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

    public static String mergeProfiles(String config) {
        LinkedHashMap mergedConfig = new LinkedHashMap();
        ((Map) YamlHelper.toObject(config)).forEach((k, v) -> {
            if (!((String) k).equalsIgnoreCase("profiles")) {
                mergedConfig.put(k, v);
            } else {
                Map profiles = new LinkedHashMap();
                ((LinkedHashMap) v).forEach((pk, pv) ->
                        profiles.put(pk, mergeItems(mergedConfig, (LinkedHashMap) pv)));
                mergedConfig.put("profiles", profiles);
            }
        });
        return YamlHelper.toString(mergedConfig);
    }

    public static String mergeProject(String source, String target) {
        return YamlHelper.toString(mergeItems(YamlHelper.toObject(source), YamlHelper.toObject(target)));
    }

    private static LinkedHashMap mergeItems(LinkedHashMap source, LinkedHashMap target) {
        target.forEach((k, v) -> {
            if (source.containsKey(k)) {
                // 如果源map和目标map都存在
                if (v instanceof LinkedHashMap) {
                    // 并且存在子项目，递归合并
                    target.put(k, mergeItems((LinkedHashMap) source.get(k), (LinkedHashMap) v));
                }
                // 否则不合并，即使用target的原始值
            }
        });
        source.forEach((k, v) -> {
            if (!target.containsKey(k)) {
                // 添加 源map存在，目标map不存在的项目
                target.put(k, v);
            }
        });
        return target;
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
            if (finalProjectConfig.getKind() == AppKind.FRONTEND) {
                finalProjectConfig.getApp().setPort(80);
            }
        }

        static void fillGit(FinalProjectConfig finalProjectConfig, MavenProject mavenProject) {
            finalProjectConfig.setScmUrl(GitHelper.inst().getScmUrl());
            finalProjectConfig.setGitBranch(GitHelper.inst().getCurrentBranch());
            finalProjectConfig.setGitCommit(GitHelper.inst().getCurrentCommit());
        }

    }
}
