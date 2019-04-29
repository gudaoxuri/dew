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
import ms.dew.devops.exception.ProcessException;
import ms.dew.devops.kernel.Dew;
import ms.dew.devops.kernel.function.ExecuteEventProcessor;
import ms.dew.devops.util.DewLog;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.BuildPluginManager;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.Parameter;

import java.io.IOException;
import java.util.Map;

/**
 * Basic mojo.
 *
 * @author gudaoxuri
 */
public abstract class BasicMojo extends AbstractMojo {

    // ============= 公共场景使用 =============

    /**
     * The Profile.
     */
    @Parameter(property = Dew.Constants.FLAG_DEW_DEVOPS_PROFILE, defaultValue = Dew.Constants.FLAG_DEW_DEVOPS_DEFAULT_PROFILE)
    private String profile;

    /**
     * The Kubernetes base64 config.
     */
    @Parameter(property = Dew.Constants.FLAG_DEW_DEVOPS_KUBE_CONFIG)
    private String kubeBase64Config;

    // ============= 发布与回滚使用 =============
    /**
     * The Docker host.
     */
    @Parameter(property = Dew.Constants.FLAG_DEW_DEVOPS_DOCKER_HOST)
    private String dockerHost;

    /**
     * The Docker registry url.
     */
    @Parameter(property = Dew.Constants.FLAG_DEW_DEVOPS_DOCKER_REGISTRY_URL)
    private String dockerRegistryUrl;

    /**
     * The Docker registry user name.
     */
    @Parameter(property = Dew.Constants.FLAG_DEW_DEVOPS_DOCKER_REGISTRY_USERNAME)
    private String dockerRegistryUserName;

    /**
     * The Docker registry password.
     */
    @Parameter(property = Dew.Constants.FLAG_DEW_DEVOPS_DOCKER_REGISTRY_PASSWORD)
    private String dockerRegistryPassword;

    /**
     * The Quiet.
     */
    @Parameter(property = Dew.Constants.FLAG_DEW_DEVOPS_QUIET)
    boolean quiet;

    /**
     * The Custom version.
     */
    @Parameter(property = Dew.Constants.FLAG_DEW_DEVOPS_VERSION_CUST)
    private String customVersion;


    // ============= 日志及调试场景使用 =============
    /**
     * The Pod name.
     */
    @Parameter(property = Dew.Constants.FLAG_DEW_DEVOPS_POD_NAME)
    String podName;
    // ============= 日志场景使用 =============
    /**
     * The Follow.
     */
    @Parameter(property = Dew.Constants.FLAG_DEW_DEVOPS_LOG_FOLLOW)
    boolean follow;
    // ============= 调试场景使用 =============
    /**
     * The Forward Port.
     */
    @Parameter(property = Dew.Constants.FLAG_DEW_DEVOPS_DEBUG_FORWARD_PORT, defaultValue = "9999")
    int forwardPort;

    // ============= 伸缩场景使用 =============
    /**
     * The Replicas.
     */
    @Parameter(property = Dew.Constants.FLAG_DEW_DEVOPS_SCALE_REPLICAS)
    protected int replicas;

    /**
     * The Auto scale.
     */
    @Parameter(property = Dew.Constants.FLAG_DEW_DEVOPS_SCALE_AUTO)
    boolean autoScale;

    /**
     * The Min replicas.
     */
    @Parameter(property = Dew.Constants.FLAG_DEW_DEVOPS_SCALE_AUTO_REPLICAS_MIN)
    int minReplicas;

    /**
     * The Max replicas.
     */
    @Parameter(property = Dew.Constants.FLAG_DEW_DEVOPS_SCALE_AUTO_REPLICAS_MAX)
    int maxReplicas;

    /**
     * The Cpu avg.
     */
    @Parameter(property = Dew.Constants.FLAG_DEW_DEVOPS_SCALE_AUTO_CPU_AVG)
    int cpuAvg;


    // ============= 测试场景使用 =============
    @Parameter(property = Dew.Constants.FLAG_DEW_DEVOPS_MOCK_CLASS_PATH)
    private String mockClasspath;

    // ============= 其它参数 =============

    @Component
    private MavenSession session;

    @Component
    private BuildPluginManager pluginManager;

    /**
     * Pre execute.
     *
     * @return <b>true</b> if success
     * @throws IOException  the io exception
     * @throws ApiException the api exception
     */
    protected boolean preExecute() throws IOException, ApiException {
        return true;
    }

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
            if (Dew.Config.getCurrentProject() == null) {
                // 这多半是正常的行为
                Dew.log.info("The current project kind does not match");
                return;
            }
            if (Dew.Config.getCurrentProject().isSkip()) {
                // 这多半是正常的行为
                disabledDefaultBehavior();
                Dew.log.info("The current project is manually set to skip");
                return;
            }
            if (!preExecute()) {
                Dew.log.warn("Pre-execution error");
                disabledDefaultBehavior();
                Dew.Config.getCurrentProject().skip("Pre-execution error", true);
                return;
            }
            if (Dew.stopped) {
                return;
            }
            if (executeInternal()) {
                Dew.log.info("Successful");
                ExecuteEventProcessor.onMojoExecuteSuccessful(getMojoName(), Dew.Config.getCurrentProject(), "");
            } else {
                // 此错误不会中止程序
                disabledDefaultBehavior();
                Dew.Config.getCurrentProject().skip("Internal execution error", true);
            }
        } catch (Exception e) {
            // 此错误会中止程序
            Dew.log.error("Process error", e);
            Dew.Config.getCurrentProject().skip("Process error : " + e.getMessage(), true);
            ExecuteEventProcessor.onMojoExecuteFailure(getMojoName(), Dew.Config.getCurrentProject(), e);
            throw new ProcessException("Process error", e);
        }
    }

    private void disabledDefaultBehavior() {
        Dew.Config.getMavenProperties().setProperty("maven.test.skip", "true");
        Dew.Config.getMavenProperties().setProperty("maven.install.skip", "true");
        Dew.Config.getMavenProperties().setProperty("maven.deploy.skip", "true");
    }

    private void formatParameters() {
        Dew.log.info("Parsing parameters with standard underline and short");
        Map<String, String> formattedProperties = Dew.Constants.getMavenProperties(session);
        Dew.Constants.formatParameters(Dew.Constants.FLAG_DEW_DEVOPS_PROFILE, formattedProperties)
                .ifPresent(obj -> profile = obj);
        Dew.Constants.formatParameters(Dew.Constants.FLAG_DEW_DEVOPS_KUBE_CONFIG, formattedProperties)
                .ifPresent(obj -> kubeBase64Config = obj);
        Dew.Constants.formatParameters(Dew.Constants.FLAG_DEW_DEVOPS_DOCKER_HOST, formattedProperties)
                .ifPresent(obj -> dockerHost = obj);
        Dew.Constants.formatParameters(Dew.Constants.FLAG_DEW_DEVOPS_DOCKER_REGISTRY_URL, formattedProperties)
                .ifPresent(obj -> dockerRegistryUrl = obj);
        Dew.Constants.formatParameters(Dew.Constants.FLAG_DEW_DEVOPS_DOCKER_REGISTRY_USERNAME, formattedProperties)
                .ifPresent(obj -> dockerRegistryUserName = obj);
        Dew.Constants.formatParameters(Dew.Constants.FLAG_DEW_DEVOPS_DOCKER_REGISTRY_PASSWORD, formattedProperties)
                .ifPresent(obj -> dockerRegistryPassword = obj);
        Dew.Constants.formatParameters(Dew.Constants.FLAG_DEW_DEVOPS_QUIET, formattedProperties)
                .ifPresent(obj -> quiet = Boolean.valueOf(obj));
        Dew.Constants.formatParameters(Dew.Constants.FLAG_DEW_DEVOPS_VERSION_CUST, formattedProperties)
                .ifPresent(obj -> customVersion = obj);
        Dew.Constants.formatParameters(Dew.Constants.FLAG_DEW_DEVOPS_POD_NAME, formattedProperties)
                .ifPresent(obj -> podName = obj);
        Dew.Constants.formatParameters(Dew.Constants.FLAG_DEW_DEVOPS_LOG_FOLLOW, formattedProperties)
                .ifPresent(obj -> follow = Boolean.valueOf(obj));
        Dew.Constants.formatParameters(Dew.Constants.FLAG_DEW_DEVOPS_DEBUG_FORWARD_PORT, formattedProperties)
                .ifPresent(obj -> forwardPort = Integer.valueOf(obj));
        Dew.Constants.formatParameters(Dew.Constants.FLAG_DEW_DEVOPS_SCALE_REPLICAS, formattedProperties)
                .ifPresent(obj -> replicas = Integer.valueOf(obj));
        Dew.Constants.formatParameters(Dew.Constants.FLAG_DEW_DEVOPS_SCALE_AUTO, formattedProperties)
                .ifPresent(obj -> autoScale = Boolean.valueOf(obj));
        Dew.Constants.formatParameters(Dew.Constants.FLAG_DEW_DEVOPS_SCALE_AUTO_REPLICAS_MIN, formattedProperties)
                .ifPresent(obj -> minReplicas = Integer.valueOf(obj));
        Dew.Constants.formatParameters(Dew.Constants.FLAG_DEW_DEVOPS_SCALE_AUTO_REPLICAS_MAX, formattedProperties)
                .ifPresent(obj -> maxReplicas = Integer.valueOf(obj));
        Dew.Constants.formatParameters(Dew.Constants.FLAG_DEW_DEVOPS_SCALE_AUTO_CPU_AVG, formattedProperties)
                .ifPresent(obj -> cpuAvg = Integer.valueOf(obj));
        Dew.Constants.formatParameters(Dew.Constants.FLAG_DEW_DEVOPS_MOCK_CLASS_PATH, formattedProperties)
                .ifPresent(obj -> mockClasspath = obj);
    }

    /**
     * Gets mojo name.
     *
     * @return the mojo name
     */
    protected String getMojoName() {
        return this.getClass().getSimpleName().substring(0, this.getClass().getSimpleName().indexOf("Mojo")).toLowerCase();
    }

    @Override
    public Log getLog() {
        return Dew.log;
    }

}
