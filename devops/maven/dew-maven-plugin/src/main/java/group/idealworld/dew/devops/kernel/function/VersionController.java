/*
 * Copyright 2022. the original author or authors
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

package group.idealworld.dew.devops.kernel.function;

import group.idealworld.dew.devops.kernel.config.FinalProjectConfig;
import group.idealworld.dew.devops.kernel.helper.KubeHelper;
import group.idealworld.dew.devops.kernel.helper.KubeRES;
import group.idealworld.dew.devops.kernel.resource.KubeConfigMapBuilder;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.models.V1ConfigMap;
import io.kubernetes.client.openapi.models.V1Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Version controller.
 *
 * @author gudaoxuri
 */
public class VersionController {

    /**
     * The constant FLAG_KUBE_RESOURCE_GIT_COMMIT.
     */
    public static final String FLAG_KUBE_RESOURCE_GIT_COMMIT = "dew.idealworld.group/git-commit";

    /**
     * The constant FLAG_KUBE_RESOURCE_APP_VERSION.
     */
    public static final String FLAG_KUBE_RESOURCE_APP_VERSION = "version";

    private static final String FLAG_VERSION_APP = "app";
    private static final String FLAG_VERSION_KIND = "kind";
    private static final String FLAG_VERSION_LAST_UPDATE_TIME = "lastUpdateTime";
    private static final String FLAG_VERSION_RE_RELEASE = "re-release";
    private static final String FLAG_VERSION_ENABLED = "enabled";

    /**
     * Add new version.
     *
     * @param config       the config
     * @param appVersion   the app version
     * @param gitCommit    the git commit
     * @param reRelease    the re release
     * @param data         the data
     * @param appendLabels the append labels
     * @throws ApiException the api exception
     */
    public static void addNewVersion(FinalProjectConfig config, String appVersion, String gitCommit,
                                     boolean reRelease, Map<String, String> data, Map<String, String> appendLabels)
            throws ApiException {
        Map<String, String> labels = new HashMap<String, String>() {
            {
                put(FLAG_VERSION_APP, config.getAppName());
                put(FLAG_KUBE_RESOURCE_APP_VERSION, appVersion);
                put(FLAG_KUBE_RESOURCE_GIT_COMMIT, gitCommit);
                put(FLAG_VERSION_KIND, "version");
                put(FLAG_VERSION_ENABLED, "true");
                put(FLAG_VERSION_LAST_UPDATE_TIME, System.currentTimeMillis() + "");
                put(FLAG_VERSION_RE_RELEASE, reRelease + "");
            }
        };
        labels.putAll(appendLabels);
        V1ConfigMap currVerConfigMap = new KubeConfigMapBuilder().build(
                getVersionName(config, appVersion), config.getNamespace(), labels, data);
        KubeHelper.inst(config.getId()).apply(currVerConfigMap);
    }

    /**
     * Has version.
     *
     * @param config      the config
     * @param appVersion  the app version
     * @param onlyEnabled the only enabled
     * @return the boolean
     * @throws ApiException the api exception
     */
    public static boolean hasVersion(FinalProjectConfig config, String appVersion, boolean onlyEnabled) throws ApiException {
        return getVersion(config, appVersion, onlyEnabled) != null;
    }

    /**
     * 从kubernetes中获取指定的版本.
     *
     * @param config      the project config
     * @param appVersion  the app version
     * @param onlyEnabled the only enabled
     * @return the old version
     * @throws ApiException the api exception
     */
    public static V1ConfigMap getVersion(FinalProjectConfig config, String appVersion, boolean onlyEnabled) throws ApiException {
        V1ConfigMap oldVersion = KubeHelper.inst(config.getId())
                .read(getVersionName(config, appVersion),
                        config.getNamespace(),
                        KubeRES.CONFIG_MAP, V1ConfigMap.class);
        if (oldVersion != null && (!onlyEnabled || oldVersion.getMetadata().getLabels().get(FLAG_VERSION_ENABLED).equalsIgnoreCase("true"))) {
            return oldVersion;
        } else {
            return null;
        }
    }


    /**
     * 从kubernetes中获取历史版本列表.
     *
     * @param projectId   the project id
     * @param appName     the app name
     * @param namespace   the namespace
     * @param onlyEnabled the only enabled
     * @return the version history
     * @throws ApiException the api exception
     */
    public static List<V1ConfigMap> getVersionHistory(String projectId, String appName, String namespace, boolean onlyEnabled) throws ApiException {
        List<V1ConfigMap> versions = KubeHelper.inst(projectId).list(
                FLAG_VERSION_APP + "=" + appName + "," + FLAG_VERSION_KIND + "=version",
                namespace,
                KubeRES.CONFIG_MAP, V1ConfigMap.class);
        if (onlyEnabled) {
            versions = versions.stream()
                    .filter(cm -> cm.getMetadata().getLabels().get(FLAG_VERSION_ENABLED).equalsIgnoreCase("true"))
                    .collect(Collectors.toList());
        }
        // 按时间倒序
        if (versions.size() > 1) {
            versions.sort((m1, m2) ->
                    Long.valueOf(m2.getMetadata().getLabels().get(FLAG_VERSION_LAST_UPDATE_TIME))
                            .compareTo(Long.valueOf(m1.getMetadata().getLabels().get(FLAG_VERSION_LAST_UPDATE_TIME))));
        }
        return versions;
    }

    /**
     * 从kubernetes中获取最新版本.
     *
     * @param projectId   the project id
     * @param appName     the app name
     * @param namespace   the namespace
     * @param onlyEnabled the only enabled
     * @return the version
     * @throws ApiException the api exception
     */
    public static Optional<V1ConfigMap> getLastVersion(String projectId, String appName, String namespace, boolean onlyEnabled) throws ApiException {
        List<V1ConfigMap> versions = getVersionHistory(projectId, appName, namespace, onlyEnabled);
        if (versions.size() == 0) {
            return Optional.empty();
        } else {
            return Optional.of(versions.get(0));
        }
    }

    /**
     * 从kubernetes中删除版本.
     *
     * @param config     the project config
     * @param appVersion the app version
     * @throws ApiException the api exception
     */
    public static void deleteVersion(FinalProjectConfig config, String appVersion) throws ApiException {
        V1ConfigMap version = getVersion(config, appVersion, false);
        if (version != null) {
            KubeHelper.inst(config.getId()).delete(version.getMetadata().getName(), version.getMetadata().getNamespace(), KubeRES.CONFIG_MAP);
        }
    }

    /**
     * Is version enabled.
     *
     * @param versionMap the version
     * @return the boolean
     */
    public static boolean isVersionEnabled(V1ConfigMap versionMap) {
        if (versionMap == null) {
            return false;
        }
        return versionMap.getMetadata().getLabels()
                .get(VersionController.FLAG_VERSION_ENABLED).equalsIgnoreCase("true");
    }

    /**
     * Gets app version.
     *
     * @param service the service
     * @return the app version
     */
    public static String getAppVersion(V1Service service) {
        return service.getMetadata().getLabels().get(FLAG_KUBE_RESOURCE_APP_VERSION);
    }

    /**
     * Gets app version.
     *
     * @param versionMap the version
     * @return the app version
     */
    public static String getAppVersion(V1ConfigMap versionMap) {
        return versionMap.getMetadata().getLabels().get(FLAG_KUBE_RESOURCE_APP_VERSION);
    }

    /**
     * Gets app current version.
     *
     * @param config the FinalProjectConfig
     * @return the app current version
     * @throws ApiException the api exception
     */
    public static String getAppCurrentVersion(FinalProjectConfig config) throws ApiException {
        V1Service service = KubeHelper.inst(config.getId()).read(config.getAppName(), config.getNamespace(), KubeRES.SERVICE, V1Service.class);
        return service != null ? VersionController.getAppVersion(service) : null;
    }

    /**
     * Gets app versions.
     *
     * @param config the FinalProjectConfig
     * @return the app version
     * @throws ApiException the api exception
     */
    public static Map<String, V1ConfigMap> getAppVersions(FinalProjectConfig config) throws ApiException {
        return VersionController
                .getVersionHistory(config.getId(), config.getAppName(), config.getNamespace(), true).stream()
                .collect(Collectors
                        .toMap(VersionController::getAppVersion, ver -> ver,
                                (v1, v2) -> v1, LinkedHashMap::new));
    }

    /**
     * Gets git commit.
     *
     * @param versionMap the version
     * @return the git commit
     */
    public static String getGitCommit(V1ConfigMap versionMap) {
        return versionMap.getMetadata().getLabels().get(FLAG_KUBE_RESOURCE_GIT_COMMIT);
    }

    /**
     * Gets last update time.
     *
     * @param versionMap the version
     * @return the last update time
     */
    public static Long getLastUpdateTime(V1ConfigMap versionMap) {
        return Long.valueOf(versionMap.getMetadata().getLabels().get(FLAG_VERSION_LAST_UPDATE_TIME));
    }

    /**
     * Get version name.
     *
     * @param config     the project config
     * @param appVersion the app version
     * @return the version name
     */
    public static String getVersionName(FinalProjectConfig config, String appVersion) {
        return "ver." + config.getAppName() + "." + appVersion;
    }


}
