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

import com.ecfront.dew.common.$;
import com.ecfront.dew.common.Resp;
import group.idealworld.dew.devops.kernel.DevOps;
import group.idealworld.dew.devops.kernel.config.FinalProjectConfig;
import group.idealworld.dew.devops.kernel.exception.GitDiffException;
import group.idealworld.dew.devops.kernel.exception.GlobalProcessException;
import group.idealworld.dew.devops.kernel.helper.GitHelper;
import group.idealworld.dew.devops.kernel.plugin.appkind.jvmlib.JvmLibAppKindPlugin;
import group.idealworld.dew.devops.kernel.plugin.appkind.pom.PomAppKindPlugin;
import group.idealworld.dew.devops.kernel.util.DewLog;
import group.idealworld.dew.devops.kernel.util.ExecuteOnceProcessor;
import io.kubernetes.client.openapi.models.V1ConfigMap;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;
import java.util.regex.Matcher;
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
                    DevOps.SkipProcess.skip(projectConfig, deployAble.getMessage(), FinalProjectConfig.SkipCodeEnum.NON_SELF_CONFIG, false);
                    continue;
                }
                // 是否需要重用
                Optional<String> lastVersionDeployCommitFromProfileOptional = getAndSetCommitVersion(projectConfig);
                projectConfig.getDeployPlugin()
                        .fetchLastDeployedVersion(projectConfig.getId(), projectConfig.getAppName(), projectConfig.getNamespace())
                        .ifPresent(lastDeployedVersion -> {
                            logger.debug("Latest version is " + lastDeployedVersion);
                            if (!projectConfig.getDisableReuseVersion()) {
                                // 重用版本
                                lastVersionDeployCommitFromProfileOptional.ifPresent(lastVersionDeployCommitFromProfile -> {
                                    if (lastDeployedVersion.equals(lastVersionDeployCommitFromProfile)) {
                                        DevOps.SkipProcess.skip(projectConfig,
                                                "Reuse last version " + lastDeployedVersion + " has been deployed",
                                                FinalProjectConfig.SkipCodeEnum.NON_SELF_CONFIG, false);
                                    }
                                });
                            }
                            try {
                                List<String> changedFiles = fetchGitDiff(lastDeployedVersion);
                                // 判断有没有代码变更
                                if (!hasUnDeployFiles(changedFiles, projectConfig)) {
                                    DevOps.SkipProcess.skip(projectConfig, "No code changes", FinalProjectConfig.SkipCodeEnum.NON_SELF_CONFIG, false);
                                } else {
                                    // 如果前端项目的 package.json 文件有代码变更，则删掉同级目录下的 node_modules 文件夹
                                    // 以便执行 preparePackageCmd 的预打包命令
                                    removeNodeModulesDirectory(projectConfig, changedFiles);
                                }
                            } catch (GitDiffException e) {
                                logger.warn("Redeploying [" + projectConfig.getAppName() + "] due to some codes had been reverted or changed.");
                            }
                        });
            }
            // 依赖分析
            dependencyProcess(DevOps.Config.getFinalConfig().getProjects().values());

            List<FinalProjectConfig> processingProjects = DevOps.Config.getFinalConfig().getProjects().values().stream()
                    .filter(config -> !config.getSkip())
                    .collect(Collectors.toList());
            // 提示要处理的项目，并判断是否继续执行
            if (!dealProcessProjects(processingProjects, quiet)) {
                return;
            }
            ExecuteEventProcessor.init(processingProjects);
        } catch (Throwable e) {
            throw new GlobalProcessException(e.getMessage(), e);
        }
    }

    /**
     * Remove node_modules directory while package.json file changed.
     *
     * @param projectConfig the project config
     * @param changedFiles  the project changed files
     */
    private static void removeNodeModulesDirectory(FinalProjectConfig projectConfig, List<String> changedFiles) {
        File basePathFile = new File(projectConfig.getDirectory());
        while (!Arrays.asList(Objects.requireNonNull(basePathFile.list())).contains(".git")) {
            basePathFile = basePathFile.getParentFile();
        }
        final String basePath = basePathFile.getPath();
        changedFiles.stream().filter(changedFile -> changedFile.toLowerCase().contains("package.json"))
                .forEach(packageJsonPath -> {
                    File nodeModulesPath = new File(basePath + File.separator
                            + packageJsonPath.replace("package.json", "node_modules"));
                    if (projectConfig.getDirectory().startsWith(nodeModulesPath.getParent())) {
                        try {
                            logger.info("Found package.json file changed, removing node_modules directory...");
                            FileUtils.deleteDirectory(nodeModulesPath);
                        } catch (IOException e) {
                            logger.warn("Failed to remove node_modules directory.");
                            e.printStackTrace();
                        }
                    }
                });
    }

    /**
     * Get and set commit version.
     *
     * @param projectConfig the project config
     * @return commit version
     */
    private static Optional<String> getAndSetCommitVersion(FinalProjectConfig projectConfig) throws IOException {
        if (!projectConfig.getDisableReuseVersion()) {
            Optional<String> lastVersionDeployCommitFromProfileOptional = projectConfig.getDeployPlugin()
                    .fetchLastDeployedVersionByReuseProfile(projectConfig);
            lastVersionDeployCommitFromProfileOptional.ifPresent(lastVersionDeployCommitFromProfile -> {
                if (projectConfig.getGitCommit().equals(lastVersionDeployCommitFromProfile)) {
                    logger.info("Reuse last version " + lastVersionDeployCommitFromProfile
                            + " from " + projectConfig.getReuseLastVersionFromProfile());
                    projectConfig.setGitCommit(lastVersionDeployCommitFromProfile);
                    projectConfig.setImageVersion(lastVersionDeployCommitFromProfile);
                } else {
                    projectConfig.setDisableReuseVersion(true);
                }
            });
            return lastVersionDeployCommitFromProfileOptional;
        }
        return Optional.empty();
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
        String projectPath = projectConfig.getDirectory();
        final String basePath = basePathFile.getPath();
        // 获取当前项目目录下的工程路径及依赖项目的工程路径
        List<String> collectedProjectPaths = projectConfig.getMavenSession().getProjects().stream()
                .filter(project -> (project.getBasedir().getPath() + File.separator).startsWith(projectConfig.getDirectory())
                        || projectConfig.getMavenProject().getDependencies().stream()
                        .anyMatch(dependency -> dependency.getArtifactId().equals(project.getArtifactId())))
                .map(project -> project.getBasedir().getPath())
                .collect(Collectors.toList());
        // 找到当前项目变更的文件列表及依赖项目变更的文件列表
        changedFiles = changedFiles.stream().map(changedFile -> basePath + File.separator
                + changedFile.replaceAll("/", Matcher.quoteReplacement(File.separator)))
                .filter(path -> path.startsWith(projectPath) || projectConfig.getMavenSession().getProjects().stream()
                        .anyMatch(project -> path.startsWith((project.getBasedir().getPath() + File.separator)
                                .substring(project.getBasedir().getPath().length() + 1))))
                .collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(collectedProjectPaths)) {
            changedFiles = changedFiles.stream()
                    .filter(path -> {
                        for (String collectedProjectPath : collectedProjectPaths) {
                            if (path.startsWith(collectedProjectPath) || path.equals(basePath + File.separator + ".dew")) {
                                return true;
                            }
                        }
                        return false;
                    })
                    .collect(Collectors.toList());
        }
        logger.info("Found " + changedFiles.size() + " changed files for " + projectConfig.getAppName());
        if (changedFiles.isEmpty()) {
            return false;
        } else if (!projectConfig.getIgnoreChangeFiles().isEmpty()
                && !$.file.noneMath(changedFiles, new ArrayList<>(projectConfig.getIgnoreChangeFiles()))) {
            // 排除忽略的文件后是否存在未部署的文件
            logger.info("Found 0 changed file filtered ignore files for " + projectConfig.getAppName());
            return false;
        }
        return true;
    }

    private static void dependencyProcess(Collection<FinalProjectConfig> projectConfigs) {
        List<String> needProcessSnapshotProjects = projectConfigs.stream()
                // 找到需要处理的快照项目
                .filter(projectConfig -> !projectConfig.getSkip()
                        && projectConfig.getId().toUpperCase().endsWith("SNAPSHOT"))
                .map(FinalProjectConfig::getId)
                .collect(Collectors.toList());
        dependencyProcess(projectConfigs, needProcessSnapshotProjects);
    }

    private static void dependencyProcess(Collection<FinalProjectConfig> projectConfigs, List<String> needProcessProjects) {
        projectConfigs.stream()
                // 所有跳过的项目(配置文件跳过的项目除外)
                .filter(finalProjectConfig -> finalProjectConfig.getSkip()
                        && !finalProjectConfig.getSkipCode().equals(FinalProjectConfig.SkipCodeEnum.SELF_CONFIG))
                // 找到有依赖于需要处理的快照项目
                .filter(projectConfig ->
                        projectConfig.getMavenProject().getArtifacts().stream()
                                .anyMatch(artifact -> needProcessProjects.contains(artifact.getId())))
                .forEach(projectConfig -> {
                    // 这些项目不能跳过
                    DevOps.SkipProcess.unSkip(projectConfig);
                    // 递归依赖于此项目的各项目，这些项目也不能跳过
                    dependencyProcess(projectConfigs, new ArrayList<>() {
                        {
                            add(projectConfig.getId());
                        }
                    });
                });
    }

    /**
     * check Need Rollback Process Projects.
     *
     * @param rollbackVersion rollbackVersion
     * @param quiet           the quiet
     */
    public static synchronized void checkNeedRollbackProcessProjects(String rollbackVersion, boolean quiet) {
        if (StringUtils.isBlank(rollbackVersion)) {
            return;
        }
        try {
            logger.info("Assign rollback version : " + rollbackVersion);
            List<FinalProjectConfig> processingProjects = new ArrayList<>();
            for (FinalProjectConfig projectConfig : DevOps.Config.getFinalConfig().getProjects().values()) {
                if (projectConfig.getSkip() || projectConfig.getAppKindPlugin() instanceof PomAppKindPlugin
                        || projectConfig.getAppKindPlugin() instanceof JvmLibAppKindPlugin) {
                    continue;
                }
                String currentAppVersion = VersionController.getAppCurrentVersion(projectConfig);
                Map<String, V1ConfigMap> versions = VersionController.getAppVersions(projectConfig);

                if (!(projectConfig.getAppKindPlugin() instanceof PomAppKindPlugin)
                        && !rollbackVersion.equalsIgnoreCase(currentAppVersion)
                        && versions.containsKey(rollbackVersion)) {
                    processingProjects.add(projectConfig);
                }
            }
            // 提示要处理的项目，并判断是否继续执行
            if (!dealProcessProjects(processingProjects, quiet)) {
                return;
            }
            ExecuteEventProcessor.init(processingProjects);
        } catch (Throwable e) {
            throw new GlobalProcessException(e.getMessage(), e);
        }

    }


    /**
     * 是否要继续处理项目.
     *
     * @param processingProjects the processing projects
     * @param quiet              the quiet
     * @return Boolean
     * @throws IOException IOException
     */
    private static Boolean dealProcessProjects(List<FinalProjectConfig> processingProjects, boolean quiet) throws IOException {
        if (processingProjects.isEmpty()) {
            // 不存在需要处理的项目
            DevOps.stopped = true;
            logger.info("No project found to be processed");
            ExecuteEventProcessor.init(processingProjects);
            return false;
        }
        // 提示将要处理的项目
        StringBuilder sb = getProcessProjectsString(processingProjects);
        if (quiet) {
            logger.info(sb.toString());
        } else {
            // 非静默模式，用户选择是否继续
            logger.info(sb.append("\r\n< Y > or < N >").toString());
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            if (reader.readLine().trim().equalsIgnoreCase("N")) {
                DevOps.stopped = true;
                logger.info("Process canceled");
                return false;
            }
        }
        return true;
    }

    /**
     * Get Process Projects String.
     *
     * @param processingProjects processingProjects
     * @return StringBuilder
     */
    private static StringBuilder getProcessProjectsString(List<FinalProjectConfig> processingProjects) {
        // 将要处理的项目
        return new StringBuilder()
                .append("\r\n==================== Processing Projects =====================\r\n\r\n")
                .append(processingProjects.stream().map(config ->
                        String.format("> [%-25s] %s:%s", config.getAppKindPlugin().getName(), config.getAppGroup(), config.getAppName()))
                        .collect(Collectors.joining("\r\n")))
                .append("\r\n\r\n==============================================================\r\n");
    }

}
