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

package com.tairanchina.csp.dew.kernel.function;

import com.ecfront.dew.common.$;
import com.tairanchina.csp.dew.helper.GitHelper;
import com.tairanchina.csp.dew.helper.KubeHelper;
import com.tairanchina.csp.dew.kernel.Dew;
import com.tairanchina.csp.dew.kernel.config.FinalProjectConfig;
import io.kubernetes.client.ApiException;
import io.kubernetes.client.models.V1Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

import static com.tairanchina.csp.dew.kernel.flow.BasicFlow.FLAG_KUBE_RESOURCE_GIT_COMMIT;

public class NeedExecuteByGit {

    private static final AtomicBoolean initialized = new AtomicBoolean(false);

    public static void setNeedExecuteProjects() throws ApiException {
        if (initialized.getAndSet(true)) {
            return;
        }
        // TODO submodule
        Dew.log.info("Fetch need process projects");
        for (FinalProjectConfig config : Dew.Config.getProjects().values()) {
            Dew.log.debug("Execute need checking for" + config.getAppName());
            String lastVersionDeployCommit = fetchLastVersionDeployCommit(config);
            Dew.log.debug("Latest commit is " + lastVersionDeployCommit);
            // 判断有没有发过版本
            if (lastVersionDeployCommit != null) {
                List<String> changedFiles = fetchGitDiff(lastVersionDeployCommit, Dew.basicDirectory);
                // 判断有没有代码变更
                if (!hasUnDeployFiles(changedFiles, config)) {
                    config.setSkip(true);
                }
            }
        }
        Dew.log.info("===============================================");
        Dew.log.info("============= Processing Projects =============");
        Dew.Config.getProjects().values().stream()
                .filter(config -> !config.isSkip())
                .forEach(config ->
                        Dew.log.info("||" + config.getMvnGroupId() + ":" + config.getMvnArtifactId()));
        Dew.log.info("===============================================");
    }

    private static String fetchLastVersionDeployCommit(FinalProjectConfig config) throws ApiException {
        V1Service lastVersionService = KubeHelper.read(config.getAppName(), config.getNamespace(), KubeHelper.RES.SERVICE, V1Service.class, config.getId());
        if (lastVersionService == null) {
            return null;
        } else {
            return lastVersionService.getMetadata().getAnnotations().get(FLAG_KUBE_RESOURCE_GIT_COMMIT);
        }
    }

    private static List<String> fetchGitDiff(String lastVersionDeployCommit, String gitDirectory) {
        List<String> changedFiles = GitHelper.diff(lastVersionDeployCommit, "HEAD", gitDirectory);
        Dew.log.debug("Change files:");
        Dew.log.debug("-------------------");
        changedFiles.forEach(file -> Dew.log.debug(">>" + file));
        Dew.log.debug("-------------------");
        return changedFiles;
    }

    private static boolean hasUnDeployFiles(List<String> changedFiles, FinalProjectConfig projectConfig) {
        String projectPath = projectConfig.getMvnDirectory().substring(Dew.basicDirectory.length()).replaceAll("\\\\", "/");
        changedFiles = changedFiles.stream()
                .filter(file -> file.startsWith(projectPath))
                .collect(Collectors.toList());
        Dew.log.debug("Found " + changedFiles.size() + " changed files ");
        if (changedFiles.isEmpty()) {
            return false;
        } else if (!projectConfig.getApp().getIgnoreChangeFiles().isEmpty()) {
            if (!$.file.noneMath(changedFiles, new ArrayList<>(projectConfig.getApp().getIgnoreChangeFiles()))) {
                Dew.log.debug("Found 0 changed files filter ignore files");
                return false;
            }
        }
        return true;
    }

}
