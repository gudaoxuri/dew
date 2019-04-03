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
import ms.dew.devops.helper.KubeOpt;
import ms.dew.devops.kernel.Dew;
import ms.dew.devops.kernel.flow.BasicFlow;
import ms.dew.devops.kernel.resource.KubeConfigMapBuilder;
import ms.dew.devops.kernel.resource.KubeDeploymentBuilder;
import ms.dew.devops.kernel.resource.KubeServiceBuilder;
import org.apache.maven.plugin.MojoExecutionException;

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

public class DefaultReleaseFlow extends BasicFlow {

    private String flowBasePath;

    public boolean process(String flowBasePath) throws ApiException, IOException, MojoExecutionException {
        this.flowBasePath = flowBasePath;
        release(Dew.Config.getCurrentProject().getGitCommit());
        if (Dew.Config.getCurrentProject().getApp().getRevisionHistoryLimit() > 0) {
            Dew.log.debug("Delete old version from kubernetes resources and docker images");
            removeOldVersions(Dew.Config.getCurrentProject().getApp().getRevisionHistoryLimit());
        }
        return true;
    }

    public void release(String gitCommit) throws ApiException, IOException {
        Map<String, Object> deployResult;
        V1ConfigMap oldVersion = getOldVersion(gitCommit);
        if (oldVersion != null) {
            deployResult = fetchOldVersionResources(oldVersion);
            Dew.log.info("Rollback version to : " + getVersionName(gitCommit));
            release(deployResult, gitCommit, true);
        } else {
            Dew.log.info("Deploy new version : " + getVersionName(gitCommit));
            deployResult = buildNewVersionResources(flowBasePath);
            release(deployResult, gitCommit, false);
        }
    }

    private Map<String, Object> fetchOldVersionResources(V1ConfigMap oldVersion) throws IOException {
        ExtensionsV1beta1Deployment rollbackDeployment = KubeHelper.inst(Dew.Config.getCurrentProject().getId()).toResource(
                $.security.decodeBase64ToString(oldVersion.getData().get(KubeOpt.RES.DEPLOYMENT.getVal()), "UTF-8"),
                ExtensionsV1beta1Deployment.class);
        V1Service rollbackService = KubeHelper.inst(Dew.Config.getCurrentProject().getId()).toResource(
                $.security.decodeBase64ToString(oldVersion.getData().get(KubeOpt.RES.SERVICE.getVal()), "UTF-8"),
                V1Service.class);
        return new HashMap<String, Object>() {
            {
                put(KubeOpt.RES.DEPLOYMENT.getVal(), rollbackDeployment);
                put(KubeOpt.RES.SERVICE.getVal(), rollbackService);
            }
        };
    }

    private Map<String, Object> buildNewVersionResources(String flowBasePath) throws IOException {
        ExtensionsV1beta1Deployment deployment = new KubeDeploymentBuilder().build(Dew.Config.getCurrentProject());
        V1Service service = new KubeServiceBuilder().build(Dew.Config.getCurrentProject());
        Files.write(Paths.get(flowBasePath + KubeOpt.RES.DEPLOYMENT.getVal() + ".yaml"),
                KubeHelper.inst(Dew.Config.getCurrentProject().getId()).toString(deployment).getBytes(StandardCharsets.UTF_8));
        Files.write(Paths.get(flowBasePath + KubeOpt.RES.SERVICE.getVal() + ".yaml"),
                KubeHelper.inst(Dew.Config.getCurrentProject().getId()).toString(service).getBytes(StandardCharsets.UTF_8));
        return new HashMap<String, Object>() {
            {
                put(KubeOpt.RES.DEPLOYMENT.getVal(), deployment);
                put(KubeOpt.RES.SERVICE.getVal(), service);
            }
        };
    }

    private void release(Map<String, Object> deployResult, String gitCommit, boolean reRelease) throws ApiException, IOException {
        appendVersionInfo(deployResult, gitCommit, reRelease);
        Dew.log.debug("Add version to ConfigMap");
        Dew.log.info("Publishing kubernetes resources");
        deployResources(deployResult);
        Dew.log.debug("Enabled version");
        enabledVersionInfo(gitCommit);
    }

    private void deployResources(Map<String, Object> kubeResources) throws ApiException, IOException {
        V1Service service = (V1Service) kubeResources.get(KubeOpt.RES.SERVICE.getVal());
        ExtensionsV1beta1Deployment deployment = (ExtensionsV1beta1Deployment) kubeResources.get(KubeOpt.RES.DEPLOYMENT.getVal());
        KubeHelper.inst(Dew.Config.getCurrentProject().getId()).apply(deployment);
        CountDownLatch cdl = new CountDownLatch(1);
        ExtensionsV1beta1Deployment deploymentRes = (ExtensionsV1beta1Deployment) kubeResources.get(KubeOpt.RES.DEPLOYMENT.getVal());
        String select = "app="
                + deploymentRes.getMetadata().getName()
                + ",group=" + deploymentRes.getMetadata().getLabels().get("group")
                + ",version=" + deploymentRes.getMetadata().getLabels().get("version");
        String watchId = KubeHelper.inst(Dew.Config.getCurrentProject().getId()).watch(
                (coreApi, extensionsApi, rbacAuthorizationApi, autoscalingApi)
                        -> extensionsApi.listNamespacedDeploymentCall(deploymentRes.getMetadata().getNamespace(),
                        null, null, null, null,
                        select, 1, null, null, Boolean.TRUE, null, null),
                resp -> {
                    // Ready Pod数量是否等于设定的数量
                    if (resp.object.getStatus().getReadyReplicas() != null
                            && resp.object.getStatus().getReadyReplicas().intValue() == resp.object.getSpec().getReplicas()) {
                        try {
                            long runningPodSize = KubeHelper.inst(Dew.Config.getCurrentProject().getId())
                                    .list(select, deploymentRes.getMetadata().getNamespace(), KubeOpt.RES.POD, V1Pod.class)
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
            cdl.await(30, TimeUnit.MINUTES);
            KubeHelper.inst(Dew.Config.getCurrentProject().getId()).stopWatch(watchId);
            if (!KubeHelper.inst(Dew.Config.getCurrentProject().getId()).exist(service.getMetadata().getName(), service.getMetadata().getNamespace(), KubeOpt.RES.SERVICE)) {
                KubeHelper.inst(Dew.Config.getCurrentProject().getId()).create(service);
            } else {
                KubeHelper.inst(Dew.Config.getCurrentProject().getId()).patch(service.getMetadata().getName(), new KubeServiceBuilder().buildPatch(service), service.getMetadata().getNamespace(), KubeOpt.RES.SERVICE);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            Dew.log.error("Publish error,maybe timeout.", e);
            throw new RuntimeException(e);
        }
    }

    private void appendVersionInfo(Map<String, Object> kubeResources, String gitCommit, boolean reRelease) throws ApiException {
        Map<String, String> resources = kubeResources.entrySet()
                .stream().collect(Collectors.toMap(Map.Entry::getKey,
                        entry -> {
                            try {
                                return $.security.encodeStringToBase64(KubeHelper.inst(Dew.Config.getCurrentProject().getId()).toString(entry.getValue()), "UTF-8");
                            } catch (UnsupportedEncodingException ignore) {
                                return "";
                            }
                        }));
        V1ConfigMap currVerConfigMap = new KubeConfigMapBuilder().build(
                getVersionName(gitCommit), new HashMap<String, String>() {{
                    put(FLAG_VERSION_APP, Dew.Config.getCurrentProject().getAppName());
                    put(FLAG_KUBE_RESOURCE_GIT_COMMIT, gitCommit);
                    put(FLAG_VERSION_KIND, "version");
                    put(FLAG_VERSION_ENABLED, "false");
                    put(FLAG_VERSION_LAST_UPDATE_TIME, System.currentTimeMillis() + "");
                    put(FLAG_VERSION_RE_RELEASE, reRelease + "");
                }}, resources);
        KubeHelper.inst(Dew.Config.getCurrentProject().getId()).apply(currVerConfigMap);
    }

    private void enabledVersionInfo(String gitCommit) throws ApiException {
        KubeHelper.inst(Dew.Config.getCurrentProject().getId()).patch(getVersionName(gitCommit), new ArrayList<String>() {{
            add("{\"op\":\"replace\",\"path\":\"/metadata/labels/enabled\",\"value\":\"true\"}");
        }}, Dew.Config.getCurrentProject().getNamespace(), KubeOpt.RES.CONFIG_MAP);
    }

    private void removeOldVersions(int revisionHistoryLimit) throws ApiException, IOException {
        List<V1ConfigMap> verConfigMaps = getVersionHistory(false);
        int offset = revisionHistoryLimit;
        for (V1ConfigMap configMap : verConfigMaps) {
            boolean enabled = configMap.getMetadata().getLabels().get(FLAG_VERSION_ENABLED).equalsIgnoreCase("true");
            if (enabled) {
                offset--;
            }
            if (!enabled || offset <= 0) {
                String oldGitCommit = configMap.getMetadata().getLabels().get(FLAG_KUBE_RESOURCE_GIT_COMMIT);
                Dew.log.debug("Remove old version : " + configMap.getMetadata().getName());
                KubeHelper.inst(Dew.Config.getCurrentProject().getId()).delete(configMap.getMetadata().getName(), Dew.Config.getCurrentProject().getNamespace(), KubeOpt.RES.CONFIG_MAP);
                DockerHelper.inst(Dew.Config.getCurrentProject().getId()).image.remove(Dew.Config.getCurrentProject().getImageName(oldGitCommit));
                DockerHelper.inst(Dew.Config.getCurrentProject().getId()).registry.remove(Dew.Config.getCurrentProject().getImageName(oldGitCommit));
            }
        }
    }

}
