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

package com.tairanchina.csp.dew.mojo;

import com.tairanchina.csp.dew.kernel.Dew;
import com.tairanchina.csp.dew.utils.DewLog;
import io.kubernetes.client.ApiException;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.BuildPluginManager;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

import java.io.IOException;

public abstract class BasicMojo extends AbstractMojo {

    public static final String FLAG_DEW_DEVOPS_DEFAULT_PROFILE = "default";

    public static final String FLAG_DEW_DEVOPS_SKIP = "dew.devops.skip";
    public static final String FLAG_DEW_DEVOPS_PROFILE = "dew.devops.profile";
    public static final String FLAG_DEW_DEVOPS_DOCKER_HOST = "dew.devops.docker.host";
    public static final String FLAG_DEW_DEVOPS_DOCKER_REGISTRY_URL = "dew.devops.docker.registry.url";
    public static final String FLAG_DEW_DEVOPS_DOCKER_REGISTRY_USERNAME = "dew.devops.docker.registry.username";
    public static final String FLAG_DEW_DEVOPS_DOCKER_REGISTRY_PASSWORD = "dew.devops.docker.registry.password";
    public static final String FLAG_DEW_DEVOPS_KUBE_CONFIG = "dew.devops.kube.config";

    @Parameter(property = FLAG_DEW_DEVOPS_SKIP)
    private Boolean skip;

    @Parameter(property = FLAG_DEW_DEVOPS_PROFILE, defaultValue = FLAG_DEW_DEVOPS_DEFAULT_PROFILE)
    private String profile;

    @Parameter(property = FLAG_DEW_DEVOPS_DOCKER_HOST)
    private String dockerHost;

    @Parameter(property = FLAG_DEW_DEVOPS_DOCKER_REGISTRY_URL)
    private String dockerRegistryUrl;

    @Parameter(property = FLAG_DEW_DEVOPS_DOCKER_REGISTRY_USERNAME)
    private String dockerRegistryUserName;

    @Parameter(property = FLAG_DEW_DEVOPS_DOCKER_REGISTRY_PASSWORD)
    private String dockerRegistryPassword;

    @Parameter(property = FLAG_DEW_DEVOPS_KUBE_CONFIG)
    private String kubeBase64Config;

    @Component
    protected MavenProject project;

    @Component
    protected MavenSession session;

    @Component
    protected BuildPluginManager pluginManager;


    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        try {
            Dew.log = new DewLog(super.getLog(), "[DEW][" + getMojoName() + "]:");
            Dew.log.info("Start...");
            if (skip != null && skip) {
                // 传入参数或从各项目pom配置中读取的 dew.devops.skip 为第一优先级
                Dew.log.info("Skipped");
                return;
            }
            Dew.Init.init(skip, profile,
                    dockerHost, dockerRegistryUrl, dockerRegistryUserName, dockerRegistryPassword, kubeBase64Config,
                    project, session, pluginManager);
            if (Dew.config.isSkip()) {
                // 各项目 .dew 配置 skip=true || 不支持的app kind
                Dew.log.info("Skipped");
                return;
            }
            executeInternal();
            Dew.log.info("Successful");
        } catch (MojoExecutionException | MojoFailureException e) {
            Dew.log.error("Error", e);
            throw e;
        } catch (Exception e) {
            Dew.log.error("Error", e);
            throw new MojoFailureException(e.getMessage());
        }
    }

    protected abstract void executeInternal() throws MojoExecutionException, MojoFailureException, IOException, ApiException;

    private String getMojoName() {
        return this.getClass().getSimpleName().substring(0, this.getClass().getSimpleName().indexOf("Mojo"));
    }

    @Override
    public Log getLog() {
        return Dew.log;
    }

}