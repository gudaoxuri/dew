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

package ms.dew.devops.kernel.flow;

import io.kubernetes.client.ApiException;
import io.kubernetes.client.models.V1ConfigMap;
import ms.dew.devops.helper.KubeHelper;
import ms.dew.devops.helper.KubeRES;
import ms.dew.devops.kernel.Dew;
import ms.dew.devops.kernel.config.FinalProjectConfig;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Basic flow.
 *
 * @author gudaoxuri
 */
public abstract class BasicFlow {

    public static final String FLAG_KUBE_RESOURCE_GIT_COMMIT = "dew.ms/git-commit";


    protected static final String FLAG_VERSION_APP = "app";
    protected static final String FLAG_VERSION_KIND = "kind";
    protected static final String FLAG_VERSION_LAST_UPDATE_TIME = "lastUpdateTime";
    protected static final String FLAG_VERSION_RE_RELEASE = "re-release";
    protected static final String FLAG_VERSION_ENABLED = "enabled";

    /**
     * 执行流程.
     *
     * @param mojoName the mojo name
     * @return the process result
     * @throws ApiException the api exception
     * @throws IOException  the io exception
     */
    public final boolean exec(String mojoName) throws ApiException, IOException {
        FinalProjectConfig config = Dew.Config.getCurrentProject();
        Dew.log.debug("Executing " + this.getClass().getSimpleName());
        // 为每个mojo创建输出目录
        String flowBasePath = config.getMvnTargetDirectory() + "dew_" + mojoName + File.separator;
        Files.createDirectories(Paths.get(flowBasePath));
        if (!preProcess(config, flowBasePath)) {
            Dew.log.debug("Finished,because [preProcess] is false");
            return false;
        }
        if (!process(config, flowBasePath)) {
            Dew.log.debug("Finished,because [process] is false");
            return false;
        }
        if (!postProcess(config, flowBasePath)) {
            Dew.log.debug("Finished,because [postProcess] is false");
            return false;
        }
        return true;
    }

    /**
     * Process.
     *
     * @param config       the project config
     * @param flowBasePath the flow base path
     * @return the process result
     * @throws ApiException the api exception
     * @throws IOException  the io exception
     */
    protected abstract boolean process(FinalProjectConfig config, String flowBasePath) throws ApiException, IOException;

    /**
     * Pre process.
     *
     * @param config       the project config
     * @param flowBasePath the flow base path
     * @return the boolean
     * @throws ApiException the api exception
     * @throws IOException  the io exception
     */
    protected boolean preProcess(FinalProjectConfig config, String flowBasePath) throws ApiException, IOException {
        return true;
    }

    /**
     * Post process.
     *
     * @param config       the project config
     * @param flowBasePath the flow base path
     * @return the process result
     * @throws ApiException the api exception
     * @throws IOException  the io exception
     */
    protected boolean postProcess(FinalProjectConfig config, String flowBasePath) throws ApiException, IOException {
        return true;
    }

    /**
     * 从kubernetes中获取历史版本列表.
     *
     * @param config      the project config
     * @param onlyEnabled the only enabled
     * @return the version history
     * @throws ApiException the api exception
     */
    protected List<V1ConfigMap> getVersionHistory(FinalProjectConfig config, boolean onlyEnabled) throws ApiException {
        List<V1ConfigMap> versions = KubeHelper.inst(config.getId()).list(
                FLAG_VERSION_APP + "=" + config.getAppName() + "," + FLAG_VERSION_KIND + "=version",
                config.getNamespace(),
                KubeRES.CONFIG_MAP, V1ConfigMap.class);
        // 按时间倒序
        versions.sort((m1, m2) ->
                Long.valueOf(m2.getMetadata().getLabels().get(FLAG_VERSION_LAST_UPDATE_TIME))
                        .compareTo(Long.valueOf(m1.getMetadata().getLabels().get(FLAG_VERSION_LAST_UPDATE_TIME))));
        if (onlyEnabled) {
            return versions.stream()
                    .filter(cm -> cm.getMetadata().getLabels().get(FLAG_VERSION_ENABLED).equalsIgnoreCase("true"))
                    .collect(Collectors.toList());
        } else {
            return versions;
        }
    }

    /**
     * 从kubernetes中获取指定的历史版本.
     *
     * @param config    the project config
     * @param gitCommit the git commit
     * @return the old version
     * @throws ApiException the api exception
     */
    protected V1ConfigMap getOldVersion(FinalProjectConfig config, String gitCommit) throws ApiException {
        V1ConfigMap oldVersion = KubeHelper.inst(config.getId())
                .read(getVersionName(config, gitCommit),
                        config.getNamespace(),
                        KubeRES.CONFIG_MAP, V1ConfigMap.class);
        if (oldVersion != null && oldVersion.getMetadata().getLabels().get(FLAG_VERSION_ENABLED).equalsIgnoreCase("true")) {
            return oldVersion;
        } else {
            return null;
        }
    }

    /**
     * Get version name.
     *
     * @param config    the project config
     * @param gitCommit the git commit
     * @return the version name
     */
    protected String getVersionName(FinalProjectConfig config, String gitCommit) {
        return "ver." + config.getAppName() + "." + gitCommit;
    }

}
