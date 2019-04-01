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
import ms.dew.devops.helper.KubeOpt;
import ms.dew.devops.kernel.Dew;
import ms.dew.devops.kernel.flow.BasicFlow;
import ms.dew.devops.kernel.flow.release.DefaultReleaseFlow;
import org.apache.maven.plugin.MojoExecutionException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class DefaultRollbackFlow extends BasicFlow {

    protected boolean process(String flowBasePath) throws ApiException, IOException, MojoExecutionException {
        V1Service service = KubeHelper.inst(Dew.Config.getCurrentProject().getId()).read(Dew.Config.getCurrentProject().getAppName(), Dew.Config.getCurrentProject().getNamespace(), KubeOpt.RES.SERVICE, V1Service.class);
        String currentGitCommit = null;
        if (service != null) {
            currentGitCommit = service.getMetadata().getAnnotations().get(FLAG_KUBE_RESOURCE_GIT_COMMIT);
        }
        Map<String, V1ConfigMap> versions = getVersionHistory(true).stream()
                .collect(Collectors
                        .toMap(ver -> ver.getMetadata().getLabels().get(FLAG_KUBE_RESOURCE_GIT_COMMIT), ver -> ver,
                                (v1, v2) -> v1, LinkedHashMap::new));
        String finalCurrentGitCommit = currentGitCommit;
        String sb = "\r\n------------------ Please select rollback version : ------------------\r\n"
                + versions.entrySet().stream()
                .map(ver -> " < " + ver.getKey() + " > Last update time : "
                        + $.time().yyyy_MM_dd_HH_mm_ss_SSS.format(new Date(Long.valueOf(ver.getValue().getMetadata().getLabels().get(FLAG_VERSION_LAST_UPDATE_TIME))))
                        + (finalCurrentGitCommit != null && finalCurrentGitCommit.equalsIgnoreCase(ver.getKey()) ? " [Online]" : ""))
                .collect(Collectors.joining("\r\n"))
                + "\r\n---------------------------------------------------------------------\r\n";
        Dew.log.info(sb);
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        String selected = reader.readLine().trim();
        while (!versions.containsKey(selected) || selected.equalsIgnoreCase(finalCurrentGitCommit)) {
            Dew.log.error("Version number illegal,please re-enter");
            reader = new BufferedReader(new InputStreamReader(System.in));
            selected = reader.readLine().trim();
        }
        String rollbackGitCommit = versions.get(selected).getMetadata().getLabels().get(FLAG_KUBE_RESOURCE_GIT_COMMIT);
        new DefaultReleaseFlow().release(rollbackGitCommit);
        return true;
    }

}
