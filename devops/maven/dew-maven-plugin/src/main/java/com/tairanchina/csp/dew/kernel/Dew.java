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
import org.apache.maven.artifact.DependencyResolutionRequiredException;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.BuildPluginManager;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.project.MavenProject;
import org.twdata.maven.mojoexecutor.MojoExecutor;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.twdata.maven.mojoexecutor.MojoExecutor.*;

public class Dew {

    public static Log log;

    public static FinalConfig config;
    public static String rootDirectory;
    public static String rootTargetDirectory;
    public static String projectDirectory;
    public static String projectTargetDirectory;

    public static MavenProject mavenProject;
    private static MavenSession mavenSession;
    private static BuildPluginManager mavenPluginManager;

    public static class Init {

        public static void init(Boolean skip, String profile,
                                String dockerHost, String dockerRegistryUrl, String dockerRegistryUserName, String dockerRegistryPassword, String kubeBase64Config,
                                MavenProject project, MavenSession session, BuildPluginManager pluginManager) throws IllegalAccessException, IOException, InvocationTargetException {
            Dew.mavenProject = project;
            Dew.mavenSession = session;
            Dew.mavenPluginManager = pluginManager;
            initPath(session);
            initFinalConfig(skip, profile, dockerHost, dockerRegistryUrl, dockerRegistryUserName, dockerRegistryPassword, kubeBase64Config);
            if (config.isSkip()) {
                return;
            }
            if (config.getKube().getBase64Config() == null
                    || config.getKube().getBase64Config().isEmpty()) {
                throw new RuntimeException("Kubernetes config can't be empty");
            }
            initHelper();
        }

        private static void initPath(MavenSession session) {
            rootDirectory = session.getExecutionRootDirectory() + File.separator;
            rootTargetDirectory = rootDirectory + "target" + File.separator;
            projectDirectory = session.getCurrentProject().getBasedir().getPath() + File.separator;
            projectTargetDirectory = projectDirectory + "target" + File.separator;
        }

        private static void initFinalConfig(Boolean skip, String profile,
                                            String dockerHost, String dockerRegistryUrl, String dockerRegistryUserName, String dockerRegistryPassword, String kubeBase64Config) throws IOException, InvocationTargetException, IllegalAccessException {
            AppKind appKind = Dew.Utils.checkAppKind();
            if (appKind == null) {
                Dew.config = new FinalConfig();
                Dew.config.setSkip(true);
                return;
            }
            YamlHelper.init(log);
            StringBuilder config = new StringBuilder();
            if (new File(rootDirectory + ".dew").exists()) {
                config.append($.file.readAllByPathName(rootDirectory + ".dew", "UTF-8"));
                config.append("\r\n");
            }
            if (!rootDirectory.equals(projectDirectory) && new File(projectDirectory + ".dew").exists()) {
                config.append($.file.readAllByPathName(projectDirectory + ".dew", "UTF-8"));
            }
            DewConfig dewConfig;
            if (!config.toString().isEmpty()) {
                dewConfig = YamlHelper.toObject(DewConfig.class, config.toString());
                // 各profile以default为基础做自定义
                dewConfig.getProfiles().forEach((k, v) ->
                        dewConfig.getProfiles().put(k, YamlHelper.toObject(DewProfile.class, config + "\r\n" + YamlHelper.toYaml(v))));
            } else {
                dewConfig = new DewConfig();
            }
            Dew.config = ConfigBuilder.build(appKind, profile, dewConfig, skip, dockerHost, dockerRegistryUrl, dockerRegistryUserName, dockerRegistryPassword, kubeBase64Config);
        }

        private static void initHelper() {
            GitHelper.init(log);
            DockerHelper.init(log, config.getDocker().getHost(), config.getDocker().getRegistryUrl(), config.getDocker().getRegistryUserName(), config.getDocker().getRegistryPassword());
            KubeHelper.init(log, config.getKube().getBase64Config());
        }

    }

    public static class Context {

        public static boolean setPropIfAbsent(String key, String value, String defaultValue) {
            String oldValue = getProp(key, defaultValue);
            if (oldValue == null || oldValue.equals(defaultValue)) {
                setProp(key, value);
                return true;
            } else {
                return false;
            }
        }

        public static String getSetProp(String key, String value, String defaultValue) {
            String oldValue = getProp(key, defaultValue);
            setProp(key, value);
            return oldValue;
        }

        public static void setProp(String key, String value) {
            mavenSession.getUserProperties().setProperty(key, value);
        }

        public static String getProp(String key, String defaultValue) {
            return mavenSession.getUserProperties().getProperty(key, defaultValue);
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
                            mavenProject,
                            mavenSession,
                            mavenPluginManager
                    )
            );
        }

        public static void exec(String cmd, String outFileName) throws MojoExecutionException {
            log.debug("exec cmd = " + cmd + " ,outFileName = " + outFileName);
            MojoExecutor.executeMojo(
                    plugin(
                            groupId("org.codehaus.mojo"),
                            artifactId("exec-maven-plugin"),
                            version("1.6.0")
                    ),
                    goal("exec"),
                    configuration(
                            element("executable", cmd + " > " + outFileName)
                    ),
                    executionEnvironment(
                            mavenProject,
                            mavenSession,
                            mavenPluginManager
                    )
            );
        }

        public static List<String> execAndGet(String cmd, String outFileName) throws MojoExecutionException, IOException {
            exec(cmd, outFileName);
            Path tmpPath = Paths.get(outFileName);
            List<String> result = Files.readAllLines(tmpPath);
            Files.delete(tmpPath);
            return result;
        }

    }

    public static class Utils {

        public static AppKind checkAppKind() {
            AppKind appKind = null;
            try {
                if (Dew.mavenProject.getPackaging().equalsIgnoreCase("pom")
                        || Dew.mavenProject.getPackaging().equalsIgnoreCase("maven-plugin")) {
                    // 排除 POM 及 插件类型
                } else if (Dew.mavenProject.getCompileClasspathElements()
                        .stream().anyMatch(jar -> jar.contains("spring-web-"))) {
                    // TODO 还要再查询是否有main方法
                    appKind = AppKind.JVM_SERVICE;
                } else {
                    // TODO
                }
            } catch (DependencyResolutionRequiredException e) {
                throw new RuntimeException(e);
            }
            Dew.log.debug("Current app kind is " + appKind);
            return appKind;
        }

    }


}
