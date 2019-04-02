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

package ms.dew.devops.mojo;

import io.kubernetes.client.ApiException;
import ms.dew.devops.kernel.Dew;
import ms.dew.devops.util.DewLog;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.BuildPluginManager;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.Parameter;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public abstract class BasicMojo extends AbstractMojo {

    public static final String FLAG_DEW_DEVOPS_DEFAULT_PROFILE = "default";

    // ============= 公共场景使用 =============
    private static final String FLAG_DEW_DEVOPS_PROFILE = "dew.devops.profile";
    private static final String FLAG_DEW_DEVOPS_KUBE_CONFIG = "dew.devops.kube.config";

    @Parameter(property = FLAG_DEW_DEVOPS_PROFILE, defaultValue = FLAG_DEW_DEVOPS_DEFAULT_PROFILE)
    private String profile;

    @Parameter(property = FLAG_DEW_DEVOPS_KUBE_CONFIG)
    private String kubeBase64Config;

    // ============= 发布与回滚使用 =============
    private static final String FLAG_DEW_DEVOPS_DOCKER_HOST = "dew.devops.docker.host";
    private static final String FLAG_DEW_DEVOPS_DOCKER_REGISTRY_URL = "dew.devops.docker.registry.url";
    private static final String FLAG_DEW_DEVOPS_DOCKER_REGISTRY_USERNAME = "dew.devops.docker.registry.username";
    private static final String FLAG_DEW_DEVOPS_DOCKER_REGISTRY_PASSWORD = "dew.devops.docker.registry.password";
    private static final String FLAG_DEW_DEVOPS_QUIET = "dew.devops.quiet";
    private static final String FLAG_DEW_DEVOPS_VERSION_CUST = "dew.devops.version.custom";

    @Parameter(property = FLAG_DEW_DEVOPS_DOCKER_HOST)
    private String dockerHost;

    @Parameter(property = FLAG_DEW_DEVOPS_DOCKER_REGISTRY_URL)
    private String dockerRegistryUrl;

    @Parameter(property = FLAG_DEW_DEVOPS_DOCKER_REGISTRY_USERNAME)
    private String dockerRegistryUserName;

    @Parameter(property = FLAG_DEW_DEVOPS_DOCKER_REGISTRY_PASSWORD)
    private String dockerRegistryPassword;

    @Parameter(property = FLAG_DEW_DEVOPS_QUIET)
    protected boolean quiet;

    @Parameter(property = FLAG_DEW_DEVOPS_VERSION_CUST)
    private String customVersion;

    // ============= 日志场景使用 =============
    private static final String FLAG_DEW_DEVOPS_POD_NAME = "dew.devops.log.podName";
    private static final String FLAG_DEW_DEVOPS_LOG_FOLLOW = "dew.devops.log.follow";

    @Parameter(property = FLAG_DEW_DEVOPS_POD_NAME)
    protected String podName;
    @Parameter(property = FLAG_DEW_DEVOPS_LOG_FOLLOW)
    protected boolean follow;

    // ============= 伸缩场景使用 =============
    private static final String FLAG_DEW_DEVOPS_SCALE_REPLICAS = "dew.devops.scale.replicas";
    private static final String FLAG_DEW_DEVOPS_SCALE_AUTO = "dew.devops.scale.auto";
    private static final String FLAG_DEW_DEVOPS_SCALE_AUTO_REPLICAS_MIN = "dew.devops.scale.auto.minReplicas";
    private static final String FLAG_DEW_DEVOPS_SCALE_AUTO_REPLICAS_MAX = "dew.devops.scale.auto.maxReplicas";
    private static final String FLAG_DEW_DEVOPS_SCALE_AUTO_CPU_AVG = "dew.devops.scale.auto.cpu.averageUtilization";
    private static final String FLAG_DEW_DEVOPS_SCALE_AUTO_TPS = "dew.devops.scale.auto.cpu.tps";

    @Parameter(property = FLAG_DEW_DEVOPS_SCALE_REPLICAS)
    protected int replicas;

    @Parameter(property = FLAG_DEW_DEVOPS_SCALE_AUTO)
    protected boolean autoScale;

    @Parameter(property = FLAG_DEW_DEVOPS_SCALE_AUTO_REPLICAS_MIN)
    protected int minReplicas;

    @Parameter(property = FLAG_DEW_DEVOPS_SCALE_AUTO_REPLICAS_MAX)
    protected int maxReplicas;

    @Parameter(property = FLAG_DEW_DEVOPS_SCALE_AUTO_CPU_AVG)
    protected int cpuAvg;

    @Parameter(property = FLAG_DEW_DEVOPS_SCALE_AUTO_TPS)
    protected long tps;

    // ============= 测试场景使用 =============
    private static final String FLAG_DEW_DEVOPS_MOCK_CLASS_PATH = "dew.devops.mock.classpath";

    @Parameter(property = FLAG_DEW_DEVOPS_MOCK_CLASS_PATH)
    private String mockClasspath;

    // ============= 其它参数 =============

    @Component
    private MavenSession session;

    @Component
    private BuildPluginManager pluginManager;

    protected boolean preExecute() throws MojoExecutionException, MojoFailureException, IOException, ApiException {
        return true;
    }

    protected abstract boolean executeInternal() throws MojoExecutionException, MojoFailureException, IOException, ApiException;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        if (Dew.stopped) {
            return;
        }
        try {
            Dew.log = new DewLog(super.getLog(), "[DEW][" + getMojoName() + "]:");
            formatParameters();
            Dew.log.info("Start...");
            Dew.Init.init(session, pluginManager, profile,
                    dockerHost, dockerRegistryUrl, dockerRegistryUserName, dockerRegistryPassword, kubeBase64Config,
                    customVersion, mockClasspath);
            if (!preExecute() || Dew.Config.getCurrentProject() == null || Dew.Config.getCurrentProject().isSkip()) {
                // 各项目 .dew 配置 skip=true || 不支持的app kind
                Dew.log.info("Skipped");
                return;
            }
            if (Dew.stopped) {
                return;
            }
            if (executeInternal()) {
                Dew.log.info("Successful");
                Dew.Notify.success("Successful", getMojoName());
            } else {
                Dew.Config.getCurrentProject().setSkip(true);
            }
        } catch (MojoExecutionException | MojoFailureException e) {
            Dew.log.error("Error", e);
            Dew.Notify.fail(e, getMojoName());
            throw e;
        } catch (Exception e) {
            Dew.log.error("Error", e);
            Dew.Notify.fail(e, getMojoName());
            throw new MojoFailureException(e.getMessage());
        }
    }

    private void formatParameters() {
        Dew.log.info("Parsing parameters with standard underline and short");
        Map<String, Object> formattedProperties = formatProperties();
        formatParameters(FLAG_DEW_DEVOPS_PROFILE, formattedProperties).ifPresent(obj -> profile = (String) obj);
        formatParameters(FLAG_DEW_DEVOPS_KUBE_CONFIG, formattedProperties).ifPresent(obj -> kubeBase64Config = (String) obj);
        formatParameters(FLAG_DEW_DEVOPS_DOCKER_HOST, formattedProperties).ifPresent(obj -> dockerHost = (String) obj);
        formatParameters(FLAG_DEW_DEVOPS_DOCKER_REGISTRY_URL, formattedProperties).ifPresent(obj -> dockerRegistryUrl = (String) obj);
        formatParameters(FLAG_DEW_DEVOPS_DOCKER_REGISTRY_USERNAME, formattedProperties).ifPresent(obj -> dockerRegistryUserName = (String) obj);
        formatParameters(FLAG_DEW_DEVOPS_DOCKER_REGISTRY_PASSWORD, formattedProperties).ifPresent(obj -> dockerRegistryPassword = (String) obj);
        formatParameters(FLAG_DEW_DEVOPS_QUIET, formattedProperties).ifPresent(obj -> quiet = (Boolean) obj);
        formatParameters(FLAG_DEW_DEVOPS_VERSION_CUST, formattedProperties).ifPresent(obj -> customVersion = (String) obj);
        formatParameters(FLAG_DEW_DEVOPS_POD_NAME, formattedProperties).ifPresent(obj -> podName = (String) obj);
        formatParameters(FLAG_DEW_DEVOPS_LOG_FOLLOW, formattedProperties).ifPresent(obj -> follow = (Boolean) obj);
        formatParameters(FLAG_DEW_DEVOPS_SCALE_REPLICAS, formattedProperties).ifPresent(obj -> replicas = (Integer) obj);
        formatParameters(FLAG_DEW_DEVOPS_SCALE_AUTO, formattedProperties).ifPresent(obj -> autoScale = (Boolean) obj);
        formatParameters(FLAG_DEW_DEVOPS_SCALE_AUTO_REPLICAS_MIN, formattedProperties).ifPresent(obj -> minReplicas = (Integer) obj);
        formatParameters(FLAG_DEW_DEVOPS_SCALE_AUTO_REPLICAS_MAX, formattedProperties).ifPresent(obj -> maxReplicas = (Integer) obj);
        formatParameters(FLAG_DEW_DEVOPS_SCALE_AUTO_CPU_AVG, formattedProperties).ifPresent(obj -> cpuAvg = (Integer) obj);
        formatParameters(FLAG_DEW_DEVOPS_SCALE_AUTO_TPS, formattedProperties).ifPresent(obj -> tps = (Long) obj);
        formatParameters(FLAG_DEW_DEVOPS_MOCK_CLASS_PATH, formattedProperties).ifPresent(obj -> mockClasspath = (String) obj);
    }

    private Optional<Object> formatParameters(String standardFlag, Map<String, Object> formattedProperties) {
        standardFlag = standardFlag.toLowerCase();
        String underlineFlag = standardFlag.replaceAll("\\.", "_");
        String shortStandardFlag = standardFlag.substring("dew.devops.".length());
        String shortUnderlineFlag = underlineFlag.substring("dew_devops_".length());
        if (formattedProperties.containsKey(standardFlag)) {
            return Optional.of(formattedProperties.get(standardFlag));
        }
        if (formattedProperties.containsKey(underlineFlag)) {
            return Optional.of(formattedProperties.get(underlineFlag));
        }
        if (formattedProperties.containsKey(shortStandardFlag)) {
            return Optional.of(formattedProperties.get(shortStandardFlag));
        }
        if (formattedProperties.containsKey(shortUnderlineFlag)) {
            return Optional.of(formattedProperties.get(shortUnderlineFlag));
        }
        return Optional.empty();
    }

    private Map<String, Object> formatProperties() {
        return System.getProperties().entrySet().stream()
                .collect(Collectors.toMap(prop -> prop.getKey().toString().toLowerCase(), Map.Entry::getValue));
    }

    protected String getMojoName() {
        return this.getClass().getSimpleName().substring(0, this.getClass().getSimpleName().indexOf("Mojo")).toLowerCase();
    }

    @Override
    public Log getLog() {
        return Dew.log;
    }

}