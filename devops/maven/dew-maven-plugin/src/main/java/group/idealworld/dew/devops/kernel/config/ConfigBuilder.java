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

package group.idealworld.dew.devops.kernel.config;

import com.ecfront.dew.common.$;
import group.idealworld.dew.devops.kernel.DevOps;
import group.idealworld.dew.devops.kernel.exception.ConfigException;
import group.idealworld.dew.devops.kernel.helper.GitHelper;
import group.idealworld.dew.devops.kernel.helper.YamlHelper;
import group.idealworld.dew.devops.kernel.plugin.appkind.AppKindPlugin;
import group.idealworld.dew.devops.kernel.plugin.deploy.DeployPlugin;
import org.apache.commons.lang3.StringUtils;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.project.MavenProject;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Config builder.
 *
 * @author gudaoxuri
 */
public class ConfigBuilder {

    /**
     * 默认环境值.
     */
    public static final String FLAG_DEW_DEVOPS_DEFAULT_PROFILE = "default";

    /**
     * 将default环境的配置合并到其它环境中.
     *
     * @param config the project config
     * @return merged content
     */
    public static String mergeProfiles(String config) {
        LinkedHashMap mergedConfig = new LinkedHashMap();
        ((Map) YamlHelper.toObject(config)).forEach((k, v) -> {
            if (!((String) k).equalsIgnoreCase("profiles")) {
                mergedConfig.put(k, v);
            }
        });
        if (((Map) YamlHelper.toObject(config)).containsKey("profiles")) {
            Map profiles = new LinkedHashMap();
            ((Map) ((Map) YamlHelper.toObject(config)).get("profiles")).forEach((k, v) -> {
                profiles.put(k, mergeItems(mergedConfig, (LinkedHashMap) v));
            });
            mergedConfig.put("profiles", profiles);
        }
        return YamlHelper.toString(mergedConfig);
    }

    /**
     * 合并基础配置与项目配置.
     *
     * @param source the source, 基础配置
     * @param target the target， 项目配置
     * @return merged content
     */
    public static String mergeProject(String source, String target) {
        return YamlHelper.toString(mergeItems(YamlHelper.toObject(source), YamlHelper.toObject(target)));
    }

    private static LinkedHashMap mergeItems(LinkedHashMap source, LinkedHashMap target) {
        target.forEach((k, v) -> {
            if (source.containsKey(k) && v instanceof LinkedHashMap) {
                // 如果源map和目标map都存在，并且存在子项目，递归合并
                // 并且存在子项目，递归合并
                target.put(k, mergeItems((LinkedHashMap) source.get(k), (LinkedHashMap) v));
            }
            // 否则不合并，即使用target的原始值
        });
        source.forEach((k, v) -> {
            if (!target.containsKey(k)) {
                // 添加 源map存在，目标map不存在的项目
                target.put(k, v);
            }
        });
        return target;
    }


    /**
     * Build project optional.
     *
     * @param dewConfig                       the dew config
     * @param appKindPlugin                   the app kind plugin
     * @param deployPlugin                    the deploy plugin
     * @param mavenSession                    the maven session
     * @param mavenProject                    the maven project
     * @param inputProfile                    the input profile
     * @param inputDockerHost                 the input docker host
     * @param inputDockerRegistryUrl          the input docker registry url
     * @param inputDockerRegistryUserName     the input docker registry user name
     * @param inputDockerRegistryPassword     the input docker registry password
     * @param inputKubeBase64Config           the input kube base 64 config
     * @param dockerHostAppendOpt             the docker host append opt
     * @param dockerRegistryUrlAppendOpt      the docker registry url append opt
     * @param dockerRegistryUserNameAppendOpt the docker registry user name append opt
     * @param dockerRegistryPasswordAppendOpt the docker registry password append opt
     * @return the result
     */
    public static FinalProjectConfig buildProject(DewConfig dewConfig, AppKindPlugin appKindPlugin, DeployPlugin deployPlugin,
                                                  MavenSession mavenSession, MavenProject mavenProject,
                                                  String inputProfile,
                                                  String inputDockerHost, String inputDockerRegistryUrl,
                                                  String inputDockerRegistryUserName, String inputDockerRegistryPassword,
                                                  String inputKubeBase64Config, Optional<String> dockerHostAppendOpt,
                                                  Optional<String> dockerRegistryUrlAppendOpt,
                                                  Optional<String> dockerRegistryUserNameAppendOpt,
                                                  Optional<String> dockerRegistryPasswordAppendOpt) {
        // 格式化
        inputProfile = inputProfile.toLowerCase();
        dewConfig.setProfiles(
                dewConfig.getProfiles().entrySet().stream()
                        .collect(Collectors.toMap(profile -> profile.getKey().toLowerCase(), Map.Entry::getValue)));

        // 命名空间与Kubernetes集群冲突检查，不同环境如果命名空间同名则要求位于不同的Kubernetes集群中
        Set<String> envChecker = dewConfig.getProfiles().values().stream()
                .map(prof -> prof.getNamespace() + prof.getKube().getBase64Config())
                .collect(Collectors.toSet());
        if (!dewConfig.getNamespace().isEmpty() && !dewConfig.getKube().getBase64Config().isEmpty()) {
            envChecker.add(dewConfig.getNamespace() + dewConfig.getKube().getBase64Config());
        } else {
            envChecker.add("");
        }
        if (envChecker.size() != dewConfig.getProfiles().size() + 1) {
            throw new ConfigException("[" + mavenProject.getArtifactId() + "] "
                    + "Namespace and kubernetes cluster between different environments cannot be the same");
        }
        // 指定的环境是否存在
        if (!inputProfile.equals(FLAG_DEW_DEVOPS_DEFAULT_PROFILE) && !dewConfig.getProfiles().containsKey(inputProfile)) {
            throw new ConfigException("[" + mavenProject.getArtifactId() + "] Can't be found [" + inputProfile + "] profile");
        }
        FinalProjectConfig finalProjectConfig = doBuildProject(dewConfig, appKindPlugin, deployPlugin, mavenSession, mavenProject,
                inputProfile, inputDockerHost, inputDockerRegistryUrl,
                inputDockerRegistryUserName, inputDockerRegistryPassword, inputKubeBase64Config,
                dockerHostAppendOpt, dockerRegistryUrlAppendOpt, dockerRegistryUserNameAppendOpt, dockerRegistryPasswordAppendOpt);
        if (!finalProjectConfig.getSkip() && finalProjectConfig.getKube().getBase64Config().isEmpty()) {
            throw new ConfigException("[" + mavenProject.getArtifactId() + "] Kubernetes config can't be empty");
        }
        return finalProjectConfig;
    }

    private static FinalProjectConfig doBuildProject(DewConfig dewConfig, AppKindPlugin appKindPlugin, DeployPlugin deployPlugin,
                                                     MavenSession mavenSession, MavenProject mavenProject,
                                                     String inputProfile,
                                                     String inputDockerHost, String inputDockerRegistryUrl,
                                                     String inputDockerRegistryUserName, String inputDockerRegistryPassword,
                                                     String inputKubeBase64Config,
                                                     Optional<String> dockerHostAppendOpt, Optional<String> dockerRegistryUrlAppendOpt,
                                                     Optional<String> dockerRegistryUserNameAppendOpt,
                                                     Optional<String> dockerRegistryPasswordAppendOpt) {
        FinalProjectConfig finalProjectConfig = new FinalProjectConfig();
        if (inputProfile.equalsIgnoreCase(FLAG_DEW_DEVOPS_DEFAULT_PROFILE)) {
            $.bean.copyProperties(finalProjectConfig, dewConfig);
        } else {
            $.bean.copyProperties(finalProjectConfig, dewConfig.getProfiles().get(inputProfile));
        }
        // setting basic
        finalProjectConfig.setId(mavenProject.getId());
        finalProjectConfig.setAppKindPlugin(appKindPlugin);
        finalProjectConfig.setDeployPlugin(deployPlugin);
        finalProjectConfig.setProfile(inputProfile);
        finalProjectConfig.setAppGroup(mavenProject.getGroupId());
        finalProjectConfig.setAppName(mavenProject.getArtifactId());
        if (mavenProject.getName() != null && !mavenProject.getName().trim().isEmpty()) {
            finalProjectConfig.setAppShowName(mavenProject.getName());
        } else {
            finalProjectConfig.setAppShowName(mavenProject.getArtifactId());
        }

        finalProjectConfig.setMavenSession(mavenSession);
        finalProjectConfig.setMavenProject(mavenProject);

        // setting path
        finalProjectConfig.setDirectory(mavenProject.getBasedir().getPath() + File.separator);
        finalProjectConfig.setTargetDirectory(finalProjectConfig.getDirectory() + "target" + File.separator);

        if (finalProjectConfig.getSkip()) {
            DevOps.SkipProcess.skip(finalProjectConfig, "Configured to skip", FinalProjectConfig.SkipCodeEnum.SELF_CONFIG, false);
            return finalProjectConfig;
        }
        // 优先使用命令行参数
        if (inputDockerHost != null && !inputDockerHost.trim().isEmpty()) {
            finalProjectConfig.getDocker().setHost(inputDockerHost.trim());
        }
        if (inputDockerRegistryUrl != null && !inputDockerRegistryUrl.trim().isEmpty()) {
            finalProjectConfig.getDocker().setRegistryUrl(inputDockerRegistryUrl.trim());
        }
        if (inputDockerRegistryUserName != null && !inputDockerRegistryUserName.trim().isEmpty()) {
            finalProjectConfig.getDocker().setRegistryUserName(inputDockerRegistryUserName.trim());
        }
        if (inputDockerRegistryPassword != null && !inputDockerRegistryPassword.trim().isEmpty()) {
            finalProjectConfig.getDocker().setRegistryPassword(inputDockerRegistryPassword.trim());
        }
        if (inputKubeBase64Config != null && !inputKubeBase64Config.trim().isEmpty()) {
            finalProjectConfig.getKube().setBase64Config(inputKubeBase64Config.trim());
        }

        // setting git info
        finalProjectConfig.setScmUrl(GitHelper.inst().getScmUrl());
        finalProjectConfig.setGitCommit(GitHelper.inst().getCurrentCommit());
        finalProjectConfig.setImageVersion(finalProjectConfig.getGitCommit());
        finalProjectConfig.setAppVersion(finalProjectConfig.getGitCommit());

        // setting custom config by app kind
        finalProjectConfig.getAppKindPlugin().customConfig(finalProjectConfig);
        if (StringUtils.isNotEmpty(inputDockerRegistryUrl)) {
            // setting reuse version
            fillReuseVersionInfo(finalProjectConfig, dewConfig,
                    dockerHostAppendOpt, dockerRegistryUrlAppendOpt, dockerRegistryUserNameAppendOpt, dockerRegistryPasswordAppendOpt);
        }
        return finalProjectConfig;
    }

    private static void fillReuseVersionInfo(FinalProjectConfig finalProjectConfig, DewConfig dewConfig,
                                             Optional<String> dockerHostAppendOpt,
                                             Optional<String> dockerRegistryUrlAppendOpt,
                                             Optional<String> dockerRegistryUserNameAppendOpt,
                                             Optional<String> dockerRegistryPasswordAppendOpt) {
        if (finalProjectConfig.getDisableReuseVersion() != null
                && finalProjectConfig.getDisableReuseVersion()) {
            // 配置显示要求禁用重用版本
            return;
        }
        // 配置没有指明，按默认逻辑执行
        // 先设置默认启用
        finalProjectConfig.setDisableReuseVersion(false);
        if ((finalProjectConfig.getReuseLastVersionFromProfile() == null || finalProjectConfig.getReuseLastVersionFromProfile().isEmpty())) {
            // 如果当前是生产环境则自动填充
            // 猜测填充的来源环境
            String guessFromProfile = null;
            if (finalProjectConfig.getProfile().equals("production") || finalProjectConfig.getProfile().equals("prod")) {
                if (dewConfig.getProfiles().containsKey("pre-prod")) {
                    guessFromProfile = "pre-prod";
                } else if (dewConfig.getProfiles().containsKey("pre-production")) {
                    guessFromProfile = "pre-production";
                } else if (dewConfig.getProfiles().containsKey("uat")) {
                    guessFromProfile = "uat";
                }
            } else if (finalProjectConfig.getProfile().equals("pre-production") || finalProjectConfig.getProfile().equals("pre-prod")
                    || finalProjectConfig.getProfile().equals("uat")) {
                if (dewConfig.getProfiles().containsKey("test")) {
                    guessFromProfile = "test";
                }
            } else if (finalProjectConfig.getProfile().equals("test")) {
                if (dewConfig.getProfiles().containsKey("dev")) {
                    guessFromProfile = "dev";
                }
            }
            if (guessFromProfile != null) {
                finalProjectConfig.setReuseLastVersionFromProfile(guessFromProfile);
            }
        }
        if (finalProjectConfig.getReuseLastVersionFromProfile() == null || finalProjectConfig.getReuseLastVersionFromProfile().isEmpty()) {
            // 没有找到重用版本对应的目标环境
            finalProjectConfig.setDisableReuseVersion(true);
            return;
        }
        // 存在重用版本，不允许重用默认profile
        // 附加Docker的配置
        // 附加顺序：
        // 1) 带 '-append' 命令行参数
        // 2) '.dew' 配置中对应环境的配置
        // 3) 当前环境的配置，这意味着目标环境与当前环境共用配置！
        DewProfile appendProfile = dewConfig.getProfiles().get(finalProjectConfig.getReuseLastVersionFromProfile());
        dockerHostAppendOpt.ifPresent(obj ->
                appendProfile.getDocker().setHost(obj.trim())
        );
        if (appendProfile.getDocker().getHost() == null || appendProfile.getDocker().getHost().isEmpty()) {
            appendProfile.getDocker().setHost(dewConfig.getDocker().getHost());
        }
        if (appendProfile.getDocker().getHost() == null || appendProfile.getDocker().getHost().isEmpty()) {
            appendProfile.getDocker().setHost(finalProjectConfig.getDocker().getHost());
        }

        dockerRegistryUrlAppendOpt.ifPresent(obj ->
                appendProfile.getDocker().setRegistryUrl(obj.trim())
        );
        if (appendProfile.getDocker().getRegistryUrl() == null || appendProfile.getDocker().getRegistryUrl().isEmpty()) {
            appendProfile.getDocker().setRegistryUrl(dewConfig.getDocker().getRegistryUrl());
        }
        if (appendProfile.getDocker().getRegistryUrl() == null || appendProfile.getDocker().getRegistryUrl().isEmpty()) {
            appendProfile.getDocker().setRegistryUrl(finalProjectConfig.getDocker().getRegistryUrl());
        }

        dockerRegistryUserNameAppendOpt.ifPresent(obj ->
                appendProfile.getDocker().setRegistryUserName(obj.trim())
        );
        if (appendProfile.getDocker().getRegistryUserName() == null || appendProfile.getDocker().getRegistryUserName().isEmpty()) {
            appendProfile.getDocker().setRegistryUserName(dewConfig.getDocker().getRegistryUserName());
        }
        if (appendProfile.getDocker().getRegistryUserName() == null || appendProfile.getDocker().getRegistryUserName().isEmpty()) {
            appendProfile.getDocker().setRegistryUserName(finalProjectConfig.getDocker().getRegistryUserName());
        }

        dockerRegistryPasswordAppendOpt.ifPresent(obj ->
                appendProfile.getDocker().setRegistryPassword(obj.trim())
        );
        if (appendProfile.getDocker().getRegistryPassword() == null || appendProfile.getDocker().getRegistryPassword().isEmpty()) {
            appendProfile.getDocker().setRegistryPassword(dewConfig.getDocker().getRegistryPassword());
        }
        if (appendProfile.getDocker().getRegistryPassword() == null || appendProfile.getDocker().getRegistryPassword().isEmpty()) {
            appendProfile.getDocker().setRegistryPassword(finalProjectConfig.getDocker().getRegistryPassword());
        }

        if (appendProfile.getDocker().getHost().isEmpty()) {
            throw new ConfigException("In reuse version mode, "
                    + "'Docker host' must be specified by "
                    + "command-line arguments '"
                    + "dew_devops_docker_host-append'"
                    + "OR '.dew' profile configuration file");
        }
        finalProjectConfig.setAppendProfile(appendProfile);
    }

}
