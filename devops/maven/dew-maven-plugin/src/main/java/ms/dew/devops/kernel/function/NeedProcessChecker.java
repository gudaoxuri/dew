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
import com.ecfront.dew.common.Resp;
import ms.dew.devops.kernel.DevOps;
import ms.dew.devops.kernel.config.DewProfile;
import ms.dew.devops.kernel.config.FinalProjectConfig;
import ms.dew.devops.kernel.exception.GlobalProcessException;
import ms.dew.devops.kernel.helper.GitHelper;
import ms.dew.devops.kernel.util.DewLog;
import ms.dew.devops.kernel.util.ExecuteOnceProcessor;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Need process checker.
 * <p>
 * 仅用于部署/回滚流程
 *
 * @author gudaoxuri
 */
public class NeedProcessChecker {

    private static Logger logger = DewLog.build(NeedProcessChecker.class);

    /**
     * Check need process projects.
     *
     * @param quiet the quiet
     */
    public static synchronized void checkNeedProcessProjects(boolean quiet) {
        if (ExecuteOnceProcessor.executedCheck(NeedProcessChecker.class)) {
            return;
        }
        // 初始化，全局只调用一次
        logger.info("Fetch need process projects");
        try {
            for (FinalProjectConfig projectConfig : DevOps.Config.getFinalConfig().getProjects().values()) {
                if (projectConfig.getSkip()) {
                    continue;
                }
                logger.info("Need process checking for " + projectConfig.getAppName());
                Resp<String> deployAble = projectConfig.getDeployPlugin().deployAble(projectConfig);
                if (!deployAble.ok()) {
                    DevOps.SkipProcess.skip(projectConfig, deployAble.getMessage(), false);
                    continue;
                }
                projectConfig.getDeployPlugin()
                        .fetchLastDeployedVersion(projectConfig.getId(), projectConfig.getAppName(), projectConfig.getNamespace())
                        .ifPresent(lastDeployedVersion -> {
                            logger.debug("Latest version is " + lastDeployedVersion);
                            if (!projectConfig.getDisableReuseVersion()) {
                                // 重用版本
                                projectConfig.getDeployPlugin().fetchLastDeployedVersion(projectConfig.getId() + DevOps.APPEND_FLAG,
                                        projectConfig.getAppName(), projectConfig.getNamespace()).ifPresent(lastVersionDeployCommitFromProfile -> {
                                    if (lastDeployedVersion.equals(lastVersionDeployCommitFromProfile)) {
                                        DevOps.SkipProcess.skip(projectConfig, "Reuse last version " + lastDeployedVersion + " has been deployed", false);
                                    } else {
                                        logger.info("Reuse last version " + lastVersionDeployCommitFromProfile
                                                + " from " + projectConfig.getReuseLastVersionFromProfile());
                                        projectConfig.setGitCommit(lastVersionDeployCommitFromProfile);
                                        projectConfig.setImageVersion(lastVersionDeployCommitFromProfile);
                                    }
                                });
                            }
                            List<String> changedFiles = fetchGitDiff(lastDeployedVersion);
                            // 判断有没有代码变更
                            if (!hasUnDeployFiles(changedFiles, projectConfig)) {
                                DevOps.SkipProcess.skip(projectConfig, "No code changes", false);
                            }
                        });
            }
            // 依赖分析
            dependencyProcess(DevOps.Config.getFinalConfig().getProjects().values());

            List<FinalProjectConfig> processingProjects = DevOps.Config.getFinalConfig().getProjects().values().stream()
                    .filter(config -> !config.getSkip())
                    .collect(Collectors.toList());
            if (processingProjects.isEmpty()) {
                // 不存在需要处理的项目
                DevOps.stopped = true;
                logger.info("No project found to be processed");
                ExecuteEventProcessor.init(processingProjects);
                return;
            }
            // 提示将要处理的项目
            StringBuilder sb = new StringBuilder();
            sb.append("\r\n==================== Processing Projects =====================\r\n\r\n");
            sb.append(processingProjects.stream().map(config ->
                    String.format("> [%-25s] %s:%s", config.getAppKindPlugin().getName(), config.getAppGroup(), config.getAppName()))
                    .collect(Collectors.joining("\r\n")));
            sb.append("\r\n\r\n==============================================================\r\n");
            if (quiet) {
                logger.info(sb.toString());
            } else {
                // 非静默模式，用户选择是否继续
                sb.append("\r\n< Y > or < N >");
                logger.info(sb.toString());
                BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
                if (reader.readLine().trim().equalsIgnoreCase("N")) {
                    DevOps.stopped = true;
                    logger.info("Process canceled");
                    return;
                }
            }
            ExecuteEventProcessor.init(processingProjects);
        } catch (Throwable e) {
            throw new GlobalProcessException(e.getMessage(), e);
        }
    }

    /**
     * 获取当前Git commit版本与kubernetes上部署的最新的commit版本的差异文件列表.
     *
     * @param lastVersionDeployCommit kubernetes上部署的最新的commit版本
     * @return 变更的文件列表
     */
    private static List<String> fetchGitDiff(String lastVersionDeployCommit) {
        List<String> changedFiles = GitHelper.inst().diff(lastVersionDeployCommit, "HEAD");
        logger.debug("Change files:\n-------------------");
        changedFiles.forEach(file -> logger.debug(">>" + file));
        logger.debug("-------------------");
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
        File basePathFile = new File(projectConfig.getDirectory());
        // 找到git根目录
        while (!Arrays.asList(basePathFile.list()).contains(".git")) {
            basePathFile = basePathFile.getParentFile();
        }
        String projectPath = projectConfig.getDirectory().substring(basePathFile.getPath().length() + 1).replaceAll("\\\\", "/");
        final String basePath = basePathFile.getPath();
        // 获取当前项目目录下的工程路径
        List<String> collectedProjectPaths = projectConfig.getMavenSession().getProjects().stream()
                .filter(project -> project.getBasedir().getPath().startsWith(projectConfig.getDirectory()))
                .map(project -> project.getBasedir().getPath().substring(basePath.length() + 1).replaceAll("\\\\", "/"))
                .collect(Collectors.toList());
        // 找到当前项目变更的文件列表
        changedFiles = changedFiles.stream()
                .filter(path -> path.startsWith(projectPath))
                .collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(collectedProjectPaths)) {
            changedFiles = changedFiles.stream()
                    .filter(path -> {
                        for (String collectedProjectPath : collectedProjectPaths) {
                            if (path.startsWith(collectedProjectPath)) {
                                return false;
                            }
                        }
                        return true;
                    })
                    .collect(Collectors.toList());
        }
        logger.info("Found " + changedFiles.size() + " changed files for " + projectConfig.getAppName());
        if (changedFiles.isEmpty()) {
            return false;
        } else if (!projectConfig.getIgnoreChangeFiles().isEmpty()
                && !$.file.noneMath(changedFiles, new ArrayList<>(projectConfig.getIgnoreChangeFiles()))) {
            // 排除忽略的文件后是否存在未部署的文件
            logger.info("Found 0 changed files filtered ignore files for " + projectConfig.getAppName());
            return false;
        }
        return true;
    }

    private static void dependencyProcess(Collection<FinalProjectConfig> projectConfigs) {
        List<String> needProcessSnapshotProjects = projectConfigs.stream()
                // 找到需要处理的快照项目
                .filter(projectConfig -> !projectConfig.getSkip()
                        && projectConfig.getAppVersion().toUpperCase().endsWith("SNAPSHOT"))
                .map(FinalProjectConfig::getId)
                .collect(Collectors.toList());
        dependencyProcess(projectConfigs, needProcessSnapshotProjects);
    }

    private static void dependencyProcess(Collection<FinalProjectConfig> projectConfigs, List<String> needProcessProjects) {
        projectConfigs.stream()
                // 所有跳过的项目
                .filter(DewProfile::getSkip)
                // 找到有依赖于需要处理的快照项目
                .filter(projectConfig ->
                        projectConfig.getMavenProject().getArtifacts().stream()
                                .anyMatch(artifact -> needProcessProjects.contains(artifact.getId())))
                .forEach(projectConfig -> {
                    // 这些项目不能跳过
                    DevOps.SkipProcess.unSkip(projectConfig);
                    // 递归依赖于此项目的各项目，这些项目也不能跳过
                    dependencyProcess(projectConfigs, new ArrayList<String>() {
                        {
                            add(projectConfig.getId());
                        }
                    });
                });
    }
}
