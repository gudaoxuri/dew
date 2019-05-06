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

package ms.dew.devops.kernel.flow.rollback;

import com.ecfront.dew.common.$;
import io.kubernetes.client.ApiException;
import io.kubernetes.client.models.V1ConfigMap;
import io.kubernetes.client.models.V1Service;
import ms.dew.devops.helper.KubeHelper;
import ms.dew.devops.helper.KubeRES;
import ms.dew.devops.kernel.Dew;
import ms.dew.devops.kernel.config.FinalProjectConfig;
import ms.dew.devops.kernel.flow.BasicFlow;
import ms.dew.devops.kernel.flow.release.KubeReleaseFlow;
import ms.dew.devops.kernel.function.VersionController;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Default rollback flow.
 *
 * @author gudaoxuri
 */
public class DefaultRollbackFlow extends BasicFlow {

    protected void process(FinalProjectConfig config, String flowBasePath) throws ApiException, IOException {
        V1Service service = KubeHelper.inst(config.getId()).read(config.getAppName(), config.getNamespace(), KubeRES.SERVICE, V1Service.class);
        String currentAppVersion = null;
        if (service != null) {
            currentAppVersion = VersionController.getAppVersion(service);
        }
        Map<String, V1ConfigMap> versions = VersionController.getVersionHistory(config, true).stream()
                .collect(Collectors
                        .toMap(VersionController::getAppVersion, ver -> ver,
                                (v1, v2) -> v1, LinkedHashMap::new));
        String finalCurrentAppVersion = currentAppVersion;
        String sb = "\r\n------------------ Please select rollback version : ------------------\r\n"
                + versions.entrySet().stream()
                .map(ver -> " < " + ver.getKey() + " > Last update time : "
                        + $.time().yyyy_MM_dd_HH_mm_ss_SSS.format(
                        new Date(VersionController.getLastUpdateTime(ver.getValue())))
                        + (finalCurrentAppVersion != null && finalCurrentAppVersion.equalsIgnoreCase(ver.getKey()) ? " [Online]" : ""))
                .collect(Collectors.joining("\r\n"))
                + "\r\n---------------------------------------------------------------------\r\n";
        Dew.log.info(sb);
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        String selected = reader.readLine().trim();
        while (!versions.containsKey(selected) || selected.equalsIgnoreCase(finalCurrentAppVersion)) {
            Dew.log.error("Version number illegal,please re-enter");
            reader = new BufferedReader(new InputStreamReader(System.in));
            selected = reader.readLine().trim();
        }
        // 要回滚的版本
        String rollbackAppVersion = VersionController.getAppVersion(versions.get(selected));
        // 调用部署流程执行重新部署
        new KubeReleaseFlow().release(config, rollbackAppVersion);
    }

}
