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

package group.idealworld.dew.devops.kernel.plugin.deploy.maven;

import com.ecfront.dew.common.$;
import com.ecfront.dew.common.Resp;
import group.idealworld.dew.devops.kernel.config.FinalProjectConfig;
import group.idealworld.dew.devops.kernel.exception.ProjectProcessException;
import group.idealworld.dew.devops.kernel.function.VersionController;
import group.idealworld.dew.devops.kernel.plugin.deploy.DeployPlugin;
import io.kubernetes.client.openapi.ApiException;
import org.apache.maven.project.MavenProject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Maven deploy plugin.
 *
 * @author gudaoxuri
 */
public class MavenDeployPlugin implements DeployPlugin {

    @Override
    public Resp<String> deployAble(FinalProjectConfig projectConfig) {
        MavenProject mavenProject = projectConfig.getMavenProject();
        String version = mavenProject.getVersion();
        if (version.trim().toLowerCase().endsWith("snapshot")) {
            // 如果快照仓库存在
            if (mavenProject.getDistributionManagement() == null
                    || mavenProject.getDistributionManagement().getSnapshotRepository() == null
                    || mavenProject.getDistributionManagement().getSnapshotRepository().getUrl() == null
                    || mavenProject.getDistributionManagement().getSnapshotRepository().getUrl().trim().isEmpty()) {
                return Resp.forbidden("Maven distribution snapshot repository not found");
            }
            // SNAPSHOT每次都要发
            return Resp.success("");
        } else if (mavenProject.getDistributionManagement() == null
                || mavenProject.getDistributionManagement().getRepository() == null
                || mavenProject.getDistributionManagement().getRepository().getUrl() == null
                || mavenProject.getDistributionManagement().getRepository().getUrl().trim().isEmpty()) {
            // 处理非快照版
            return Resp.forbidden("Maven distribution repository not found");
        }
        String repoUrl = mavenProject.getDistributionManagement().getRepository().getUrl().trim();
        // TODO auth
        repoUrl = repoUrl.endsWith("/") ? repoUrl : repoUrl + "/";
        repoUrl += mavenProject.getGroupId().replaceAll("\\.", "/")
                + "/"
                + mavenProject.getArtifactId()
                + "/"
                + version
                + "/maven-metadata.xml";
        if ($.http.getWrap(repoUrl).statusCode == 200) {
            return Resp.forbidden("The current version already exists");
        }
        return Resp.success("");
    }

    @Override
    public Optional<String> fetchLastDeployedVersion(String projectId, String appName, String namespace) {
        try {
            return VersionController.getLastVersion(projectId, appName, namespace, true)
                    .map(VersionController::getAppVersion);
        } catch (ApiException e) {
            throw new ProjectProcessException(e.getMessage(), e);
        }
    }

    @Override
    public Optional<String> fetchLastDeployedVersionByReuseProfile(FinalProjectConfig projectConfig) throws IOException {
        // TODO
        return Optional.empty();
    }

    @Override
    public Map<String, String> getEnv(FinalProjectConfig projectConfig) {
        return new HashMap<>();
    }

    @Override
    public boolean useMavenProcessingMode() {
        return true;
    }
}
