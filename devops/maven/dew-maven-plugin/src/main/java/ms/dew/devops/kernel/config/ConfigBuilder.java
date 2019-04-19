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
import ms.dew.devops.exception.ConfigException;
import ms.dew.devops.helper.GitHelper;
import ms.dew.devops.helper.YamlHelper;
import ms.dew.devops.kernel.Dew;
import org.apache.maven.project.MavenProject;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Config builder.
 *
 * @author gudaoxuri
 */
public class ConfigBuilder {

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
     * Build project.
     *
     * @param dewConfig                   the dew config
     * @param mavenProject                the maven project
     * @param inputProfile                the input profile
     * @param inputDockerHost             the input docker host
     * @param inputDockerRegistryUrl      the input docker registry url
     * @param inputDockerRegistryUserName the input docker registry user name
     * @param inputDockerRegistryPassword the input docker registry password
     * @param inputKubeBase64Config       the input kube base 64 config
     * @param customVersion               the custom version
     * @return the final project config
     * @throws InvocationTargetException the invocation target exception
     * @throws IllegalAccessException    the illegal access exception
     */
    public static Optional<FinalProjectConfig> buildProject(DewConfig dewConfig, MavenProject mavenProject,
                                                            String inputProfile,
                                                            String inputDockerHost, String inputDockerRegistryUrl,
                                                            String inputDockerRegistryUserName, String inputDockerRegistryPassword,
                                                            String inputKubeBase64Config, String customVersion)
            throws InvocationTargetException, IllegalAccessException {
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
        if (!inputProfile.equals(Dew.Constants.FLAG_DEW_DEVOPS_DEFAULT_PROFILE) && !dewConfig.getProfiles().containsKey(inputProfile)) {
            throw new ConfigException("[" + mavenProject.getArtifactId() + "] Can't be found [" + inputProfile + "] profile");
        }
        // 是否配置为skip
        if (inputProfile.equals(Dew.Constants.FLAG_DEW_DEVOPS_DEFAULT_PROFILE) && dewConfig.isSkip()
                || !inputProfile.equals(Dew.Constants.FLAG_DEW_DEVOPS_DEFAULT_PROFILE) && dewConfig.getProfiles().get(inputProfile).isSkip()) {
            return Optional.empty();
        }
        // 项目类型检查
        if (inputProfile.equals(Dew.Constants.FLAG_DEW_DEVOPS_DEFAULT_PROFILE)) {
            if (dewConfig.getKind() == null) {
                dewConfig.setKind(Plugin.getAppKind(mavenProject));
            }
            if (dewConfig.getKind() == null) {
                // 不支持的类型
                return Optional.empty();
            }
        } else {
            if (dewConfig.getProfiles().get(inputProfile).getKind() == null) {
                dewConfig.getProfiles().get(inputProfile).setKind(Plugin.getAppKind(mavenProject));
            }
            if (dewConfig.getProfiles().get(inputProfile).getKind() == null) {
                // 不支持的类型
                return Optional.empty();
            }
        }
        FinalProjectConfig finalProjectConfig = doBuildProject(dewConfig, mavenProject,
                inputProfile, inputDockerHost, inputDockerRegistryUrl,
                inputDockerRegistryUserName, inputDockerRegistryPassword,
                inputKubeBase64Config, customVersion);
        if (finalProjectConfig.getKube().getBase64Config().isEmpty()) {
            throw new ConfigException("[" + mavenProject.getArtifactId() + "] Kubernetes config can't be empty");
        }
        return Optional.of(finalProjectConfig);
    }

    private static FinalProjectConfig doBuildProject(DewConfig dewConfig, MavenProject mavenProject,
                                                     String inputProfile,
                                                     String inputDockerHost, String inputDockerRegistryUrl,
                                                     String inputDockerRegistryUserName, String inputDockerRegistryPassword,
                                                     String inputKubeBase64Config, String customVersion)
            throws InvocationTargetException, IllegalAccessException {
        FinalProjectConfig finalProjectConfig = new FinalProjectConfig();
        if (inputProfile.equalsIgnoreCase(Dew.Constants.FLAG_DEW_DEVOPS_DEFAULT_PROFILE)) {
            $.bean.copyProperties(finalProjectConfig, dewConfig);
        } else {
            $.bean.copyProperties(finalProjectConfig, dewConfig.getProfiles().get(inputProfile));
        }
        finalProjectConfig.setId(mavenProject.getId());
        finalProjectConfig.setProfile(inputProfile);
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
        // 执行各插件
        Plugin.fillMaven(finalProjectConfig, mavenProject);
        Plugin.fillApp(finalProjectConfig, mavenProject);
        Plugin.fillGit(finalProjectConfig, customVersion);
        Plugin.fillReuseVersionInfo(finalProjectConfig, dewConfig);
        return finalProjectConfig;
    }

    /**
     * Plugin.
     *
     * @author gudaoxuri
     */
    public static class Plugin {

        /**
         * Gets app kind.
         *
         * @param mavenProject the maven project
         * @return the app kind
         */
        static AppKind getAppKind(MavenProject mavenProject) {
            AppKind appKind = null;
            if (mavenProject.getPackaging().equalsIgnoreCase("maven-plugin")) {
                // 排除 插件类型
            } else if (new File(mavenProject.getBasedir().getPath() + File.separator + "package.json").exists()) {
                appKind = AppKind.FRONTEND;
            } else if (mavenProject.getPackaging().equalsIgnoreCase("jar")
                    && new File(mavenProject.getBasedir().getPath() + File.separator
                    + "src" + File.separator
                    + "main" + File.separator
                    + "resources").exists()
                    && Arrays.stream(new File(mavenProject.getBasedir().getPath() + File.separator
                    + "src" + File.separator
                    + "main" + File.separator
                    + "resources").listFiles())
                    .anyMatch((res -> res.getName().toLowerCase().contains("application")
                            || res.getName().toLowerCase().contains("bootstrap")))
                    // 包含DependencyManagement内容，不精确
                    && mavenProject.getManagedVersionMap().containsKey("org.springframework.boot:spring-boot-starter-web:jar")
            ) {
                appKind = AppKind.JVM_SERVICE;
            } else if (mavenProject.getPackaging().equalsIgnoreCase("jar")) {
                appKind = AppKind.JVM_LIB;
            } else if (mavenProject.getPackaging().equalsIgnoreCase("pom")) {
                appKind = AppKind.POM;
            }

            Dew.log.debug("Current app [" + mavenProject.getArtifactId() + "] kind is " + appKind);
            return appKind;
        }

        /**
         * Fill maven.
         *
         * @param finalProjectConfig the final project config
         * @param mavenProject       the maven project
         */
        static void fillMaven(FinalProjectConfig finalProjectConfig, MavenProject mavenProject) {
            finalProjectConfig.setMvnGroupId(mavenProject.getGroupId());
            finalProjectConfig.setMvnArtifactId(mavenProject.getArtifactId());
            finalProjectConfig.setMvnDirectory(mavenProject.getBasedir().getPath() + File.separator);
            finalProjectConfig.setMvnTargetDirectory(finalProjectConfig.getMvnDirectory() + "target" + File.separator);
        }

        /**
         * Fill app.
         *
         * @param finalProjectConfig the final project config
         * @param mavenProject       the maven project
         */
        static void fillApp(FinalProjectConfig finalProjectConfig, MavenProject mavenProject) {
            finalProjectConfig.setAppGroup(mavenProject.getGroupId());
            finalProjectConfig.setAppName(mavenProject.getArtifactId());
            if (finalProjectConfig.getKind() == AppKind.FRONTEND) {
                finalProjectConfig.getApp().setPort(80);
                finalProjectConfig.getApp().setTraceLogEnabled(false);
            }

        }

        /**
         * Fill git.
         *
         * @param finalProjectConfig the final project config
         * @param customVersion      the custom version
         */
        static void fillGit(FinalProjectConfig finalProjectConfig, String customVersion) {
            if (customVersion != null && !customVersion.trim().isEmpty()) {
                Dew.log.warn("Currently in custom version mode, git check is ignored, custom version is " + customVersion);
                finalProjectConfig.setGitCommit(customVersion);
                finalProjectConfig.setScmUrl("");
            } else {
                finalProjectConfig.setScmUrl(GitHelper.inst().getScmUrl());
                finalProjectConfig.setGitCommit(GitHelper.inst().getCurrentCommit());
            }
        }

        /**
         * Fill reuse version info.
         *
         * @param finalProjectConfig the final project config
         * @param dewConfig          the dew config
         */
        static void fillReuseVersionInfo(FinalProjectConfig finalProjectConfig, DewConfig dewConfig) {
            if (finalProjectConfig.getDisableReuseVersion() != null
                    && finalProjectConfig.getDisableReuseVersion()) {
                // 配置显示要求禁用重用版本
                return;
            }
            // 配置没有指明，按默认逻辑执行
            // 先设置默认启用
            finalProjectConfig.setDisableReuseVersion(false);
            if (finalProjectConfig.getKind() == AppKind.FRONTEND) {
                // 前端工程由于在编译时混入了环境信息，所以不允许重用版本，每次部署都要重新编译
                finalProjectConfig.setDisableReuseVersion(true);
                return;
            }
            // 其它工程
            finalProjectConfig.setReuseLastVersionFromProfile(finalProjectConfig.getReuseLastVersionFromProfile().trim());
            if (finalProjectConfig.getReuseLastVersionFromProfile().isEmpty()
                    && (finalProjectConfig.getProfile().equals("production")
                    || finalProjectConfig.getProfile().equals("prod"))) {
                // 如果当前是生产环境则自动填充
                // 猜测填充的来源环境
                String guessFromProfile = null;
                if (dewConfig.getProfiles().containsKey("pre-prod")) {
                    guessFromProfile = "pre-prod";
                } else if (dewConfig.getProfiles().containsKey("pre-production")) {
                    guessFromProfile = "pre-production";
                } else if (dewConfig.getProfiles().containsKey("uat")) {
                    guessFromProfile = "uat";
                }
                if (guessFromProfile != null) {
                    finalProjectConfig.setReuseLastVersionFromProfile(guessFromProfile);
                }
            }
            if (finalProjectConfig.getReuseLastVersionFromProfile().isEmpty()) {
                // 没有找到重用版本对应的目标环境
                finalProjectConfig.setDisableReuseVersion(true);
                return;
            }
            // 存在重用版本，不允许重用默认profile
            DewProfile appendProfile = dewConfig.getProfiles().get(finalProjectConfig.getReuseLastVersionFromProfile());
            Map<String, String> formattedProperties = Dew.Constants.getMavenProperties(null);
            Dew.Constants.formatParameters(Dew.Constants.FLAG_DEW_DEVOPS_DOCKER_HOST + "-append", formattedProperties).ifPresent(obj ->
                    appendProfile.getDocker().setHost(obj.trim())
            );
            // 附加Kubernetes和Docker的配置
            // 附加顺序：
            // 1) 带 '-append' 命令行参数
            // 2) '.dew' 配置中对应环境的配置
            // 3) 当前环境的配置，这意味着目标环境与当前环境共用配置！
            if (appendProfile.getDocker().getHost() == null || appendProfile.getDocker().getHost().isEmpty()) {
                appendProfile.getDocker().setHost(dewConfig.getDocker().getHost());
            }
            if (appendProfile.getDocker().getHost() == null || appendProfile.getDocker().getHost().isEmpty()) {
                appendProfile.getDocker().setHost(finalProjectConfig.getDocker().getHost());
            }

            Dew.Constants.formatParameters(Dew.Constants.FLAG_DEW_DEVOPS_DOCKER_REGISTRY_URL + "-append", formattedProperties).ifPresent(obj ->
                    appendProfile.getDocker().setRegistryUrl(obj.trim())
            );
            if (appendProfile.getDocker().getRegistryUrl() == null || appendProfile.getDocker().getRegistryUrl().isEmpty()) {
                appendProfile.getDocker().setRegistryUrl(dewConfig.getDocker().getRegistryUrl());
            }
            if (appendProfile.getDocker().getRegistryUrl() == null || appendProfile.getDocker().getRegistryUrl().isEmpty()) {
                appendProfile.getDocker().setRegistryUrl(finalProjectConfig.getDocker().getRegistryUrl());
            }

            Dew.Constants.formatParameters(Dew.Constants.FLAG_DEW_DEVOPS_DOCKER_REGISTRY_USERNAME + "-append",
                    formattedProperties).ifPresent(obj ->
                    appendProfile.getDocker().setRegistryUserName(obj.trim())
            );
            if (appendProfile.getDocker().getRegistryUserName() == null || appendProfile.getDocker().getRegistryUserName().isEmpty()) {
                appendProfile.getDocker().setRegistryUserName(dewConfig.getDocker().getRegistryUserName());
            }
            if (appendProfile.getDocker().getRegistryUserName() == null || appendProfile.getDocker().getRegistryUserName().isEmpty()) {
                appendProfile.getDocker().setRegistryUserName(finalProjectConfig.getDocker().getRegistryUserName());
            }

            Dew.Constants.formatParameters(Dew.Constants.FLAG_DEW_DEVOPS_DOCKER_REGISTRY_PASSWORD + "-append",
                    formattedProperties).ifPresent(obj ->
                    appendProfile.getDocker().setRegistryPassword(obj.trim())
            );
            if (appendProfile.getDocker().getRegistryPassword() == null || appendProfile.getDocker().getRegistryPassword().isEmpty()) {
                appendProfile.getDocker().setRegistryPassword(dewConfig.getDocker().getRegistryPassword());
            }
            if (appendProfile.getDocker().getRegistryPassword() == null || appendProfile.getDocker().getRegistryPassword().isEmpty()) {
                appendProfile.getDocker().setRegistryPassword(finalProjectConfig.getDocker().getRegistryPassword());
            }

            Dew.Constants.formatParameters(Dew.Constants.FLAG_DEW_DEVOPS_KUBE_CONFIG + "-append", formattedProperties).ifPresent(obj ->
                    appendProfile.getKube().setBase64Config(obj.trim())
            );
            if (appendProfile.getKube().getBase64Config() == null || appendProfile.getKube().getBase64Config().isEmpty()) {
                appendProfile.getKube().setBase64Config(dewConfig.getKube().getBase64Config());
            }
            if (appendProfile.getKube().getBase64Config() == null || appendProfile.getKube().getBase64Config().isEmpty()) {
                appendProfile.getKube().setBase64Config(finalProjectConfig.getKube().getBase64Config());
            }

            if (appendProfile.getKube().getBase64Config().isEmpty()
                    || appendProfile.getDocker().getHost().isEmpty()) {
                throw new ConfigException("In reuse version mode, "
                        + "'kubernetes base64 config' and 'docker host' must be specified by "
                        + "command-line arguments '"
                        + Dew.Constants.FLAG_DEW_DEVOPS_KUBE_CONFIG + "-append'/ "
                        + Dew.Constants.FLAG_DEW_DEVOPS_DOCKER_HOST + "-append'"
                        + "OR '.dew' profile configuration file");
            }
            finalProjectConfig.setAppendProfile(appendProfile);
            // 重用版本时Git信息后续会重用目标Git信息
            finalProjectConfig.setGitCommit("");
        }
    }
}
