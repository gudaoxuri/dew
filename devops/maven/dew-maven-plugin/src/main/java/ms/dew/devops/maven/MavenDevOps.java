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

package ms.dew.devops.maven;

import com.ecfront.dew.common.$;
import ms.dew.devops.kernel.DevOps;
import ms.dew.devops.kernel.config.ConfigBuilder;
import ms.dew.devops.kernel.config.DewConfig;
import ms.dew.devops.kernel.config.FinalProjectConfig;
import ms.dew.devops.kernel.exception.ConfigException;
import ms.dew.devops.kernel.function.NeedProcessChecker;
import ms.dew.devops.kernel.helper.GitHelper;
import ms.dew.devops.kernel.helper.YamlHelper;
import ms.dew.devops.kernel.plugin.appkind.AppKindPlugin;
import ms.dew.devops.kernel.plugin.deploy.DeployPlugin;
import ms.dew.devops.kernel.util.DewLog;
import ms.dew.devops.kernel.util.ExecuteOnceProcessor;
import ms.dew.devops.maven.function.AppKindPluginSelector;
import ms.dew.devops.maven.function.DependenciesResolver;
import ms.dew.devops.maven.function.DeployPluginSelector;
import ms.dew.devops.maven.function.MavenSkipProcessor;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.BuildPluginManager;
import org.apache.maven.project.MavenProject;
import org.slf4j.Logger;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;


/**
 * DevOps maven 插件核心类.
 *
 * @author gudaoxuri
 */
public class MavenDevOps {

    private static Logger logger = DewLog.build(MavenDevOps.class);

    /**
     * Init.
     */
    public static class Init {

        /**
         * Init.
         *
         * @param session                         the session
         * @param pluginManager                   the plugin manager
         * @param inputProfile                    the input profile
         * @param inputDockerHost                 the input docker host
         * @param inputDockerRegistryUrl          the input docker registry url
         * @param inputDockerRegistryUserName     the input docker registry user name
         * @param inputDockerRegistryPassword     the input docker registry password
         * @param inputKubeBase64Config           the input kube base 64 config
         * @param inputAssignationProjects        the assignation projects
         * @param dockerHostAppendOpt             the docker host append opt
         * @param dockerRegistryUrlAppendOpt      the docker registry url append opt
         * @param dockerRegistryUserNameAppendOpt the docker registry user name append opt
         * @param dockerRegistryPasswordAppendOpt the docker registry password append opt
         * @param kubeBase64ConfigAppendOpt       the kube base 64 config append opt
         * @param mockClasspath                   the mock classpath
         */
        public static synchronized void init(MavenSession session, BuildPluginManager pluginManager,
                                             String inputProfile,
                                             String inputDockerHost, String inputDockerRegistryUrl,
                                             String inputDockerRegistryUserName, String inputDockerRegistryPassword,
                                             String inputKubeBase64Config, String inputAssignationProjects,
                                             Optional<String> dockerHostAppendOpt, Optional<String> dockerRegistryUrlAppendOpt,
                                             Optional<String> dockerRegistryUserNameAppendOpt, Optional<String> dockerRegistryPasswordAppendOpt,
                                             Optional<String> kubeBase64ConfigAppendOpt,
                                             String mockClasspath) {
            try {
                Config.initMavenProject(session, pluginManager);
                if (ExecuteOnceProcessor.executedCheck(MavenDevOps.class)) {
                    return;
                }
                logger.info("Start init ...");
                logger.info("Dependencies resolver ...");
                DependenciesResolver.init(session);
                inputProfile = inputProfile.toLowerCase();
                logger.info("Active profile : " + inputProfile);
                // 全局只初始化一次
                GitHelper.init(logger);
                YamlHelper.init(logger);
                initFinalConfig(session, inputProfile,
                        inputDockerHost, inputDockerRegistryUrl, inputDockerRegistryUserName, inputDockerRegistryPassword,
                        inputKubeBase64Config, inputAssignationProjects,
                        dockerHostAppendOpt, dockerRegistryUrlAppendOpt, dockerRegistryUserNameAppendOpt, dockerRegistryPasswordAppendOpt,
                        kubeBase64ConfigAppendOpt);
                DevOps.Init.init(mockClasspath);
                Config.initMavenProject(session, pluginManager);
            } catch (Exception e) {
                throw new ConfigException(e.getMessage(), e);
            }
        }

        private static void initFinalConfig(MavenSession session, String inputProfile,
                                            String inputDockerHost, String inputDockerRegistryUrl,
                                            String inputDockerRegistryUserName, String inputDockerRegistryPassword,
                                            String inputKubeBase64Config, String inputAssignationProjects,
                                            Optional<String> dockerHostAppendOpt,
                                            Optional<String> dockerRegistryUrlAppendOpt,
                                            Optional<String> dockerRegistryUserNameAppendOpt,
                                            Optional<String> dockerRegistryPasswordAppendOpt,
                                            Optional<String> kubeBase64ConfigAppendOpt)
                throws IOException, InvocationTargetException, IllegalAccessException {
            logger.info("Init final config ...");
            String basicDirectory = session.getTopLevelProject().getBasedir().getPath() + File.separator;
            // 基础配置
            String basicConfig = "";
            if (new File(basicDirectory + ".dew").exists()) {
                basicConfig = ConfigBuilder.mergeProfiles($.file.readAllByPathName(basicDirectory + ".dew", "UTF-8")) + "\r\n";
                DewConfig dewConfig = YamlHelper.toObject(DewConfig.class, basicConfig);
                if (inputProfile.equalsIgnoreCase(ConfigBuilder.FLAG_DEW_DEVOPS_DEFAULT_PROFILE)) {
                    dewConfig.getProfiles().clear();
                    DevOps.Config.basicProfileConfig = dewConfig;
                } else {
                    DevOps.Config.basicProfileConfig = dewConfig.getProfiles().get(inputProfile);
                }
            }
            for (MavenProject project : session.getProjectDependencyGraph().getSortedProjects()) {
                Optional<AppKindPlugin> appKindPluginOpt = AppKindPluginSelector.select(project);
                if (!appKindPluginOpt.isPresent()) {
                    continue;
                }
                DeployPlugin deployPlugin = DeployPluginSelector.select(appKindPluginOpt.get(), project);

                String projectDirectory = project.getBasedir().getPath() + File.separator;
                // 每个项目自定义的配置
                String projectConfig;
                if (!basicDirectory.equals(projectDirectory) && new File(projectDirectory + ".dew").exists()) {
                    // 合并基础配置与项目自定义配置
                    projectConfig = ConfigBuilder.mergeProject(basicConfig,
                            $.file.readAllByPathName(projectDirectory + ".dew", "UTF-8"));
                    // basicConfig 有 Profile(s) 而 projectConfig 没有配置的情况
                    // 通过上一步合并后需要再次执行 default profile merge 到各 profile操作
                    projectConfig = ConfigBuilder.mergeProfiles(projectConfig);
                } else {
                    projectConfig = basicConfig;
                }
                DewConfig dewConfig;
                if (!projectConfig.isEmpty()) {
                    dewConfig = YamlHelper.toObject(DewConfig.class, projectConfig);
                } else {
                    dewConfig = new DewConfig();
                }
                Optional<FinalProjectConfig> finalProjectConfigOpt =
                        ConfigBuilder.buildProject(dewConfig, appKindPluginOpt.get(), deployPlugin, project, inputProfile,
                                inputDockerHost, inputDockerRegistryUrl, inputDockerRegistryUserName, inputDockerRegistryPassword,
                                inputKubeBase64Config,
                                dockerHostAppendOpt, dockerRegistryUrlAppendOpt, dockerRegistryUserNameAppendOpt, dockerRegistryPasswordAppendOpt,
                                kubeBase64ConfigAppendOpt);
                if (finalProjectConfigOpt.isPresent()) {
                    finalProjectConfigOpt.get().setMavenSession(session);
                    finalProjectConfigOpt.get().setMavenProject(project);
                    DevOps.Config.getFinalConfig().getProjects().put(project.getId(), finalProjectConfigOpt.get());
                    logger.debug("[" + project.getGroupId() + ":" + project.getArtifactId() + "] configured");
                } else {
                    logger.debug("[" + project.getGroupId() + ":" + project.getArtifactId() + "] skipped");
                }
            }
            initAssignDeploymentProjects(inputAssignationProjects, session.getProjectDependencyGraph().getSortedProjects());
        }

        private static void initAssignDeploymentProjects(String assignationProjects, List<MavenProject> projects) {
            if (null != assignationProjects) {
                projects.forEach(project -> {
                    if (assignationProjects.contains(project.getArtifactId())) {
                        notSkip(DevOps.Config.getProjectConfig(project.getId()), project, false);
                    } else {
                        DevOps.Config.getProjectConfig(project.getId()).skip("Not assign to release", false);
                    }
                });
            }
        }

        private static void notSkip(FinalProjectConfig projectConfig, MavenProject project, Boolean isParent) {
            if (isParent) {
                projectConfig.setSkip(false);
                projectConfig.setSkipReason("");
            }
            if (project.isExecutionRoot()) {
                return;
            }
            notSkip(DevOps.Config.getProjectConfig(project.getParent().getId()), project.getParent(), true);
        }
    }

    /**
     * 配置输出.
     */
    public static class Config {

        // e.g. maven.test.skip
        private static final Map<String, Map<String, String>> mavenProps = new HashMap<>();

        /**
         * 获取当前Maven项目对象.
         *
         * @return the current maven project
         */
        private static MavenProject getMavenProject(String projectId) {
            return DevOps.Config.getProjectConfig(projectId).getMavenProject();
        }

        /**
         * 设置Maven的属性.
         *
         * @param projectId Maven项目Id
         * @param key       属性名
         * @param value     属性值
         */
        public static void setMavenProperty(String projectId, String key, String value) {
            if (!mavenProps.containsKey(projectId)) {
                mavenProps.put(projectId, new HashMap<>());
            }
            mavenProps.get(projectId).put(key, value);
            getMavenProject(projectId).getProperties().putAll(mavenProps.get(projectId));
        }

        /**
         * 初始化当前Maven项目.
         *
         * @param session       the session
         * @param pluginManager this plugin manager
         */
        private static void initMavenProject(MavenSession session, BuildPluginManager pluginManager) {
            String projectId = session.getCurrentProject().getId();
            if (!DevOps.Config.getFinalConfig().getProjects().containsKey(projectId)) {
                // 尚未初始化
                return;
            }
            if (!mavenProps.containsKey(projectId)) {
                mavenProps.put(projectId, new HashMap<>());
            }
            // 更新 session project plugin
            FinalProjectConfig projectConfig = DevOps.Config.getFinalConfig().getProjects().get(projectId);
            projectConfig.setMavenSession(session);
            projectConfig.setPluginManager(pluginManager);
            projectConfig.setMavenProject(session.getCurrentProject());
            // 更新maven prop
            projectConfig.getMavenProject().getProperties().putAll(mavenProps.get(projectId));
            // 设置为当前项目
            DevOps.Config.setCurrentProjectId(projectId);
        }
    }

    /**
     * Process.
     */
    public static class Process {

        /**
         * Need process check.
         *
         * @param quiet the quiet
         */
        public static synchronized void needProcessCheck(boolean quiet) {
            NeedProcessChecker.checkNeedProcessProjects(quiet);
            MavenSkipProcessor.process();
        }
    }
}


