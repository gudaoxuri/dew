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

package ms.dew.devops.kernel.function;

import io.kubernetes.client.ApiException;
import io.kubernetes.client.models.V1ConfigMap;
import io.kubernetes.client.models.V1Service;
import ms.dew.devops.helper.KubeHelper;
import ms.dew.devops.helper.KubeRES;
import ms.dew.devops.kernel.config.FinalProjectConfig;
import ms.dew.devops.kernel.resource.KubeConfigMapBuilder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
    public static final String FLAG_KUBE_RESOURCE_GIT_COMMIT = "dew.ms/git-commit";

    private static final String FLAG_VERSION_APP = "app";
    private static final String FLAG_VERSION_KIND = "kind";
    private static final String FLAG_VERSION_LAST_UPDATE_TIME = "lastUpdateTime";
    private static final String FLAG_VERSION_RE_RELEASE = "re-release";
    private static final String FLAG_VERSION_ENABLED = "enabled";

    /**
     * Add new version.
     *
     * @param config       the config
     * @param gitCommit    the git commit
     * @param reRelease    the re release
     * @param data         the data
     * @param appendLabels the append labels
     * @throws ApiException the api exception
     */
    public static void addNewVersion(FinalProjectConfig config, String gitCommit,
                                     boolean reRelease, Map<String, String> data, Map<String, String> appendLabels)
            throws ApiException {
        Map<String, String> labels = new HashMap<String, String>() {
            {
                put(FLAG_VERSION_APP, config.getAppName());
                put(FLAG_KUBE_RESOURCE_GIT_COMMIT, gitCommit);
                put(FLAG_VERSION_KIND, "version");
                put(FLAG_VERSION_ENABLED, "true");
                put(FLAG_VERSION_LAST_UPDATE_TIME, System.currentTimeMillis() + "");
                put(FLAG_VERSION_RE_RELEASE, reRelease + "");
            }
        };
        labels.putAll(appendLabels);
        V1ConfigMap currVerConfigMap = new KubeConfigMapBuilder().build(
                getVersionName(config, gitCommit), config.getNamespace(), labels, data);
        KubeHelper.inst(config.getId()).apply(currVerConfigMap);
    }

    /**
     * Has version.
     *
     * @param config      the config
     * @param gitCommit   the git commit
     * @param onlyEnabled the only enabled
     * @return the boolean
     * @throws ApiException the api exception
     */
    public static boolean hasVersion(FinalProjectConfig config, String gitCommit, boolean onlyEnabled) throws ApiException {
        return getVersion(config, gitCommit, onlyEnabled) != null;
    }

    /**
     * 从kubernetes中获取指定的版本.
     *
     * @param config      the project config
     * @param gitCommit   the git commit
     * @param onlyEnabled the only enabled
     * @return the old version
     * @throws ApiException the api exception
     */
    public static V1ConfigMap getVersion(FinalProjectConfig config, String gitCommit, boolean onlyEnabled) throws ApiException {
        V1ConfigMap oldVersion = KubeHelper.inst(config.getId())
                .read(getVersionName(config, gitCommit),
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
     * @param config      the project config
     * @param onlyEnabled the only enabled
     * @return the version history
     * @throws ApiException the api exception
     */
    public static List<V1ConfigMap> getVersionHistory(FinalProjectConfig config, boolean onlyEnabled) throws ApiException {
        List<V1ConfigMap> versions = KubeHelper.inst(config.getId()).list(
                FLAG_VERSION_APP + "=" + config.getAppName() + "," + FLAG_VERSION_KIND + "=version",
                config.getNamespace(),
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
     * @param config      the project config
     * @param onlyEnabled the only enabled
     * @return the version
     * @throws ApiException the api exception
     */
    public static V1ConfigMap getLastVersion(FinalProjectConfig config, boolean onlyEnabled) throws ApiException {
        List<V1ConfigMap> versions = getVersionHistory(config, onlyEnabled);
        if (versions.size() == 0) {
            return null;
        } else {
            return versions.get(0);
        }
    }

    /**
     * 从kubernetes中删除版本.
     *
     * @param config    the project config
     * @param gitCommit the git commit
     * @throws ApiException the api exception
     */
    public static void deleteVersion(FinalProjectConfig config, String gitCommit) throws ApiException {
        V1ConfigMap version = getVersion(config, gitCommit, false);
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
     * Gets git commit.
     *
     * @param service the service
     * @return the git commit
     */
    public static String getGitCommit(V1Service service) {
        if (service == null) {
            return null;
        }
        return service.getMetadata().getAnnotations().get(FLAG_KUBE_RESOURCE_GIT_COMMIT);
    }

    /**
     * Gets git commit.
     *
     * @param versionMap the version
     * @return the git commit
     */
    public static String getGitCommit(V1ConfigMap versionMap) {
        if (versionMap == null) {
            return null;
        }
        return versionMap.getMetadata().getLabels().get(FLAG_KUBE_RESOURCE_GIT_COMMIT);
    }

    /**
     * Gets last update time.
     *
     * @param versionMap the version
     * @return the last update time
     */
    public static Long getLastUpdateTime(V1ConfigMap versionMap) {
        if (versionMap == null) {
            return null;
        }
        return Long.valueOf(versionMap.getMetadata().getLabels().get(FLAG_VERSION_LAST_UPDATE_TIME));
    }

    /**
     * Get version name.
     *
     * @param config    the project config
     * @param gitCommit the git commit
     * @return the version name
     */
    public static String getVersionName(FinalProjectConfig config, String gitCommit) {
        return "ver." + config.getAppName() + "." + gitCommit;
    }


}
