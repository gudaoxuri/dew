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

package com.tairanchina.csp.dew.kernel;

import com.ecfront.dew.common.$;
import com.tairanchina.csp.dew.helper.DockerHelper;
import com.tairanchina.csp.dew.helper.GitHelper;
import com.tairanchina.csp.dew.helper.KubeHelper;
import com.tairanchina.csp.dew.helper.YamlHelper;
import com.tairanchina.csp.dew.kernel.config.*;
import com.tairanchina.csp.dew.mojo.BasicMojo;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.BuildPluginManager;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.project.MavenProject;
import org.twdata.maven.mojoexecutor.MojoExecutor;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

import static org.twdata.maven.mojoexecutor.MojoExecutor.*;

public class Dew {

    public static Log log;
    public static String basicDirectory;

    private static MavenSession mavenSession;
    private static BuildPluginManager mavenPluginManager;

    public static class Init {

        private static final AtomicBoolean initialized = new AtomicBoolean(false);

        public static void init(MavenSession session, BuildPluginManager pluginManager,
                                String profile,
                                String dockerHost, String dockerRegistryUrl, String dockerRegistryUserName, String dockerRegistryPassword, String kubeBase64Config) throws IllegalAccessException, IOException, InvocationTargetException {
            Dew.mavenSession = session;
            Dew.mavenPluginManager = pluginManager;
            if (profile == null) {
                profile = BasicMojo.FLAG_DEW_DEVOPS_DEFAULT_PROFILE;
            }
            log.info("Active profile : " + profile);
            initPath(session);
            if (!initialized.getAndSet(true)) {
                GitHelper.init(log);
                YamlHelper.init(log);
                initFinalConfig(profile, dockerHost, dockerRegistryUrl, dockerRegistryUserName, dockerRegistryPassword, kubeBase64Config);
                Config.getProjects().values().forEach(config -> {
                    DockerHelper.init(config.getId(), log, config.getDocker().getHost(),
                            config.getDocker().getRegistryUrl(), config.getDocker().getRegistryUserName(), config.getDocker().getRegistryPassword());
                    KubeHelper.init(config.getId(), log, config.getKube().getBase64Config());
                });
            }
            if (Config.getCurrentProject() != null) {
                if (Config.getCurrentProject().getKube().getBase64Config() == null
                        || Config.getCurrentProject().getKube().getBase64Config().isEmpty()) {
                    throw new RuntimeException("Kubernetes config can't be empty");
                }
            }
        }

        private static void initPath(MavenSession session) {
            basicDirectory = session.getTopLevelProject().getBasedir().getPath() + File.separator;
        }

        private static void initFinalConfig(String profile,
                                            String dockerHost, String dockerRegistryUrl, String dockerRegistryUserName, String dockerRegistryPassword, String kubeBase64Config) throws IOException, InvocationTargetException, IllegalAccessException {
            String basicConfig = "";
            if (new File(basicDirectory + ".dew").exists()) {
                basicConfig = $.file.readAllByPathName(basicDirectory + ".dew", "UTF-8") + "\r\n";
            }
            for (MavenProject project : mavenSession.getProjects()) {
                String projectDirectory = project.getBasedir().getPath() + File.separator;
                String projectConfig;
                AppKind appKind = Dew.Utils.checkAppKind(project);
                if (appKind == null) {
                    // 不支持的类型
                    continue;
                }
                if (!basicDirectory.equals(projectDirectory) && new File(projectDirectory + ".dew").exists()) {
                    // FIXME 支持两个文件使用不同缩进
                    projectConfig = basicConfig + $.file.readAllByPathName(projectDirectory + ".dew", "UTF-8");
                } else {
                    projectConfig = basicConfig;
                }
                DewConfig dewConfig;
                if (!projectConfig.isEmpty()) {
                    // FIXME 默认值会干扰复制
                    dewConfig = YamlHelper.toObject(DewConfig.class, projectConfig);
                    // TODO 各profile以default为基础做自定义
                } else {
                    dewConfig = new DewConfig();
                }
                if (!profile.equalsIgnoreCase(BasicMojo.FLAG_DEW_DEVOPS_DEFAULT_PROFILE) && !dewConfig.getProfiles().containsKey(profile)) {
                    throw new IOException("Can't be found [" + profile + "] profile at " + project.getArtifactId());
                }
                if (profile.equalsIgnoreCase(BasicMojo.FLAG_DEW_DEVOPS_DEFAULT_PROFILE) && dewConfig.isSkip()
                        || !profile.equalsIgnoreCase(BasicMojo.FLAG_DEW_DEVOPS_DEFAULT_PROFILE) && dewConfig.getProfiles().get(profile).isSkip()) {
                    // 配置为跳过
                    continue;
                }
                Config.config.getProjects().put(project.getId(),
                        ConfigBuilder.buildProject(appKind, profile, dewConfig, project,
                                dockerHost, dockerRegistryUrl, dockerRegistryUserName, dockerRegistryPassword, kubeBase64Config));
                log.debug("[" + project.getGroupId() + ":" + project.getArtifactId() + "] configured");
            }
        }
    }

    public static class Config {

        private static FinalConfig config = new FinalConfig();

        public static FinalProjectConfig getCurrentProject() {
            return config.getProjects().get(mavenSession.getCurrentProject().getId());
        }

        public static Map<String, FinalProjectConfig> getProjects() {
            return config.getProjects();
        }

    }

    public static class Invoke {

        public static void invoke(String groupId, String artifactId, String version, String goal, Map<String, String> configuration) throws MojoExecutionException {
            log.debug("invoke groupId = " + groupId + " ,artifactId = " + artifactId + " ,version = " + version);
            List<Element> config = configuration.entrySet().stream()
                    .map(item -> element(item.getKey(), item.getValue()))
                    .collect(Collectors.toList());
            MojoExecutor.executeMojo(
                    plugin(
                            groupId(groupId),
                            artifactId(artifactId),
                            version(version)
                    ),
                    goal(goal),
                    configuration(config.toArray(new Element[]{})),
                    executionEnvironment(
                            mavenSession.getCurrentProject(),
                            mavenSession,
                            mavenPluginManager
                    )
            );
        }

    }

    public static class Utils {

        public static AppKind checkAppKind(MavenProject mavenProject) {
            AppKind appKind = null;
            if (mavenProject.getPackaging().equalsIgnoreCase("pom")
                    || mavenProject.getPackaging().equalsIgnoreCase("maven-plugin")) {
                // 排除 POM 及 插件类型
            } else if (
                    new File(mavenProject.getBasedir().getPath() + File.separator + "src" + File.separator + "main" + File.separator + "resources").exists()
                            && Arrays.stream(Objects.requireNonNull(new File(mavenProject.getBasedir().getPath() + File.separator + "src" + File.separator + "main" + File.separator + "resources").listFiles()))
                            .anyMatch((res -> res.getName().toLowerCase().contains("application")
                                    || res.getName().toLowerCase().contains("bootstrap")))
                            // 包含DependencyManagement内容，不精确
                            && mavenProject.getManagedVersionMap().containsKey("org.springframework.boot:spring-boot-starter-web:jar")
                // TODO 以判断无效

                /* mavenProject.getResources() != null && mavenProject.getResources().stream()
                    .filter(res -> res.getDirectory().contains("src\\main\\resources")
                            && res.getIncludes() != null)
                    .anyMatch(res -> res.getIncludes().stream()
                            .anyMatch(file -> file.toLowerCase().contains("application")
                                    || file.toLowerCase().contains("bootstrap")))
                   && mavenProject.getArtifacts()
                    .stream().anyMatch(artifact ->
                    artifact.getScope().equals(Artifact.SCOPE_RUNTIME)
                            && artifact.getArtifactId().equalsIgnoreCase("spring-boot-starter-web")*/
            ) {
                appKind = AppKind.JVM_SERVICE;
            } else {
                // TODO
            }
            Dew.log.debug("Current app [" + mavenProject.getArtifactId() + "] kind is " + appKind);
            return appKind;
        }

    }


}
