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

package com.tairanchina.csp.dew.kernel.flow.release;

import com.ecfront.dew.common.$;
import com.tairanchina.csp.dew.helper.DockerHelper;
import com.tairanchina.csp.dew.helper.GitHelper;
import com.tairanchina.csp.dew.helper.KubeHelper;
import com.tairanchina.csp.dew.helper.YamlHelper;
import com.tairanchina.csp.dew.kernel.Dew;
import com.tairanchina.csp.dew.kernel.flow.BasicFlow;
import com.tairanchina.csp.dew.kernel.resource.KubeConfigMapBuilder;
import com.tairanchina.csp.dew.kernel.resource.KubeDeploymentBuilder;
import com.tairanchina.csp.dew.kernel.resource.KubeServiceBuilder;
import com.tairanchina.csp.dew.mojo.ReleaseMojo;
import io.kubernetes.client.ApiException;
import io.kubernetes.client.models.ExtensionsV1beta1Deployment;
import io.kubernetes.client.models.V1ConfigMap;
import io.kubernetes.client.models.V1Service;
import org.apache.maven.plugin.MojoExecutionException;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public abstract class BasicReleaseFlow extends BasicFlow {

    protected static final String FLAG_DEPLOY_GET_INITIALIZED = "deploy.git.initialized";
    protected static final String FLAG_DEPLOY_GIT_CHANGED_FILES = "deploy.git.changedFiles";

    protected boolean preProcess(boolean releaseAll) throws ApiException, IOException, MojoExecutionException {
        return true;
    }

    protected boolean preNeedCheck(boolean releaseAll) throws ApiException, IOException, MojoExecutionException {
        return true;
    }

    protected boolean postNeedCheck() throws ApiException, IOException, MojoExecutionException {
        return true;
    }

    protected boolean preDockerBuild(String buildBasePath) throws ApiException, IOException, MojoExecutionException {
        return true;
    }

    protected boolean postDockerBuild(String buildBasePath) throws ApiException, IOException, MojoExecutionException {
        return true;
    }

    protected boolean preKubePublish() throws ApiException, IOException, MojoExecutionException {
        return true;
    }

    protected void postKubePublish() throws ApiException, IOException, MojoExecutionException {
    }

    public void process(boolean releaseAll) throws ApiException, IOException, MojoExecutionException {
        Dew.log.debug("Need release checking...");
        if (!preProcess(releaseAll)) {
            Dew.log.debug("Finished,because [preProcess] is false");
            return;
        }
        if (!preNeedCheck(releaseAll)) {
            Dew.log.debug("Finished,because [preNeedCheck] is false");
            return;
        }
        // 判断当前项目是否需要发布
        if (!releaseAll) {
            if (Dew.Context.setPropIfAbsent(FLAG_DEPLOY_GET_INITIALIZED, "true", "false")) {
                String lastVersionDeployCommit = prepare_fetchLastVersionDeployCommit();
                Dew.log.debug("Latest commit is " + lastVersionDeployCommit);
                if (lastVersionDeployCommit != null) {
                    Dew.Context.setPropIfAbsent(FLAG_DEPLOY_GIT_CHANGED_FILES, prepare_fetchGitDiff(lastVersionDeployCommit), null);
                } else {
                    Dew.Context.setProp(ReleaseMojo.FLAG_DEW_DEVOPS_RELEASE_ALL, "true");
                    releaseAll = true;
                }
            }
        }
        if (!releaseAll) {
            if (!prepare_needDeployByCurrentApp(Dew.Context.getProp(FLAG_DEPLOY_GIT_CHANGED_FILES, "[]"))) {
                Dew.log.debug("Finished,because no file changed");
                return;
            }
        }
        if (!postNeedCheck()) {
            Dew.log.debug("Finished,because [postNeedCheck] is false");
            return;
        }
        Dew.log.debug("Need release check successful");

        // 镜像构建
        Dew.log.debug("Docker image building...");
        String buildBasePath = Dew.projectTargetDirectory + "dew_build" + File.separator;
        Files.createDirectories(Paths.get(buildBasePath));
        if (!preDockerBuild(buildBasePath)) {
            Dew.log.debug("Finished,because [preDockerBuild] is false");
            return;
        }
        build_buildImage(buildBasePath);
        if (Dew.config.getDocker().getRegistryUrl() == null || Dew.config.getDocker().getRegistryUrl().isEmpty()) {
            Dew.log.warn("Not found docker registry url and push is ignored, which is mostly used for stand-alone testing");
        } else {
            build_pushImage();
        }
        if (!postDockerBuild(buildBasePath)) {
            Dew.log.debug("Finished,because [postDockerBuild] is false");
            return;
        }
        Dew.log.debug("Docker image build successful");

        // 应用部署
        Dew.log.debug("Kubernetes publish...");
        if (!preKubePublish()) {
            Dew.log.debug("Finished,because [preKubePublish] is false");
            return;
        }
        Dew.log.debug("Build kubernetes resources");
        Map<String, Object> deployResult = deploy_buildResources();
        build_appendVersionInfo(deployResult);
        Dew.log.debug("Add version to ConfigMap ");
        Dew.log.debug("Publish kubernetes resources");
        deploy_deployResources(deployResult);
        Dew.log.debug("Enabled version");
        build_enabledVersionInfo();
        if (Dew.config.getApp().getRevisionHistoryLimit() > 0) {
            Dew.log.debug("Delete old version from kubernetes resources and docker images");
            build_removeOldVersions(Dew.config.getApp().getRevisionHistoryLimit());
        }
        postKubePublish();
        Dew.log.debug("Kubernetes publish successful");
    }

    private String prepare_fetchLastVersionDeployCommit() throws ApiException {
        V1Service lastVersionService = KubeHelper.read(Dew.config.getAppName(), Dew.config.getNamespace(), KubeHelper.RES.SERVICE, V1Service.class);
        if (lastVersionService == null) {
            return null;
        } else {
            return lastVersionService.getMetadata().getAnnotations().get(FLAG_KUBE_RESOURCE_GIT_COMMIT);
        }
    }

    private String prepare_fetchGitDiff(String lastVersionDeployCommit) {
        List<String> changedFiles = GitHelper.diff(lastVersionDeployCommit, "HEAD", Dew.rootDirectory);
        Dew.log.debug("Change files:");
        Dew.log.debug("-------------------");
        changedFiles.forEach(file -> Dew.log.debug(">>" + file));
        Dew.log.debug("-------------------");
        return $.json.toJsonString(changedFiles);
    }

    private boolean prepare_needDeployByCurrentApp(String changedFileStr) {
        List<String> changedFiles = $.json.toList(changedFileStr, String.class);
        String projectPath = Dew.projectDirectory.substring(Dew.rootDirectory.length()).replaceAll("\\\\", "/");
        changedFiles = changedFiles.stream()
                .filter(file -> file.startsWith(projectPath))
                .collect(Collectors.toList());
        Dew.log.debug("Found " + changedFiles.size() + " changed files by " + Dew.mavenProject.getArtifactId());
        if (changedFiles.isEmpty()) {
            return false;
        } else if (!Dew.config.getApp().getIgnoreChangeFiles().isEmpty()) {
            if (!$.file.noneMath(changedFiles, new ArrayList<>(Dew.config.getApp().getIgnoreChangeFiles()))) {
                Dew.log.debug("Found 0 changed files by " + Dew.mavenProject.getArtifactId() + " filter ignore files");
                return false;
            }
        }
        return true;
    }

    private void build_buildImage(String buildBasePath) {
        DockerHelper.Image.build(Dew.config.getCurrImageName(), buildBasePath);
    }

    private void build_pushImage() {
        DockerHelper.Image.push(Dew.config.getCurrImageName(), true);
    }

    private Map<String, Object> deploy_buildResources() {
        ExtensionsV1beta1Deployment deployment = new KubeDeploymentBuilder().build(Dew.config);
        V1Service service = new KubeServiceBuilder().build(Dew.config);
        return new HashMap<String, Object>() {{
            put(KubeHelper.RES.DEPLOYMENT.getVal(), deployment);
            put(KubeHelper.RES.SERVICE.getVal(), service);
        }};
    }

    private void deploy_deployResources(Map<String, Object> kubeResources) throws ApiException, IOException {
        for (Map.Entry<String, Object> res : kubeResources.entrySet()) {
            if (res.getKey().equals(KubeHelper.RES.SERVICE.getVal())) {
                V1Service service = (V1Service) res.getValue();
                if (!KubeHelper.exist(service.getMetadata().getName(), service.getMetadata().getNamespace(), KubeHelper.RES.SERVICE)) {
                    KubeHelper.create(service);
                } else {
                    KubeHelper.patch(service.getMetadata().getName(), new KubeServiceBuilder().buildPatch(service), service.getMetadata().getNamespace(), KubeHelper.RES.SERVICE);
                }
            } else {
                KubeHelper.apply(res.getValue());
            }
        }
        CountDownLatch cdl = new CountDownLatch(1);
        ExtensionsV1beta1Deployment deploymentRes = (ExtensionsV1beta1Deployment) kubeResources.get(KubeHelper.RES.DEPLOYMENT.getVal());
        String watchId = KubeHelper.watch((coreApi, extensionsApi, rbacAuthorizationApi)
                        -> extensionsApi.listNamespacedDeploymentCall(deploymentRes.getMetadata().getNamespace(),
                null, null, null, null,
                "app=" + deploymentRes.getMetadata().getName(), 1, null, null, Boolean.TRUE, null, null),
                resp -> {
                    if (resp.object.getStatus().getReadyReplicas() != null
                            && resp.object.getStatus().getReadyReplicas().intValue() == resp.object.getSpec().getReplicas()) {
                        cdl.countDown();
                    }
                },
                ExtensionsV1beta1Deployment.class);
        try {
            cdl.await(30, TimeUnit.MINUTES);
            KubeHelper.stopWatch(watchId);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            Dew.log.error("Publish error,maybe timeout.", e);
            throw new RuntimeException(e);
        }
    }

    private void build_appendVersionInfo(Map<String, Object> kubeResources) throws ApiException {
        Map<String, String> resources = kubeResources.entrySet()
                .stream().collect(Collectors.toMap(Map.Entry::getKey,
                        entry -> {
                            try {
                                return $.security.encodeStringToBase64(YamlHelper.toYaml(entry.getValue()), "UTF-8");
                            } catch (UnsupportedEncodingException ignore) {
                                return "";
                            }
                        }));
        V1ConfigMap currVerConfigMap = new KubeConfigMapBuilder().build(
                getVersionName(), new HashMap<String, String>() {{
                    put("app", Dew.config.getAppName());
                    put(FLAG_KUBE_RESOURCE_GIT_COMMIT, Dew.config.getGitCommit());
                    put("kind", "version");
                    put("enabled", "false");
                    put("lastUpdateTime", System.currentTimeMillis() + "");
                }}, resources);
        KubeHelper.apply(currVerConfigMap);
    }

    private void build_enabledVersionInfo() throws ApiException {
        KubeHelper.patch(getVersionName(), new ArrayList<String>() {{
            add("{\"op\":\"replace\",\"path\":\"/metadata/labels/enabled\",\"value\":\"true\"}");
        }}, Dew.config.getNamespace(), KubeHelper.RES.CONFIG_MAP);
    }

    private void build_removeOldVersions(int revisionHistoryLimit) throws ApiException {
        List<V1ConfigMap> verConfigMaps = KubeHelper.list("name=" + Dew.config.getAppName() + ",kind=version", Dew.config.getNamespace(), KubeHelper.RES.CONFIG_MAP, V1ConfigMap.class);
        verConfigMaps.sort((m1, m2) -> Long.valueOf(m2.getMetadata().getLabels().get("lastUpdateTime")).compareTo(Long.valueOf(m1.getMetadata().getLabels().get("lastUpdateTime"))));
        int offset = revisionHistoryLimit;
        for (V1ConfigMap configMap : verConfigMaps) {
            if (configMap.getMetadata().getLabels().get("enabled").equals("true")) {
                offset--;
            }
            if (offset <= 0) {
                String oldGitCommit = configMap.getMetadata().getLabels().get(FLAG_KUBE_RESOURCE_GIT_COMMIT);
                KubeHelper.delete(configMap.getMetadata().getName(), Dew.config.getNamespace(), KubeHelper.RES.CONFIG_MAP);
                DockerHelper.Image.remove(Dew.config.getImageName(oldGitCommit));
            }
        }
    }

    protected String getVersionName() {
        return "ver." + Dew.config.getAppName() + "." + Dew.config.getGitCommit();
    }

}
