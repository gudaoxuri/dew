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

package ms.dew.devops.kernel.flow.release;

import com.ecfront.dew.common.$;
import io.kubernetes.client.ApiException;
import io.kubernetes.client.models.ExtensionsV1beta1Deployment;
import io.kubernetes.client.models.V1ConfigMap;
import io.kubernetes.client.models.V1Pod;
import io.kubernetes.client.models.V1Service;
import ms.dew.devops.helper.DockerHelper;
import ms.dew.devops.helper.KubeHelper;
import ms.dew.devops.helper.KubeRES;
import ms.dew.devops.kernel.Dew;
import ms.dew.devops.kernel.config.FinalProjectConfig;
import ms.dew.devops.kernel.exception.ProcessException;
import ms.dew.devops.kernel.flow.BasicFlow;
import ms.dew.devops.kernel.resource.KubeConfigMapBuilder;
import ms.dew.devops.kernel.resource.KubeDeploymentBuilder;
import ms.dew.devops.kernel.resource.KubeServiceBuilder;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * Kubernetes release flow.
 *
 * @author gudaoxuri
 */
public class KubeReleaseFlow extends BasicFlow {

    private static final int WAIT_ITMEOUT_MINUTES = 30;

    private String flowBasePath;

    @Override
    public boolean process(FinalProjectConfig config, String flowBasePath) throws ApiException, IOException {
        this.flowBasePath = flowBasePath;
        release(config, config.getGitCommit());
        if (config.getApp().getRevisionHistoryLimit() > 0) {
            Dew.log.debug("Delete old version from kubernetes resources and docker images");
            removeOldVersions(config);
        }
        return true;
    }

    /**
     * Release.
     *
     * @param config    the project config
     * @param gitCommit the git commit
     * @throws ApiException the api exception
     * @throws IOException  the io exception
     */
    public void release(FinalProjectConfig config, String gitCommit) throws ApiException, IOException {
        Map<String, Object> deployResult;
        V1ConfigMap oldVersion = getOldVersion(config, gitCommit);
        if (oldVersion != null) {
            deployResult = fetchOldVersionResources(config, oldVersion);
            Dew.log.info("Rollback version to : " + getVersionName(config, gitCommit));
            release(config, deployResult, gitCommit, true);
        } else {
            Dew.log.info("Deploy new version : " + getVersionName(config, gitCommit));
            deployResult = buildNewVersionResources(config, flowBasePath);
            release(config, deployResult, gitCommit, false);
        }
    }

    /**
     * Release.
     *
     * @param config       the project config
     * @param deployResult the deploy result
     * @param gitCommit    the git commit
     * @param reRelease    the re-release
     * @throws ApiException the api exception
     * @throws IOException  the io exception
     */
    private void release(FinalProjectConfig config, Map<String, Object> deployResult, String gitCommit, boolean reRelease)
            throws ApiException, IOException {
        Dew.log.debug("Add version to ConfigMap");
        appendVersionInfo(config, deployResult, gitCommit, reRelease);
        Dew.log.info("Publishing kubernetes resources");
        deployResources(config, deployResult);
        Dew.log.debug("Enabled version");
        enabledVersionInfo(config, gitCommit);
    }

    /**
     * Fetch old version resources from config map.
     *
     * @param config     the project config
     * @param oldVersion the old version
     * @return the old version resources
     * @throws IOException the io exception
     */
    private Map<String, Object> fetchOldVersionResources(FinalProjectConfig config, V1ConfigMap oldVersion) throws IOException {
        ExtensionsV1beta1Deployment rollbackDeployment = KubeHelper.inst(config.getId()).toResource(
                $.security.decodeBase64ToString(oldVersion.getData().get(KubeRES.DEPLOYMENT.getVal()), "UTF-8"),
                ExtensionsV1beta1Deployment.class);
        V1Service rollbackService = KubeHelper.inst(config.getId()).toResource(
                $.security.decodeBase64ToString(oldVersion.getData().get(KubeRES.SERVICE.getVal()), "UTF-8"),
                V1Service.class);
        return new HashMap<String, Object>() {
            {
                put(KubeRES.DEPLOYMENT.getVal(), rollbackDeployment);
                put(KubeRES.SERVICE.getVal(), rollbackService);
            }
        };
    }

    /**
     * Build new version resources map.
     *
     * @param config       the project config
     * @param flowBasePath the flow base path
     * @return the new version resources
     * @throws IOException the io exception
     */
    private Map<String, Object> buildNewVersionResources(FinalProjectConfig config, String flowBasePath) throws IOException {
        ExtensionsV1beta1Deployment deployment = new KubeDeploymentBuilder().build(config);
        Files.write(Paths.get(flowBasePath + KubeRES.DEPLOYMENT.getVal() + ".yaml"),
                KubeHelper.inst(config.getId()).toString(deployment).getBytes(StandardCharsets.UTF_8));
        V1Service service = new KubeServiceBuilder().build(config);
        Files.write(Paths.get(flowBasePath + KubeRES.SERVICE.getVal() + ".yaml"),
                KubeHelper.inst(config.getId()).toString(service).getBytes(StandardCharsets.UTF_8));
        return new HashMap<String, Object>() {
            {
                put(KubeRES.DEPLOYMENT.getVal(), deployment);
                put(KubeRES.SERVICE.getVal(), service);
            }
        };
    }

    /**
     * Append version info.
     *
     * @param config        the project config
     * @param kubeResources the kube resources
     * @param gitCommit     the git commit
     * @param reRelease     the re-release
     * @throws ApiException the api exception
     */
    private void appendVersionInfo(FinalProjectConfig config, Map<String, Object> kubeResources, String gitCommit, boolean reRelease)
            throws ApiException {
        Map<String, String> resources = kubeResources.entrySet()
                .stream().collect(Collectors.toMap(Map.Entry::getKey,
                        entry -> {
                            try {
                                return $.security.encodeStringToBase64(KubeHelper.inst(config.getId()).toString(entry.getValue()), "UTF-8");
                            } catch (UnsupportedEncodingException ignore) {
                                return "";
                            }
                        }));
        V1ConfigMap currVerConfigMap = new KubeConfigMapBuilder().build(
                getVersionName(config, gitCommit), config.getNamespace(), new HashMap<String, String>() {
                    {
                        put(FLAG_VERSION_APP, config.getAppName());
                        put(FLAG_KUBE_RESOURCE_GIT_COMMIT, gitCommit);
                        put(FLAG_VERSION_KIND, "version");
                        // 初始时为禁用状态
                        put(FLAG_VERSION_ENABLED, "false");
                        put(FLAG_VERSION_LAST_UPDATE_TIME, System.currentTimeMillis() + "");
                        put(FLAG_VERSION_RE_RELEASE, reRelease + "");
                    }
                }, resources);
        KubeHelper.inst(config.getId()).apply(currVerConfigMap);
    }

    /**
     * Deploy resources.
     *
     * @param config        the project config
     * @param kubeResources the kube resources
     * @throws ApiException the api exception
     * @throws IOException  the io exception
     */
    private void deployResources(FinalProjectConfig config, Map<String, Object> kubeResources) throws ApiException, IOException {
        // 部署 deployment
        ExtensionsV1beta1Deployment deployment = (ExtensionsV1beta1Deployment) kubeResources.get(KubeRES.DEPLOYMENT.getVal());
        KubeHelper.inst(config.getId()).apply(deployment);
        CountDownLatch cdl = new CountDownLatch(1);
        ExtensionsV1beta1Deployment deploymentRes = (ExtensionsV1beta1Deployment) kubeResources.get(KubeRES.DEPLOYMENT.getVal());
        String select = "app="
                + deploymentRes.getMetadata().getName()
                + ",group=" + deploymentRes.getMetadata().getLabels().get("group")
                + ",version=" + deploymentRes.getMetadata().getLabels().get("version");
        String watchId = KubeHelper.inst(config.getId()).watch(
                (coreApi, extensionsApi, rbacAuthorizationApi, autoscalingApi)
                        -> extensionsApi.listNamespacedDeploymentCall(deploymentRes.getMetadata().getNamespace(),
                        null, null, null, null,
                        select, 1, null, null, Boolean.TRUE, null, null),
                resp -> {
                    // Ready Pod数量是否等于设定的数量
                    if (resp.object.getStatus().getReadyReplicas() != null
                            && resp.object.getStatus().getReadyReplicas().intValue() == resp.object.getSpec().getReplicas()) {
                        try {
                            long runningPodSize = KubeHelper.inst(config.getId())
                                    .list(select, deploymentRes.getMetadata().getNamespace(), KubeRES.POD, V1Pod.class)
                                    .stream().filter(pod -> pod.getStatus().getPhase().equalsIgnoreCase("Running"))
                                    .count();
                            if (resp.object.getSpec().getReplicas() != runningPodSize) {
                                // 之前版本没有销毁
                                Thread.sleep(1000);
                            }
                        } catch (ApiException | InterruptedException e) {
                            e.printStackTrace();
                        }
                        cdl.countDown();
                    }
                },
                ExtensionsV1beta1Deployment.class);
        try {
            // 等待 deployment 部署完成
            cdl.await(WAIT_ITMEOUT_MINUTES, TimeUnit.MINUTES);
            KubeHelper.inst(config.getId()).stopWatch(watchId);
            // 部署 service
            V1Service service = (V1Service) kubeResources.get(KubeRES.SERVICE.getVal());
            if (!KubeHelper.inst(config.getId()).exist(service.getMetadata().getName(), service.getMetadata().getNamespace(), KubeRES.SERVICE)) {
                KubeHelper.inst(config.getId()).create(service);
            } else {
                KubeHelper.inst(config.getId())
                        .patch(service.getMetadata().getName(),
                                new KubeServiceBuilder().buildPatch(service), service.getMetadata().getNamespace(), KubeRES.SERVICE);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            Dew.log.error("Publish error,maybe timeout", e);
            throw new ProcessException("Publish error,maybe timeout", e);
        }
    }

    /**
     * Enabled version info.
     *
     * @param config    the project config
     * @param gitCommit the git commit
     * @throws ApiException the api exception
     */
    private void enabledVersionInfo(FinalProjectConfig config, String gitCommit) throws ApiException {
        KubeHelper.inst(config.getId()).patch(getVersionName(config, gitCommit), new ArrayList<String>() {
            {
                add("{\"op\":\"replace\",\"path\":\"/metadata/labels/enabled\",\"value\":\"true\"}");
            }
        }, config.getNamespace(), KubeRES.CONFIG_MAP);
    }

    /**
     * Remove old versions.
     *
     * @param config the project config
     * @throws ApiException the api exception
     * @throws IOException  the io exception
     */
    private void removeOldVersions(FinalProjectConfig config) throws ApiException, IOException {
        // 获取所有历史版本
        List<V1ConfigMap> verConfigMaps = getVersionHistory(config, false);
        int offset = config.getApp().getRevisionHistoryLimit();
        for (V1ConfigMap configMap : verConfigMaps) {
            boolean enabled = configMap.getMetadata().getLabels().get(FLAG_VERSION_ENABLED).equalsIgnoreCase("true");
            if (enabled) {
                // 保留要求的版本数量
                offset--;
            }
            if (!enabled || offset <= 0) {
                String oldGitCommit = configMap.getMetadata().getLabels().get(FLAG_KUBE_RESOURCE_GIT_COMMIT);
                Dew.log.debug("Remove old version : " + configMap.getMetadata().getName());
                // 删除 config map
                KubeHelper.inst(config.getId()).delete(configMap.getMetadata().getName(), config.getNamespace(), KubeRES.CONFIG_MAP);
                // 删除本地 image (不包含其它节点)
                DockerHelper.inst(config.getId()).image.remove(config.getImageName(oldGitCommit));
                // 删除 registry 中的 image
                DockerHelper.inst(config.getId()).registry.remove(config.getImageName(oldGitCommit));
            }
        }
    }

}
