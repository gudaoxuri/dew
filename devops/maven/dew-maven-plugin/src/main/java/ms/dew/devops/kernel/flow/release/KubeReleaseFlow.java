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
import io.kubernetes.client.models.*;
import ms.dew.devops.kernel.config.FinalProjectConfig;
import ms.dew.devops.kernel.exception.ProjectProcessException;
import ms.dew.devops.kernel.flow.BasicFlow;
import ms.dew.devops.kernel.function.VersionController;
import ms.dew.devops.kernel.helper.DockerHelper;
import ms.dew.devops.kernel.helper.KubeHelper;
import ms.dew.devops.kernel.helper.KubeRES;
import ms.dew.devops.kernel.resource.KubeDeploymentBuilder;
import ms.dew.devops.kernel.resource.KubeServiceBuilder;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
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

    private static final int WAIT_TIMEOUT_MINUTES = 20;

    private String flowBasePath;

    @Override
    protected void process(FinalProjectConfig config, String flowBasePath) throws ApiException, IOException {
        this.flowBasePath = flowBasePath;
        release(config, config.getAppVersion());
        if (config.getApp().getRevisionHistoryLimit() > 0) {
            logger.debug("Delete old version from kubernetes resources and docker images");
            removeOldVersions(config);
        }
        DockerBuildFlow.ReuseVersionProcessorFactory.processAfterReleaseSuccessful(config);
    }

    /**
     * Release.
     *
     * @param config     the project config
     * @param appVersion the app version
     * @throws ApiException the api exception
     * @throws IOException  the io exception
     */
    public void release(FinalProjectConfig config, String appVersion) throws ApiException, IOException {
        Map<String, Object> deployResult;
        V1ConfigMap oldVersion = VersionController.getVersion(config, appVersion, true);
        if (oldVersion != null) {
            deployResult = fetchOldVersionResources(config, oldVersion);
            logger.info("Rollback version to : " + VersionController.getVersionName(config, appVersion));
            release(config, deployResult, appVersion, VersionController.getGitCommit(oldVersion), true);
            return;
        }
        logger.info("Deploy new version : " + VersionController.getVersionName(config, config.getAppVersion()));
        deployResult = buildNewVersionResources(config, flowBasePath);
        release(config, deployResult, config.getAppVersion(), config.getGitCommit(), false);
    }

    /**
     * Release.
     *
     * @param config       the project config
     * @param deployResult the deploy result
     * @param appVersion   the app version
     * @param gitCommit    the git commit
     * @param reRelease    the re-release
     * @throws ApiException the api exception
     * @throws IOException  the io exception
     */
    private void release(FinalProjectConfig config, Map<String, Object> deployResult, String appVersion, String gitCommit, boolean reRelease)
            throws ApiException, IOException {
        logger.info("Publishing kubernetes resources");
        deployResources(config, deployResult);
        logger.debug("Add version to ConfigMap");
        appendVersionInfo(config, deployResult, appVersion, gitCommit, reRelease);
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
                            && resp.object.getStatus().getAvailableReplicas() != null
                            && resp.object.getStatus().getReadyReplicas().intValue() == resp.object.getSpec().getReplicas()
                            && resp.object.getStatus().getAvailableReplicas().intValue() == resp.object.getSpec().getReplicas()) {
                        try {
                            long runningPodSize = KubeHelper.inst(config.getId())
                                    .list(select, deploymentRes.getMetadata().getNamespace(), KubeRES.POD, V1Pod.class)
                                    .stream().filter(pod ->
                                            pod.getStatus().getPhase().equalsIgnoreCase("Running")
                                                    && pod.getStatus().getContainerStatuses().stream().allMatch(V1ContainerStatus::isReady)
                                    )
                                    .count();
                            while (resp.object.getSpec().getReplicas() != runningPodSize) {
                                runningPodSize = KubeHelper.inst(config.getId())
                                        .list(select, deploymentRes.getMetadata().getNamespace(), KubeRES.POD, V1Pod.class)
                                        .stream().filter(pod ->
                                                pod.getStatus().getPhase().equalsIgnoreCase("Running")
                                                        && pod.getStatus().getContainerStatuses().stream().allMatch(V1ContainerStatus::isReady)
                                        )
                                        .count();
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
            boolean awaitResult = cdl.await(WAIT_TIMEOUT_MINUTES, TimeUnit.MINUTES);
            KubeHelper.inst(config.getId()).stopWatch(watchId);
            if (!awaitResult) {
                logger.error("Publish wait timeout");
                throw new ProjectProcessException("Publish wait timeout");
            }
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
            logger.error("Publish error", e);
            throw new ProjectProcessException("Publish error", e);
        }
    }

    /**
     * Append version info.
     *
     * @param config        the project config
     * @param kubeResources the kube resources
     * @param appVersion    the app version
     * @param gitCommit     the git commit
     * @param reRelease     the re-release
     * @throws ApiException the api exception
     */
    private void appendVersionInfo(FinalProjectConfig config, Map<String, Object> kubeResources,
                                   String appVersion, String gitCommit, boolean reRelease)
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
        VersionController.addNewVersion(config, appVersion, gitCommit, reRelease, resources, new HashMap<>());
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
        List<V1ConfigMap> verConfigMaps = VersionController.getVersionHistory(config.getId(), config.getAppName(), config.getNamespace(), false);
        int offset = config.getApp().getRevisionHistoryLimit();
        for (V1ConfigMap configMap : verConfigMaps) {
            boolean enabled = VersionController.isVersionEnabled(configMap);
            if (enabled) {
                // 保留要求的版本数量
                offset--;
            }
            if (!enabled || offset <= 0) {
                String oldGitCommit = VersionController.getGitCommit(configMap);
                logger.debug("Remove old version : " + configMap.getMetadata().getName());
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
