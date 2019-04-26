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

import com.ecfront.dew.common.$;
import io.kubernetes.client.ApiException;
import io.kubernetes.client.models.V1Service;
import ms.dew.devops.helper.GitHelper;
import ms.dew.devops.helper.KubeHelper;
import ms.dew.devops.helper.KubeRES;
import ms.dew.devops.kernel.Dew;
import ms.dew.devops.kernel.config.FinalProjectConfig;
import org.apache.maven.project.MavenProject;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

/**
 * Need process checker.
 * <p>
 * 仅用于部署/回滚流程
 *
 * @author gudaoxuri
 */
public class NeedProcessChecker {

    private static final AtomicBoolean initialized = new AtomicBoolean(false);

    /**
     * Check need process projects.
     *
     * @param quiet the quiet
     * @throws ApiException the api exception
     * @throws IOException  the io exception
     */
    public static void checkNeedProcessProjects(boolean quiet) throws ApiException, IOException {
        if (initialized.getAndSet(true)) {
            // 初始化后每次都会调用
            switch (Dew.Config.getCurrentProject().getKind()) {
                case POM:
                case JVM_LIB:
                    // 启用 install 与 deploy 由 Maven 自行执行部署
                    Dew.Config.getMavenProperties().setProperty("maven.install.skip", "false");
                    Dew.Config.getMavenProperties().setProperty("maven.deploy.skip", "false");
                    break;
                default:
                    // 禁用 install 与 deploy
                    Dew.Config.getMavenProperties().setProperty("maven.install.skip", "true");
                    Dew.Config.getMavenProperties().setProperty("maven.deploy.skip", "true");
            }
            return;
        }
        // 初始化，全局只调用一次
        Dew.log.info("Fetch need process projects");
        for (FinalProjectConfig config : Dew.Config.getProjects().values()) {
            Dew.log.info("Need process checking for " + config.getAppName());
            switch (config.getKind()) {
                case POM:
                case JVM_LIB:
                    checkNeedProcessByMavenRepo(config);
                    break;
                default:
                    checkNeedProcessByGit(config);
            }
        }
        List<FinalProjectConfig> processingProjects = Dew.Config.getProjects().values().stream()
                .filter(config -> !config.isSkip())
                .collect(Collectors.toList());
        if (processingProjects.isEmpty()) {
            // 不存在需要处理的项目
            Dew.stopped = true;
            Dew.log.info("No project found to be processed");
            ExecuteEventProcessor.onShutdown(Dew.Config.getProjects());
            return;
        }
        // 提示将要处理的项目
        StringBuilder sb = new StringBuilder();
        sb.append("\r\n==================== Processing Projects =====================\r\n\r\n");
        sb.append(processingProjects.stream().map(config ->
                "> [" + config.getKind().name() + "] " + config.getMvnGroupId() + ":" + config.getMvnArtifactId())
                .collect(Collectors.joining("\r\n")));
        sb.append("\r\n\r\n==============================================================\r\n");
        if (quiet) {
            Dew.log.info(sb.toString());
        } else {
            // 非静默模式，用户选择是否继续
            sb.append("\r\n< Y > or < N >");
            Dew.log.info(sb.toString());
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            if (reader.readLine().trim().equalsIgnoreCase("N")) {
                Dew.stopped = true;
                Dew.log.info("Process canceled");
                return;
            }
        }
    }


    /**
     * Check need process by maven repo.
     * <p>
     * 通过Maven仓库判断是否需要处理，多用于处理 类库 和 pom 类型的项目
     *
     * @param config the config
     * @throws ApiException the api exception
     * @throws IOException  the io exception
     */
    private static void checkNeedProcessByMavenRepo(FinalProjectConfig config) throws ApiException, IOException {
        MavenProject mavenProject = Dew.Config.getMavenProject(config.getId());
        String version = mavenProject.getVersion();
        if (version.trim().toLowerCase().endsWith("snapshot")) {
            // 如果快照仓库存在
            if (mavenProject.getDistributionManagement() == null
                    || mavenProject.getDistributionManagement().getSnapshotRepository() == null
                    || mavenProject.getDistributionManagement().getSnapshotRepository().getUrl() == null
                    || mavenProject.getDistributionManagement().getSnapshotRepository().getUrl().trim().isEmpty()) {
                Dew.log.warn("Maven distribution snapshot repository not found");
                config.skip("Maven distribution snapshot repository not found", false);
                return;
            }
        } else if (mavenProject.getDistributionManagement() == null
                || mavenProject.getDistributionManagement().getRepository() == null
                || mavenProject.getDistributionManagement().getRepository().getUrl() == null
                || mavenProject.getDistributionManagement().getRepository().getUrl().trim().isEmpty()) {
            // 处理非快照版
            Dew.log.warn("Maven distribution repository not found");
            config.skip("Maven distribution repository not found", false);
            return;
        }
        if (config.isCustomVersion()) {
            return;
        }
        String lastVersionDeployCommit = VersionController.getGitCommit(VersionController.getLastVersion(config, true));
        Dew.log.debug("Latest version is " + lastVersionDeployCommit);
        // 判断有没有发过版本
        if (lastVersionDeployCommit != null) {
            List<String> changedFiles = fetchGitDiff(lastVersionDeployCommit);
            // 判断有没有代码变更
            if (!hasUnDeployFiles(changedFiles, config)) {
                config.skip("No code changes", false);
            }
        }
    }

    /**
     * Check need process by git.
     * <p>
     * 通过当前Git commit版本与kubernetes上部署的最新的commit版本判断是否需要处理
     *
     * @param config the config
     * @throws ApiException the api exception
     */
    private static void checkNeedProcessByGit(FinalProjectConfig config) throws ApiException {
        // kubernetes上部署的最新的commit版本
        String lastVersionDeployCommit = fetchLastVersionDeployCommit(config.getId(), config.getAppName(), config.getNamespace());
        if (!config.getDisableReuseVersion()) {
            // 重用版本
            String lastVersionDeployCommitFromProfile =
                    fetchLastVersionDeployCommit(config.getId() + "-append", config.getAppName(), config.getAppendProfile().getNamespace());
            if (lastVersionDeployCommit != null && lastVersionDeployCommit.equals(lastVersionDeployCommitFromProfile)) {
                Dew.log.warn("Reuse last version " + lastVersionDeployCommit + " has been deployed");
                config.skip("Reuse last version " + lastVersionDeployCommit + " has been deployed", false);
            } else {
                Dew.log.info("Reuse last version " + lastVersionDeployCommitFromProfile + " from " + config.getReuseLastVersionFromProfile());
                config.setGitCommit(lastVersionDeployCommitFromProfile);
            }
        } else if (!config.isCustomVersion()) {
            Dew.log.debug("Latest version is " + lastVersionDeployCommit);
            // 判断有没有发过版本
            if (lastVersionDeployCommit != null) {
                List<String> changedFiles = fetchGitDiff(lastVersionDeployCommit);
                // 判断有没有代码变更
                if (!hasUnDeployFiles(changedFiles, config)) {
                    config.skip("No code changes", false);
                }
            }
        }
        // 自定义版本时不判断Git
    }

    /**
     * 获取kubernetes上部署的最新的commit版本.
     *
     * @param configId  the config id
     * @param appName   the app name
     * @param namespace the namespace
     * @return 最新的commit版本
     * @throws ApiException the api exception
     */
    private static String fetchLastVersionDeployCommit(String configId, String appName, String namespace) throws ApiException {
        return VersionController.getGitCommit(KubeHelper.inst(configId).read(appName, namespace, KubeRES.SERVICE, V1Service.class));
    }

    /**
     * 获取当前Git commit版本与kubernetes上部署的最新的commit版本的差异文件列表.
     *
     * @param lastVersionDeployCommit kubernetes上部署的最新的commit版本
     * @return 变更的文件列表
     */
    private static List<String> fetchGitDiff(String lastVersionDeployCommit) {
        List<String> changedFiles = GitHelper.inst().diff(lastVersionDeployCommit, "HEAD");
        Dew.log.debug("Change files:");
        Dew.log.debug("-------------------");
        changedFiles.forEach(file -> Dew.log.debug(">>" + file));
        Dew.log.debug("-------------------");
        return changedFiles;
    }

    /**
     * 是否存在未部署的文件.
     * <p>
     * 即是否存在有变更的文件，为true时表示需要处理
     * <p>
     * NOTE: changedFiles 可能是多个maven模块的集合，需要过滤掉非当前模块的文件
     *
     * @param changedFiles  the changed files
     * @param projectConfig the project config
     * @return 是否存在未部署的文件
     */
    private static boolean hasUnDeployFiles(List<String> changedFiles, FinalProjectConfig projectConfig) {
        File basePath = new File(projectConfig.getMvnDirectory());
        // 找到git根目录
        while (!Arrays.asList(basePath.list()).contains(".git")) {
            basePath = basePath.getParentFile();
        }
        String projectPath = projectConfig.getMvnDirectory().substring(basePath.getPath().length() + 1).replaceAll("\\\\", "/");
        // 找到当前项目变更的文件列表
        changedFiles = changedFiles.stream()
                .filter(file -> file.startsWith(projectPath))
                .collect(Collectors.toList());
        Dew.log.info("Found " + changedFiles.size() + " changed files for " + projectConfig.getAppName());
        if (changedFiles.isEmpty()) {
            return false;
        } else if (!projectConfig.getIgnoreChangeFiles().isEmpty()
                && !$.file.noneMath(changedFiles, new ArrayList<>(projectConfig.getIgnoreChangeFiles()))) {
            // 排除忽略的文件后是否存在未部署的文件
            Dew.log.info("Found 0 changed files filtered ignore files for " + projectConfig.getAppName());
            return false;
        }
        return true;
    }

}
