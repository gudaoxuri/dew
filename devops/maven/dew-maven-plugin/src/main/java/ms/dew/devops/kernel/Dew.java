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

import com.ecfront.dew.common.$;
import ms.dew.devops.exception.ProjectProcessException;
import ms.dew.devops.helper.DockerHelper;
import ms.dew.devops.helper.GitHelper;
import ms.dew.devops.helper.KubeHelper;
import ms.dew.devops.helper.YamlHelper;
import ms.dew.devops.kernel.config.*;
import ms.dew.devops.kernel.function.ExecuteEventProcessor;
import ms.dew.devops.util.ExitMonitorProcessor;
import ms.dew.notification.NotifyConfig;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.BuildPluginManager;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.project.MavenProject;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.twdata.maven.mojoexecutor.MojoExecutor.*;

/**
 * DevOps Kernel class.
 *
 * @author gudaoxuri
 */
public class Dew {

    /**
     * 全局已初始化.
     */
    public static boolean initialized = false;

    /**
     * 全局停止标识，如果为true则表示停止后续各项目的所有操作.
     * <p>
     * 目前仅在不存在需要处理的项目及人为中止处理的情况下为 true
     */
    public static boolean stopped = false;
    /**
     * 日志类.
     *
     * @see ms.dew.devops.util.DewLog
     */
    public static Log log;

    private static MavenSession mavenSession;
    private static BuildPluginManager mavenPluginManager;

    /**
     * 命令行参数常量定义.
     */
    public static class Constants {
        /**
         * 默认环境值.
         */
        public static final String FLAG_DEW_DEVOPS_DEFAULT_PROFILE = "default";


        // ============= 公共场景使用 =============
        /**
         * 环境标识.
         */
        public static final String FLAG_DEW_DEVOPS_PROFILE = "dew.devops.profile";
        /**
         * Kubernetes Base64 配置标识.
         */
        public static final String FLAG_DEW_DEVOPS_KUBE_CONFIG = "dew.devops.kube.config";

        // ============= 发布与回滚使用 =============

        /**
         * Docker Host标识.
         * <p>
         * e.g. tcp://10.200.131.182:2375.
         */
        public static final String FLAG_DEW_DEVOPS_DOCKER_HOST = "dew.devops.docker.host";
        /**
         * Docker registry url标识.
         * <p>
         * e.g. https://harbor.dew.env/v2
         */
        public static final String FLAG_DEW_DEVOPS_DOCKER_REGISTRY_URL = "dew.devops.docker.registry.url";
        /**
         * Docker registry 用户名标识.
         */
        public static final String FLAG_DEW_DEVOPS_DOCKER_REGISTRY_USERNAME = "dew.devops.docker.registry.username";
        /**
         * Docker registry 密码标识.
         */
        public static final String FLAG_DEW_DEVOPS_DOCKER_REGISTRY_PASSWORD = "dew.devops.docker.registry.password";
        /**
         * 是否静默处理标识.
         * <p>
         * 仅对发布/回滚有效
         */
        public static final String FLAG_DEW_DEVOPS_QUIET = "dew.devops.quiet";
        /**
         * 是否忽略Maven仓库中已存在的版本标识.
         * <p>
         * 仅对发布/回滚有效
         */
        public static final String FLAG_DEW_DEVOPS_MAVEN_VERSION_EXIST_IGNORE = "dew.devops.maven.version.exist.ignore";

        // ============= 日志及调试场景使用 =============
        /**
         * 要使用的Pod名称标识.
         */
        public static final String FLAG_DEW_DEVOPS_POD_NAME = "dew.devops.podName";
        // ============= 日志场景使用 =============
        /**
         * 是否滚动查看日志标识.
         */
        public static final String FLAG_DEW_DEVOPS_LOG_FOLLOW = "dew.devops.log.follow";
        // ============= 调试场景使用 =============
        /**
         * 转发端口标识.
         */
        public static final String FLAG_DEW_DEVOPS_DEBUG_FORWARD_PORT = "dew.devops.debug.forward.port";

        // ============= 伸缩场景使用 =============

        /**
         * 伸缩Pod数量标识.
         */
        public static final String FLAG_DEW_DEVOPS_SCALE_REPLICAS = "dew.devops.scale.replicas";
        /**
         * 是否启用自动伸缩标识.
         */
        public static final String FLAG_DEW_DEVOPS_SCALE_AUTO = "dew.devops.scale.auto";
        /**
         * 自动伸缩Pod数下限标识.
         */
        public static final String FLAG_DEW_DEVOPS_SCALE_AUTO_REPLICAS_MIN = "dew.devops.scale.auto.minReplicas";
        /**
         * 自动伸缩Pod数上限标识.
         */
        public static final String FLAG_DEW_DEVOPS_SCALE_AUTO_REPLICAS_MAX = "dew.devops.scale.auto.maxReplicas";
        /**
         * 自动伸缩条件：CPU平均使用率标识.
         */
        public static final String FLAG_DEW_DEVOPS_SCALE_AUTO_CPU_AVG = "dew.devops.scale.auto.cpu.averageUtilization";

        // ============= 测试场景使用 =============
        /**
         * Mock场景下加载外部class的路径标识.
         */
        public static final String FLAG_DEW_DEVOPS_MOCK_CLASS_PATH = "dew.devops.mock.classpath";

        /**
         * 获取Maven属性.
         *
         * @param session maven session
         * @return properties maven properties
         */
        public static Map<String, String> getMavenProperties(MavenSession session) {
            if (session == null && mavenSession != null) {
                session = mavenSession;
            }
            Map<String, String> props = new HashMap<>();
            props.putAll(session.getSystemProperties().entrySet().stream()
                    .collect(Collectors.toMap(prop ->
                            prop.getKey().toString().toLowerCase().trim(), prop -> prop.getValue().toString().trim())));
            props.putAll(session.getUserProperties().entrySet().stream()
                    .collect(Collectors.toMap(prop ->
                            prop.getKey().toString().toLowerCase().trim(), prop -> prop.getValue().toString().trim())));
            // Support gitlab ci runner by chart.
            props.putAll(props.entrySet().stream()
                    .filter(prop -> prop.getKey().startsWith("env."))
                    .collect(Collectors.toMap(prop -> prop.getKey().substring("env.".length()), Map.Entry::getValue)));
            return props;
        }

        /**
         * 获取格式化后的Maven属性值.
         * <p>
         * 此方法从Maven属性获取对应标标识的值，标识会被解析成
         * 标准标识
         * 替换 '.'为 '_' 的标识
         * 去掉 'dew.devops.' 前缀的标识
         * 替换 '.'为 '_' 并去掉 'dew_devops_' 前缀的标识
         *
         * @param standardFlag        标准标识
         * @param formattedProperties Maven属性
         * @return 格式化后的Maven属性值 optional
         */
        public static Optional<String> formatParameters(String standardFlag, Map<String, String> formattedProperties) {
            standardFlag = standardFlag.toLowerCase();
            if (formattedProperties.containsKey(standardFlag)) {
                return Optional.of(formattedProperties.get(standardFlag));
            }
            String underlineFlag = standardFlag.replaceAll("\\.", "_");
            if (formattedProperties.containsKey(underlineFlag)) {
                return Optional.of(formattedProperties.get(underlineFlag));
            }
            String shortStandardFlag = standardFlag.substring("dew.devops.".length());
            if (formattedProperties.containsKey(shortStandardFlag)) {
                return Optional.of(formattedProperties.get(shortStandardFlag));
            }
            String shortUnderlineFlag = underlineFlag.substring("dew_devops_".length());
            if (formattedProperties.containsKey(shortUnderlineFlag)) {
                return Optional.of(formattedProperties.get(shortUnderlineFlag));
            }
            return Optional.empty();
        }

    }

    /**
     * 初始化.
     */
    public static class Init {

        private static final AtomicBoolean initializing = new AtomicBoolean(false);

        /**
         * Init.
         *
         * @param session                     the maven session
         * @param pluginManager               the plugin manager
         * @param inputProfile                the input input profile
         * @param inputDockerHost             the input docker host
         * @param inputDockerRegistryUrl      the input docker registry url
         * @param inputDockerRegistryUserName the input docker registry user name
         * @param inputDockerRegistryPassword the input docker registry password
         * @param inputKubeBase64Config       the input kube base 64 config
         * @param mockClasspath               the input mock classpath， 仅用于Mock
         * @throws IllegalAccessException    the illegal access exception
         * @throws IOException               the io exception
         * @throws InvocationTargetException the invocation target exception
         */
        public static void init(MavenSession session, BuildPluginManager pluginManager,
                                String inputProfile,
                                String inputDockerHost, String inputDockerRegistryUrl,
                                String inputDockerRegistryUserName, String inputDockerRegistryPassword,
                                String inputKubeBase64Config, String mockClasspath)
                throws IllegalAccessException, IOException, InvocationTargetException {
            Dew.mavenSession = session;
            Dew.mavenPluginManager = pluginManager;
            if (inputProfile == null) {
                inputProfile = Constants.FLAG_DEW_DEVOPS_DEFAULT_PROFILE;
            }
            inputProfile = inputProfile.toLowerCase();
            log.info("Active profile : " + inputProfile);
            // 全局只初始化一次
            if (!initializing.getAndSet(true)) {
                GitHelper.init(log);
                YamlHelper.init(log);
                initFinalConfig(inputProfile,
                        inputDockerHost, inputDockerRegistryUrl, inputDockerRegistryUserName, inputDockerRegistryPassword,
                        inputKubeBase64Config);
                Config.getProjects().values().forEach(config -> {
                    DockerHelper.init(config.getId(), log,
                            config.getDocker().getHost(),
                            config.getDocker().getRegistryUrl(),
                            config.getDocker().getRegistryUserName(),
                            config.getDocker().getRegistryPassword());
                    KubeHelper.init(config.getId(), log,
                            config.getKube().getBase64Config());
                    if (config.getAppendProfile() != null) {
                        // 初始化附加环境，多用于版本重用模式
                        DockerHelper.init(config.getId() + "-append", log,
                                config.getAppendProfile().getDocker().getHost(),
                                config.getAppendProfile().getDocker().getRegistryUrl(),
                                config.getAppendProfile().getDocker().getRegistryUserName(),
                                config.getAppendProfile().getDocker().getRegistryPassword());
                        KubeHelper.init(config.getId() + "-append", log,
                                config.getAppendProfile().getKube().getBase64Config());
                    }
                });
                initNotify();
                initMock(mockClasspath);
                shutdownHook();
                initialized = true;
            }
        }


        /**
         * Init final config.
         *
         * @param inputProfile                the input profile
         * @param inputDockerHost             the input docker host
         * @param inputDockerRegistryUrl      the input docker registry url
         * @param inputDockerRegistryUserName the input docker registry user name
         * @param inputDockerRegistryPassword the input docker registry password
         * @param inputKubeBase64Config       the input kube base 64 config
         * @throws IOException               the io exception
         * @throws InvocationTargetException the invocation target exception
         * @throws IllegalAccessException    the illegal access exception
         */
        private static void initFinalConfig(String inputProfile,
                                            String inputDockerHost, String inputDockerRegistryUrl,
                                            String inputDockerRegistryUserName, String inputDockerRegistryPassword,
                                            String inputKubeBase64Config)
                throws IOException, InvocationTargetException, IllegalAccessException {
            String basicDirectory = mavenSession.getTopLevelProject().getBasedir().getPath() + File.separator;
            // 基础配置
            String basicConfig = "";
            if (new File(basicDirectory + ".dew").exists()) {
                basicConfig = ConfigBuilder.mergeProfiles($.file.readAllByPathName(basicDirectory + ".dew", "UTF-8")) + "\r\n";
                DewConfig dewConfig = YamlHelper.toObject(DewConfig.class, basicConfig);
                if (inputProfile.equalsIgnoreCase(Dew.Constants.FLAG_DEW_DEVOPS_DEFAULT_PROFILE)) {
                    dewConfig.getProfiles().clear();
                    Config.basicProfileConfig = dewConfig;
                } else {
                    Config.basicProfileConfig = dewConfig.getProfiles().get(inputProfile);
                }
            }
            for (MavenProject project : mavenSession.getProjects()) {
                String projectDirectory = project.getBasedir().getPath() + File.separator;
                // 每个项目自定义的配置
                String projectConfig;
                if (!basicDirectory.equals(projectDirectory) && new File(projectDirectory + ".dew").exists()) {
                    // 合并基础配置与项目自定义配置
                    projectConfig = ConfigBuilder.mergeProject(basicConfig, $.file.readAllByPathName(projectDirectory + ".dew", "UTF-8"));
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
                Optional<FinalProjectConfig> finalProjectConfigOpt = ConfigBuilder.buildProject(dewConfig, project, inputProfile,
                        inputDockerHost, inputDockerRegistryUrl, inputDockerRegistryUserName, inputDockerRegistryPassword,
                        inputKubeBase64Config);
                if (finalProjectConfigOpt.isPresent()) {
                    Config.config.getProjects().put(project.getId(), finalProjectConfigOpt.get());
                    log.debug("[" + project.getGroupId() + ":" + project.getArtifactId() + "] configured");
                } else {
                    log.debug("[" + project.getGroupId() + ":" + project.getArtifactId() + "] skipped");
                }
            }
        }

        /**
         * Init notify.
         */
        private static void initNotify() {
            Map<String, NotifyConfig> configMap = new HashMap<>();
            Config.getProjects().entrySet().stream()
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
            log.warn("Discover mock configuration, mock class path is " + mockClasspath);
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
                        && !Dew.Config.getCurrentProject().isHasError()
                        && Dew.Config.getCurrentProject().getSkipReason().isEmpty()) {
                    Dew.Config.getCurrentProject().skip("Uncaught error", true);
                }
                ExecuteEventProcessor.onShutdown(Config.getProjects());
            });
        }

    }

    /**
     * 配置输出.
     */
    public static class Config {

        // e.g. maven.test.skip
        private static final Map<String, Map<String, String>> mavenProps = new HashMap<>();

        // 基础配置，全局 .dew 配置对应的当前Profile
        private static DewProfile basicProfileConfig;

        // 最终的配置
        private static FinalConfig config = new FinalConfig();

        // 当前项目的配置
        private static FinalProjectConfig currentProjectConfig = new FinalProjectConfig();

        /**
         * 获取当前项目配置.
         *
         * @return the current project
         */
        public static FinalProjectConfig getCurrentProject() {
            return currentProjectConfig;
        }

        /**
         * 获取当前Maven项目对象.
         *
         * @return the current maven project
         */
        public static MavenProject getMavenProject(String mavenId) {
            return Dew.mavenSession.getProjects().stream()
                    .filter(project -> project.getId().equalsIgnoreCase(mavenId)).findAny().get();
        }

        /**
         * 初始化当前Maven项目.
         */
        public static void initCurrentMavenProject() {
            if (!mavenProps.containsKey(mavenSession.getCurrentProject().getId())) {
                mavenProps.put(mavenSession.getCurrentProject().getId(), new HashMap<>());
            }
            mavenSession.getCurrentProject().getProperties().putAll(mavenProps.get(mavenSession.getCurrentProject().getId()));
            // 更新当前Maven项目指向，用于shutdown hook时获取出错的项目（如果存在的话）
            currentProjectConfig = config.getProjects().get(mavenSession.getCurrentProject().getId());
        }

        /**
         * 设置当前Maven的属性.
         *
         * @param key   属性名
         * @param value 属性值
         */
        public static void setCurrentMavenProperty(String key, String value) {
            setMavenProperty(mavenSession.getCurrentProject().getId(), key, value);
        }

        /**
         * 设置Maven的属性.
         *
         * @param mavenId Maven项目Id
         * @param key     属性名
         * @param value   属性值
         */
        public static void setMavenProperty(String mavenId, String key, String value) {
            if (!mavenProps.containsKey(mavenId)) {
                mavenProps.put(mavenId, new HashMap<>());
            }
            mavenProps.get(mavenId).put(key, value);
            getMavenProject(mavenId).getProperties().putAll(mavenProps.get(mavenSession.getCurrentProject().getId()));
        }

        /**
         * 获取所有项目配置.
         *
         * @return the project configs
         */
        public static Map<String, FinalProjectConfig> getProjects() {
            return config.getProjects();
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
         */
        public static void invoke(String groupId, String artifactId, String version, String goal, Map<String, String> configuration) {
            log.debug("invoke groupId = " + groupId + " ,artifactId = " + artifactId + " ,version = " + version);
            List<Element> config = configuration.entrySet().stream()
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
                                mavenSession.getCurrentProject(),
                                mavenSession,
                                mavenPluginManager
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
                log.debug("Not found mock class path in " + classPath);
                return;
            }
            Optional<File> mockFiles = Stream.of(clazzPath.listFiles()).filter(f -> f.getName().equals("mock") && f.isDirectory()).findAny();
            if (!mockFiles.isPresent()) {
                log.debug("Mock class path must contain a directory named 'mock'");
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
                log.debug("Loading class " + file.getName());
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
