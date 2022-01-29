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

package group.idealworld.dew.devops.kernel.flow.release;

import group.idealworld.dew.devops.kernel.exception.ProjectProcessException;
import group.idealworld.dew.devops.kernel.flow.BasicFlow;
import group.idealworld.dew.devops.kernel.helper.DockerHelper;
import group.idealworld.dew.devops.kernel.util.ShellHelper;
import group.idealworld.dew.devops.kernel.config.FinalProjectConfig;

import java.io.IOException;
import java.util.HashMap;
import java.util.Optional;

/**
 * Basic prepare flow.
 *
 * @author gudaoxuri
 */
public abstract class BasicPrepareFlow extends BasicFlow {

    /**
     * Need execute prepare package cmd.
     *
     * @param config      the config
     * @param currentPath the current path
     * @return the boolean
     */
    protected abstract boolean needExecutePreparePackageCmd(FinalProjectConfig config, String currentPath);


    /**
     * Gets prepare package cmd.
     *
     * @param config      the config
     * @param currentPath the current path
     * @return the error process package cmd
     */
    protected abstract Optional<String> getPreparePackageCmd(FinalProjectConfig config, String currentPath);


    /**
     * Gets package cmd.
     *
     * @param config      the config
     * @param currentPath the current path
     * @return the package cmd
     */
    protected abstract Optional<String> getPackageCmd(FinalProjectConfig config, String currentPath);

    /**
     * Post prepare build.
     *
     * @param config       the project config
     * @param flowBasePath the flow base path
     * @throws IOException the io exception
     */
    protected void postPrepareBuild(FinalProjectConfig config, String flowBasePath) throws IOException {
    }

    /**
     * Exists reuse version.
     * @param config the config
     * @return the boolean
     */
    protected abstract boolean existsReuseVersion(FinalProjectConfig config);

    @Override
    protected void process(FinalProjectConfig config, String flowBasePath) throws IOException {
        if (null != config.getDisableReuseVersion() && !config.getDisableReuseVersion()
                && existsReuseVersion(config)) {
            // 重用模式下不用再执行准备操作
            return;
        }
        if (null != config.getCurrImageName() && DockerHelper.inst(config.getId()).registry.existImage(config.getCurrImageName())) {
            // 镜像已存在不用再执行准备操作
            return;
        }
        // 镜像不存在时执行准备操作
        execPackageCmd(config, false);
        postPrepareBuild(config, flowBasePath);
    }

    private void execPackageCmd(FinalProjectConfig config, boolean retry) {
        String currentPath = config.getDirectory();
        Optional<String> packageCmdOpt = getPackageCmd(config, currentPath);
        if (!packageCmdOpt.isPresent()) {
            // 不用执行命令
            return;
        }
        boolean result;
        if (retry || needExecutePreparePackageCmd(config, currentPath)) {
            Optional<String> preparePackageCmdOpt = getPreparePackageCmd(config, currentPath);
            if (!preparePackageCmdOpt.isPresent()) {
                // 失败处理命令失败
                logger.warn("Prepare package command needs to be executed, but the command does not exist");
                throw new ProjectProcessException("Prepare package command needs to be executed, but the command does not exist");
            }
            result = ShellHelper.execCmd("preparePackageCmd", new HashMap<>() {
                {
                    put("NODE_ENV", config.getProfile());
                }
            }, preparePackageCmdOpt.get());
            if (!result) {
                // 预打包命令执行失败
                logger.warn("Prepare package command execution failed");
                throw new ProjectProcessException("Prepare package command execution failed");
            }
        }
        result = ShellHelper.execCmd("packageCmd", new HashMap<>() {
            {
                put("NODE_ENV", config.getProfile());
            }
        }, packageCmdOpt.get());
        if (result) {
            // 命令执行成功
            return;
        }
        if (!retry) {
            // 命令执行失败，尝试进行强制执行预打包命令
            logger.info("Package command execution failed, try to enforce execution prepare package command");
            execPackageCmd(config, true);
        } else {
            throw new ProjectProcessException("Retry package command execution failed");
        }
    }

}
