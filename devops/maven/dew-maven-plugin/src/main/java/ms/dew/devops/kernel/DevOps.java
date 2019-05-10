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

package ms.dew.devops.kernel;

import ms.dew.devops.kernel.config.DewProfile;
import ms.dew.devops.kernel.config.FinalConfig;
import ms.dew.devops.kernel.config.FinalProjectConfig;
import ms.dew.devops.kernel.exception.ProjectProcessException;
import ms.dew.devops.kernel.function.ExecuteEventProcessor;
import ms.dew.devops.kernel.helper.DockerHelper;
import ms.dew.devops.kernel.helper.KubeHelper;
import ms.dew.devops.kernel.util.DewLog;
import ms.dew.devops.kernel.util.ExecuteOnceProcessor;
import ms.dew.devops.kernel.util.ExitMonitorProcessor;
import ms.dew.notification.NotifyConfig;
import org.apache.maven.plugin.MojoExecutionException;
import org.slf4j.Logger;
import org.twdata.maven.mojoexecutor.MojoExecutor;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.twdata.maven.mojoexecutor.MojoExecutor.*;

/**
 * DevOps 核心类.
 *
 * @author gudaoxuri
 */
public class DevOps {

    private static Logger logger = DewLog.build(DevOps.class);

    /**
     * 全局停止标识，如果为true则表示停止后续各项目的所有操作.
     * <p>
     * 目前仅在不存在需要处理的项目及人为中止处理的情况下为 true
     */
    public static boolean stopped = false;

    /**
     * 初始化.
     */
    public static class Init {

        /**
         * Init.
         *
         * @param mockClasspath the mock classpath
         */
        public static synchronized void init(String mockClasspath) {
            if (ExecuteOnceProcessor.executedCheck(DevOps.class)) {
                return;
            }
            logger.info("Init env ...");
            initEnv();
            logger.info("Init notify ...");
            initNotify();
            initMock(mockClasspath);
            shutdownHook();
        }

        private static void initEnv() {
            Config.getFinalConfig().getProjects().values().forEach(config -> {
                DockerHelper.init(config.getId(), logger,
                        config.getDocker().getHost(),
                        config.getDocker().getRegistryUrl(),
                        config.getDocker().getRegistryUserName(),
                        config.getDocker().getRegistryPassword());
                KubeHelper.init(config.getId(), logger,
                        config.getKube().getBase64Config());
                if (config.getAppendProfile() != null) {
                    // 初始化附加环境，多用于版本重用模式
                    DockerHelper.init(config.getId() + "-append", logger,
                            config.getAppendProfile().getDocker().getHost(),
                            config.getAppendProfile().getDocker().getRegistryUrl(),
                            config.getAppendProfile().getDocker().getRegistryUserName(),
                            config.getAppendProfile().getDocker().getRegistryPassword());
                    KubeHelper.init(config.getId() + "-append", logger,
                            config.getAppendProfile().getKube().getBase64Config());
                }
            });
        }

        /**
         * Init notify.
         */
        private static void initNotify() {
            Map<String, NotifyConfig> configMap = new HashMap<>();
            Config.getFinalConfig().getProjects().entrySet().stream()
                    .filter(config -> config.getValue().getNotifies() != null
                            && !config.getValue().getNotifies().isEmpty())
                    .forEach(config ->
                            config.getValue().getNotifies().forEach(notifyConfig -> {
                                if (notifyConfig.getType() == null) {
                                    notifyConfig.setType(NotifyConfig.TYPE_DD);
                                }
                                configMap.put(config.getKey() + "_" + notifyConfig.getType(), notifyConfig);
                            }));
            // 对于根项目为 skip 的情况强制启用通知
            DewProfile dewProfile = Config.basicProfileConfig;
            if (dewProfile != null && dewProfile.getNotifies() != null) {
                dewProfile.getNotifies().forEach(notifyConfig -> {
                    if (notifyConfig.getType() == null) {
                        notifyConfig.setType(NotifyConfig.TYPE_DD);
                    }
                    // 添加key为空的全局通知配置
                    configMap.put("" + "_" + notifyConfig.getType(), notifyConfig);
                });
            }
            configMap.values().forEach(notifyConfig -> {
                if (notifyConfig.getType().equalsIgnoreCase(NotifyConfig.TYPE_DD)
                        && !notifyConfig.getArgs().containsKey("msgType")) {
                    // 默认使用Markdown格式
                    notifyConfig.getArgs().put("msgType", "markdown");
                }
            });
            ms.dew.notification.Notify.init(configMap, flag -> "");
        }

        /**
         * Init mock.
         *
         * @param mockClasspath the mock classpath
         */
        private static void initMock(String mockClasspath) {
            if (mockClasspath == null || mockClasspath.trim().isEmpty()) {
                return;
            }
            logger.warn("Discover mock configuration, mock class path is " + mockClasspath);
            try {
                Mock.loadClass(mockClasspath);
                Mock.invokeMock();
            } catch (NoSuchMethodException
                    | InvocationTargetException
                    | MalformedURLException
                    | ClassNotFoundException
                    | IllegalAccessException
                    | InstantiationException e) {
                throw new ProjectProcessException("Mock invoke error", e);
            }
        }

        /**
         * Init shutdown process.
         */
        private static void shutdownHook() {
            ExitMonitorProcessor.hook(status -> {
                if (status != 0
                        && !Config.getProjectConfig(Config.getCurrentProjectId()).isHasError()
                        && Config.getProjectConfig(Config.getCurrentProjectId()).getSkipReason().isEmpty()) {
                    Config.getProjectConfig(Config.getCurrentProjectId()).skip("Uncaught error", true);
                }
                ExecuteEventProcessor.onShutdown(Config.getFinalConfig().getProjects());
            });
        }

    }

    /**
     * 配置输出.
     */
    public static class Config {

        // TODO
        // 基础配置，全局 .dew 配置对应的当前Profile
        public static DewProfile basicProfileConfig;

        // TODO
        // 当前的项目ID，注意多线程下有问题
        private static String currentProjectId;

        // 最终的配置
        private static FinalConfig finalConfig = new FinalConfig();

        /**
         * 获取项目配置.
         *
         * @param projectId the project id
         * @return the current project
         */
        public static FinalProjectConfig getProjectConfig(String projectId) {
            return finalConfig.getProjects().get(projectId);
        }

        /**
         * 获取所有配置.
         *
         * @return the final config
         */
        public static FinalConfig getFinalConfig() {
            return finalConfig;
        }

        public static String getCurrentProjectId() {
            return currentProjectId;
        }

        public static void setCurrentProjectId(String currentProjectId) {
            Config.currentProjectId = currentProjectId;
        }
    }

    /**
     * Maven Mojo调用.
     */
    public static class Invoke {

        /**
         * 调用指定的Maven mojo.
         *
         * @param groupId       the group id
         * @param artifactId    the artifact id
         * @param version       the version
         * @param goal          the goal
         * @param configuration the configuration
         * @param projectConfig the final project config
         */
        public static void invoke(String groupId, String artifactId, String version,
                                  String goal, Map<String, String> configuration,
                                  FinalProjectConfig projectConfig
        ) {
            logger.debug("invoke groupId = " + groupId + " ,artifactId = " + artifactId + " ,version = " + version);
            List<MojoExecutor.Element> config = configuration.entrySet().stream()
                    .map(item -> element(item.getKey(), item.getValue()))
                    .collect(Collectors.toList());
            org.apache.maven.model.Plugin plugin;
            if (version == null) {
                plugin = plugin(groupId, artifactId);
            } else {
                plugin = plugin(groupId, artifactId, version);
            }
            try {
                executeMojo(
                        plugin,
                        goal(goal),
                        configuration(config.toArray(new Element[]{})),
                        executionEnvironment(
                                projectConfig.getMavenProject(),
                                projectConfig.getMavenSession(),
                                projectConfig.getPluginManager()
                        )
                );
            } catch (MojoExecutionException e) {
                throw new ProjectProcessException("Invoke maven mojo error", e);
            }
        }

    }

    /**
     * Mock.
     */
    public static class Mock {

        /**
         * Load external class.
         * <p>
         * 要求：
         * <p>
         * classPath // 根文件路径
         * -- mock // 存在名为 mock 的路径
         * -- some.class // 仅加载 mock 路径下的 class 文件，不支持子路径
         * -- Mock.class // 存在 Mock 类作为调用入口
         *
         * @param classPath the class path
         * @throws NoSuchMethodException     the no such method exception
         * @throws MalformedURLException     the malformed url exception
         * @throws InvocationTargetException the invocation target exception
         * @throws IllegalAccessException    the illegal access exception
         * @throws ClassNotFoundException    the class not found exception
         */
        public static void loadClass(String classPath)
                throws NoSuchMethodException, MalformedURLException, InvocationTargetException, IllegalAccessException, ClassNotFoundException {
            File clazzPath = new File(classPath);
            if (!clazzPath.exists() || clazzPath.isFile()) {
                logger.debug("Not found mock class path in " + classPath);
                return;
            }
            Optional<File> mockFiles = Stream.of(clazzPath.listFiles()).filter(f -> f.getName().equals("mock") && f.isDirectory()).findAny();
            if (!mockFiles.isPresent()) {
                logger.debug("Mock class path must contain a directory named 'mock'");
                return;
            }
            Method method = URLClassLoader.class.getDeclaredMethod("addURL", URL.class);
            boolean accessible = method.isAccessible();
            try {
                if (!accessible) {
                    method.setAccessible(true);
                }
                URLClassLoader classLoader = (URLClassLoader) ClassLoader.getSystemClassLoader();
                method.invoke(classLoader, clazzPath.toURI().toURL());
            } finally {
                method.setAccessible(accessible);
            }
            File[] classFiles = mockFiles.get().listFiles(pathname -> pathname.getName().endsWith(".class"));
            for (File file : classFiles) {
                logger.debug("Loading class " + file.getName());
                Class.forName("mock." + file.getName().split("\\.")[0]);
            }
        }

        /**
         * Invoke mock.
         * <p>
         * 调用Mock类执行 mock 逻辑，Mock类要求构造方法为空
         *
         * @throws ClassNotFoundException the class not found exception
         * @throws IllegalAccessException the illegal access exception
         * @throws InstantiationException the instantiation exception
         */
        public static void invokeMock() throws ClassNotFoundException, IllegalAccessException, InstantiationException {
            Class.forName("mock.Mock").newInstance();
        }
    }
}
