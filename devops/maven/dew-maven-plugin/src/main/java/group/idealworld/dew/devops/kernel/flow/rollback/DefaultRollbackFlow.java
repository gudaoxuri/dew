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

package group.idealworld.dew.devops.kernel.flow.rollback;

import com.ecfront.dew.common.$;
import group.idealworld.dew.devops.kernel.config.FinalProjectConfig;
import group.idealworld.dew.devops.kernel.flow.BasicFlow;
import group.idealworld.dew.devops.kernel.flow.release.KubeReleaseFlow;
import group.idealworld.dew.devops.kernel.function.VersionController;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.models.V1ConfigMap;
import org.apache.commons.lang.StringUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Date;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Default rollback flow.
 *
 * @author gudaoxuri
 */
public class DefaultRollbackFlow extends BasicFlow {

    private boolean history;

    private String version;

    /**
     * DefaultRollbackFlow .
     *
     * @param history history
     * @param version version
     */
    public DefaultRollbackFlow(boolean history, String version) {
        this.history = history;
        this.version = version;
    }

    protected void process(FinalProjectConfig config, String flowBasePath) throws ApiException, IOException {
        String currentAppVersion = VersionController.getAppCurrentVersion(config);
        Map<String, V1ConfigMap> versions = VersionController.getAppVersions(config);

        if (history && StringUtils.isBlank(version)) {
            logFinalCurrentAppVersion(currentAppVersion, versions, "[" + config.getAppShowName() + "] revision history");
            return;
        }

        if (StringUtils.isBlank(version)) {
            logFinalCurrentAppVersion(currentAppVersion, versions, "Please select rollback version");
        }
        String selected = !StringUtils.isBlank(version) ? version : new BufferedReader(new InputStreamReader(System.in)).readLine().trim();

        while (!versions.containsKey(selected) || selected.equalsIgnoreCase(currentAppVersion)) {
            if (version != null && !version.isEmpty()) {
                logger.warn("[" + config.getAppName() + "] version number was illegal,rollback skipped.\n"
                        + "[Tip] It's recommended that assign the project which need to rollback.");
                return;
            } else {
                logger.error("Version number illegal,please re-enter");
                selected = new BufferedReader(new InputStreamReader(System.in)).readLine().trim();
            }
        }
        // 要回滚的版本
        String rollbackAppVersion = VersionController.getAppVersion(versions.get(selected));
        // 调用部署流程执行重新部署
        new KubeReleaseFlow().release(config, rollbackAppVersion);
    }

    void logFinalCurrentAppVersion(String currentAppVersion, Map<String, V1ConfigMap> versions, String message) {
        String sb = "\r\n------------------ " + message + " : ------------------\r\n"
                + versions.entrySet().stream()
                .map(ver -> " < " + ver.getKey() + " > Last update time : "
                        + $.time().yyyy_MM_dd_HH_mm_ss_SSS.format(
                        new Date(VersionController.getLastUpdateTime(ver.getValue())))
                        + (currentAppVersion != null && currentAppVersion.equalsIgnoreCase(ver.getKey()) ? " [Online]" : ""))
                .collect(Collectors.joining("\r\n"))
                + "\r\n----------------------------------------------------------------------\r\n";
        logger.info(sb);
    }

}
