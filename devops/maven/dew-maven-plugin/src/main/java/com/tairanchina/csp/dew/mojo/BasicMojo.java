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
import com.tairanchina.csp.dew.util.DewLog;
import io.kubernetes.client.ApiException;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.BuildPluginManager;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.Parameter;

import java.io.IOException;

public abstract class BasicMojo extends AbstractMojo {

    public static final String FLAG_DEW_DEVOPS_DEFAULT_PROFILE = "default";

    private static final String FLAG_DEW_DEVOPS_PROFILE = "dew.devops.profile";
    private static final String FLAG_DEW_DEVOPS_QUIET = "dew.devops.quiet";
    private static final String FLAG_DEW_DEVOPS_DOCKER_HOST = "dew.devops.docker.host";
    private static final String FLAG_DEW_DEVOPS_DOCKER_REGISTRY_URL = "dew.devops.docker.registry.url";
    private static final String FLAG_DEW_DEVOPS_DOCKER_REGISTRY_USERNAME = "dew.devops.docker.registry.username";
    private static final String FLAG_DEW_DEVOPS_DOCKER_REGISTRY_PASSWORD = "dew.devops.docker.registry.password";
    private static final String FLAG_DEW_DEVOPS_KUBE_CONFIG = "dew.devops.kube.config";

    @Parameter(property = FLAG_DEW_DEVOPS_PROFILE, defaultValue = FLAG_DEW_DEVOPS_DEFAULT_PROFILE)
    private String profile;

    @Parameter(property = FLAG_DEW_DEVOPS_QUIET)
    protected boolean quiet;

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
    private MavenSession session;

    @Component
    private BuildPluginManager pluginManager;


    protected boolean preExecute() throws MojoExecutionException, MojoFailureException, IOException, ApiException {
        return true;
    }

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        if (Dew.stopped) {
            return;
        }
        try {
            Dew.log = new DewLog(super.getLog(), "[DEW][" + getMojoName() + "]:");
            Dew.log.info("Start...");
            Dew.Init.init(session, pluginManager, profile,
                    dockerHost, dockerRegistryUrl, dockerRegistryUserName, dockerRegistryPassword, kubeBase64Config);
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

    protected abstract boolean executeInternal() throws MojoExecutionException, MojoFailureException, IOException, ApiException;

    private String getMojoName() {
        return this.getClass().getSimpleName().substring(0, this.getClass().getSimpleName().indexOf("Mojo"));
    }

    @Override
    public Log getLog() {
        return Dew.log;
    }

}