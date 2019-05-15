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

package ms.dew.devops.maven.mojo;

import io.kubernetes.client.ApiException;
import ms.dew.devops.kernel.DevOps;
import ms.dew.devops.kernel.config.ConfigBuilder;
import ms.dew.devops.kernel.config.FinalProjectConfig;
import ms.dew.devops.kernel.exception.ConfigException;
import ms.dew.devops.kernel.exception.GlobalProcessException;
import ms.dew.devops.kernel.exception.ProjectProcessException;
import ms.dew.devops.kernel.function.ExecuteEventProcessor;
import ms.dew.devops.kernel.util.DewLog;
import ms.dew.devops.maven.MavenDevOps;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.BuildPluginManager;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.slf4j.Logger;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Basic mojo.
 *
 * @author gudaoxuri
 */
public abstract class BasicMojo extends AbstractMojo {

    // ========================== 常量定义 ==========================

    // ============= 公共场景使用 =============
    /**
     * 环境标识.
     */
    private static final String FLAG_DEW_DEVOPS_PROFILE = "dew.devops.profile";
    /**
     * Kubernetes Base64 配置标识.
     */
    private static final String FLAG_DEW_DEVOPS_KUBE_CONFIG = "dew.devops.kube.config";
    /**
     * 指定发布项目名称.
     */
    private static final String FLAG_DEW_DEVOPS_ASSIGNATION_PROJECTS = "dew.devops.assignation.projects";

    // ============= 发布与回滚使用 =============

    /**
     * Docker Host标识.
     * <p>
     * e.g. tcp://10.200.131.182:2375.
     */
    private static final String FLAG_DEW_DEVOPS_DOCKER_HOST = "dew.devops.docker.host";
    /**
     * Docker registry url标识.
     * <p>
     * e.g. https://harbor.dew.env/v2
     */
    private static final String FLAG_DEW_DEVOPS_DOCKER_REGISTRY_URL = "dew.devops.docker.registry.url";
    /**
     * Docker registry 用户名标识.
     */
    private static final String FLAG_DEW_DEVOPS_DOCKER_REGISTRY_USERNAME = "dew.devops.docker.registry.username";
    /**
     * Docker registry 密码标识.
     */
    private static final String FLAG_DEW_DEVOPS_DOCKER_REGISTRY_PASSWORD = "dew.devops.docker.registry.password";
    /**
     * 是否静默处理标识.
     * <p>
     * 仅对发布/回滚有效
     */
    private static final String FLAG_DEW_DEVOPS_QUIET = "dew.devops.quiet";

    // ============= 日志及调试场景使用 =============
    /**
     * 要使用的Pod名称标识.
     */
    private static final String FLAG_DEW_DEVOPS_POD_NAME = "dew.devops.podName";
    // ============= 日志场景使用 =============
    /**
     * 是否滚动查看日志标识.
     */
    private static final String FLAG_DEW_DEVOPS_LOG_FOLLOW = "dew.logger.follow";
    // ============= 调试场景使用 =============
    /**
     * 转发端口标识.
     */
    private static final String FLAG_DEW_DEVOPS_DEBUG_FORWARD_PORT = "dew.devops.debug.forward.port";

    // ============= 伸缩场景使用 =============

    /**
     * 伸缩Pod数量标识.
     */
    private static final String FLAG_DEW_DEVOPS_SCALE_REPLICAS = "dew.devops.scale.replicas";
    /**
     * 是否启用自动伸缩标识.
     */
    private static final String FLAG_DEW_DEVOPS_SCALE_AUTO = "dew.devops.scale.auto";
    /**
     * 自动伸缩Pod数下限标识.
     */
    private static final String FLAG_DEW_DEVOPS_SCALE_AUTO_REPLICAS_MIN = "dew.devops.scale.auto.minReplicas";
    /**
     * 自动伸缩Pod数上限标识.
     */
    private static final String FLAG_DEW_DEVOPS_SCALE_AUTO_REPLICAS_MAX = "dew.devops.scale.auto.maxReplicas";
    /**
     * 自动伸缩条件：CPU平均使用率标识.
     */
    private static final String FLAG_DEW_DEVOPS_SCALE_AUTO_CPU_AVG = "dew.devops.scale.auto.cpu.averageUtilization";

    // ============= 测试场景使用 =============
    /**
     * Mock场景下加载外部class的路径标识.
     */
    private static final String FLAG_DEW_DEVOPS_MOCK_CLASS_PATH = "dew.devops.mock.classpath";


    // ========================== 接收参数 ==========================

    // ============= 公共场景使用 =============

    /**
     * The Profile.
     */
    @Parameter(property = FLAG_DEW_DEVOPS_PROFILE, defaultValue = ConfigBuilder.FLAG_DEW_DEVOPS_DEFAULT_PROFILE)
    private String profile;

    /**
     * The Kubernetes base64 config.
     */
    @Parameter(property = FLAG_DEW_DEVOPS_KUBE_CONFIG)
    private String kubeBase64Config;


    /**
     * Assign deploy projects.
     */
    @Parameter(property = FLAG_DEW_DEVOPS_ASSIGNATION_PROJECTS)
    private String assignationProjects;

    // ============= 发布与回滚使用 =============
    /**
     * The Docker host.
     */
    @Parameter(property = FLAG_DEW_DEVOPS_DOCKER_HOST)
    private String dockerHost;

    /**
     * The Docker registry url.
     */
    @Parameter(property = FLAG_DEW_DEVOPS_DOCKER_REGISTRY_URL)
    private String dockerRegistryUrl;

    /**
     * The Docker registry user name.
     */
    @Parameter(property = FLAG_DEW_DEVOPS_DOCKER_REGISTRY_USERNAME)
    private String dockerRegistryUserName;

    /**
     * The Docker registry password.
     */
    @Parameter(property = FLAG_DEW_DEVOPS_DOCKER_REGISTRY_PASSWORD)
    private String dockerRegistryPassword;

    /**
     * The Quiet.
     */
    @Parameter(property = FLAG_DEW_DEVOPS_QUIET)
    boolean quiet;

    // ============= 日志及调试场景使用 =============
    /**
     * The Pod name.
     */
    @Parameter(property = FLAG_DEW_DEVOPS_POD_NAME)
    String podName;
    // ============= 日志场景使用 =============
    /**
     * The Follow.
     */
    @Parameter(property = FLAG_DEW_DEVOPS_LOG_FOLLOW)
    boolean follow;
    // ============= 调试场景使用 =============
    /**
     * The Forward Port.
     */
    @Parameter(property = FLAG_DEW_DEVOPS_DEBUG_FORWARD_PORT, defaultValue = "9999")
    int forwardPort;

    // ============= 伸缩场景使用 =============
    /**
     * The Replicas.
     */
    @Parameter(property = FLAG_DEW_DEVOPS_SCALE_REPLICAS)
    protected int replicas;

    /**
     * The Auto scale.
     */
    @Parameter(property = FLAG_DEW_DEVOPS_SCALE_AUTO)
    boolean autoScale;

    /**
     * The Min replicas.
     */
    @Parameter(property = FLAG_DEW_DEVOPS_SCALE_AUTO_REPLICAS_MIN)
    int minReplicas;

    /**
     * The Max replicas.
     */
    @Parameter(property = FLAG_DEW_DEVOPS_SCALE_AUTO_REPLICAS_MAX)
    int maxReplicas;

    /**
     * The Cpu avg.
     */
    @Parameter(property = FLAG_DEW_DEVOPS_SCALE_AUTO_CPU_AVG)
    int cpuAvg;


    // ============= 测试场景使用 =============
    @Parameter(property = FLAG_DEW_DEVOPS_MOCK_CLASS_PATH)
    private String mockClasspath;

    // ============= 其它参数 =============

    @Parameter(defaultValue = "${session}", readonly = true)
    protected MavenSession mavenSession;

    @Parameter(defaultValue = "${project}", readonly = true)
    protected MavenProject mavenProject;

    @Component
    private BuildPluginManager pluginManager;

    protected Logger logger = null;

    /**
     * Execute internal.
     *
     * @return <b>true</b> if success
     * @throws IOException  the io exception
     * @throws ApiException the api exception
     */
    protected abstract boolean executeInternal() throws IOException, ApiException;

    @Override
    public void execute() {
        logger = DewLog.build(this.getClass(), mavenProject.getName(), getMojoName());
        if (DevOps.stopped) {
            return;
        }
        Map<String, String> formattedProperties = getMavenProperties(mavenSession);
        formatParameters(mavenSession, formattedProperties);
        Optional<String> dockerHostAppendOpt =
                formatParameters(FLAG_DEW_DEVOPS_DOCKER_HOST + "-append", formattedProperties);
        Optional<String> dockerRegistryUrlAppendOpt =
                formatParameters(FLAG_DEW_DEVOPS_DOCKER_REGISTRY_URL + "-append", formattedProperties);
        Optional<String> dockerRegistryUserNameAppendOpt =
                formatParameters(FLAG_DEW_DEVOPS_DOCKER_REGISTRY_USERNAME + "-append", formattedProperties);
        Optional<String> dockerRegistryPasswordAppendOpt =
                formatParameters(FLAG_DEW_DEVOPS_DOCKER_REGISTRY_PASSWORD + "-append", formattedProperties);
        Optional<String> kubeBase64ConfigAppendOpt =
                formatParameters(FLAG_DEW_DEVOPS_KUBE_CONFIG + "-append", formattedProperties);
        try {
            MavenDevOps.Init.init(mavenSession, pluginManager, profile,
                    dockerHost, dockerRegistryUrl, dockerRegistryUserName, dockerRegistryPassword, kubeBase64Config, assignationProjects,
                    dockerHostAppendOpt, dockerRegistryUrlAppendOpt, dockerRegistryUserNameAppendOpt, dockerRegistryPasswordAppendOpt,
                    kubeBase64ConfigAppendOpt,
                    mockClasspath);
        } catch (ConfigException e) {
            // 此错误会中止程序
            logger.error("Init Process error", e);
            e.printStackTrace();
            ExecuteEventProcessor.onGlobalProcessError(e);
            throw e;
        }
        FinalProjectConfig projectConfig = DevOps.Config.getProjectConfig(mavenSession.getCurrentProject().getId());
        try {
            if (projectConfig == null) {
                // 这多半是正常的行为
                logger.info("The current project kind does not match");
                return;
            }
            if (projectConfig.getSkip()) {
                // 这多半是正常的行为
                disabledDefaultBehavior();
                logger.info("The current project is manually set to skip");
                return;
            }
            if (executeInternal()) {
                logger.info("Successful");
                ExecuteEventProcessor.onMojoExecuteSuccessful(getMojoName(), projectConfig, "");
            } else {
                // 此错误不会中止程序
                disabledDefaultBehavior();
                projectConfig.skip("Internal execution error", true);
            }
        } catch (GlobalProcessException e) {
            // 此错误会中止程序
            logger.error("Global Process error", e);
            e.printStackTrace();
            ExecuteEventProcessor.onGlobalProcessError(e);
            throw e;
        } catch (Exception e) {
            // 此错误会中止程序
            logger.error("Process error", e);
            e.printStackTrace();
            assert projectConfig != null;
            projectConfig.skip("Process error [" + e.getClass().getSimpleName() + "]" + e.getMessage(), true);
            ExecuteEventProcessor.onMojoExecuteFailure(getMojoName(), projectConfig, e);
            throw new ProjectProcessException("Process error", e);
        }
    }

    private void formatParameters(MavenSession session, Map<String, String> formattedProperties) {
        formatParameters(FLAG_DEW_DEVOPS_PROFILE, formattedProperties)
                .ifPresent(obj -> profile = obj);
        formatParameters(FLAG_DEW_DEVOPS_KUBE_CONFIG, formattedProperties)
                .ifPresent(obj -> kubeBase64Config = obj);
        formatParameters(FLAG_DEW_DEVOPS_ASSIGNATION_PROJECTS, formattedProperties)
                .ifPresent(obj -> assignationProjects = obj);
        formatParameters(FLAG_DEW_DEVOPS_DOCKER_HOST, formattedProperties)
                .ifPresent(obj -> dockerHost = obj);
        formatParameters(FLAG_DEW_DEVOPS_DOCKER_REGISTRY_URL, formattedProperties)
                .ifPresent(obj -> dockerRegistryUrl = obj);
        formatParameters(FLAG_DEW_DEVOPS_DOCKER_REGISTRY_USERNAME, formattedProperties)
                .ifPresent(obj -> dockerRegistryUserName = obj);
        formatParameters(FLAG_DEW_DEVOPS_DOCKER_REGISTRY_PASSWORD, formattedProperties)
                .ifPresent(obj -> dockerRegistryPassword = obj);
        formatParameters(FLAG_DEW_DEVOPS_QUIET, formattedProperties)
                .ifPresent(obj -> quiet = Boolean.valueOf(obj));
        formatParameters(FLAG_DEW_DEVOPS_POD_NAME, formattedProperties)
                .ifPresent(obj -> podName = obj);
        formatParameters(FLAG_DEW_DEVOPS_LOG_FOLLOW, formattedProperties)
                .ifPresent(obj -> follow = Boolean.valueOf(obj));
        formatParameters(FLAG_DEW_DEVOPS_DEBUG_FORWARD_PORT, formattedProperties)
                .ifPresent(obj -> forwardPort = Integer.valueOf(obj));
        formatParameters(FLAG_DEW_DEVOPS_SCALE_REPLICAS, formattedProperties)
                .ifPresent(obj -> replicas = Integer.valueOf(obj));
        formatParameters(FLAG_DEW_DEVOPS_SCALE_AUTO, formattedProperties)
                .ifPresent(obj -> autoScale = Boolean.valueOf(obj));
        formatParameters(FLAG_DEW_DEVOPS_SCALE_AUTO_REPLICAS_MIN, formattedProperties)
                .ifPresent(obj -> minReplicas = Integer.valueOf(obj));
        formatParameters(FLAG_DEW_DEVOPS_SCALE_AUTO_REPLICAS_MAX, formattedProperties)
                .ifPresent(obj -> maxReplicas = Integer.valueOf(obj));
        formatParameters(FLAG_DEW_DEVOPS_SCALE_AUTO_CPU_AVG, formattedProperties)
                .ifPresent(obj -> cpuAvg = Integer.valueOf(obj));
        formatParameters(FLAG_DEW_DEVOPS_MOCK_CLASS_PATH, formattedProperties)
                .ifPresent(obj -> mockClasspath = obj);
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
    private static Optional<String> formatParameters(String standardFlag, Map<String, String> formattedProperties) {
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

    /**
     * 获取Maven属性.
     *
     * @param session maven mavenSession
     * @return properties maven properties
     */
    private static Map<String, String> getMavenProperties(MavenSession session) {
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
     * Gets mojo name.
     *
     * @return the mojo name
     */
    String getMojoName() {
        return this.getClass().getSimpleName().substring(0, this.getClass().getSimpleName().indexOf("Mojo")).toLowerCase();
    }

    private void disabledDefaultBehavior() {
        MavenDevOps.Config.setMavenProperty(mavenSession.getCurrentProject().getId(), "maven.test.skip", "true");
        MavenDevOps.Config.setMavenProperty(mavenSession.getCurrentProject().getId(), "maven.install.skip", "true");
        MavenDevOps.Config.setMavenProperty(mavenSession.getCurrentProject().getId(), "maven.deploy.skip", "true");
    }


}
