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

package ms.dew.devops.kernel.flow.prepare;

import ms.dew.devops.helper.DockerHelper;
import ms.dew.devops.kernel.Dew;
import ms.dew.devops.kernel.config.FinalProjectConfig;
import ms.dew.devops.kernel.flow.BasicFlow;
import ms.dew.devops.util.ShellHelper;

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
     * Gets error compensation package cmd.
     *
     * @param config      the config
     * @param currentPath the current path
     * @return the error process package cmd
     */
    protected abstract Optional<String> getErrorCompensationPackageCmd(FinalProjectConfig config, String currentPath);


    /**
     * Post prepare build.
     *
     * @param config       the project config
     * @param flowBasePath the flow base path
     * @return build result
     * @throws IOException the io exception
     */
    protected boolean postPrepareBuild(FinalProjectConfig config, String flowBasePath) throws IOException {
        return true;
    }

    @Override
    protected boolean process(FinalProjectConfig config, String flowBasePath) throws IOException {
        if (!config.getDisableReuseVersion()) {
            // 重用模式下不用再执行准备操作
            return true;
        }
        if (DockerHelper.inst(config.getId()).registry.exist(config.getCurrImageName())) {
            // 镜像已存在不用再执行准备操作
            return true;
        }
        // 镜像不存在时执行准备操作
        if (!execPackageCmd(config)) {
            Dew.log.warn("Finished,because [execPackageCmd] is false");
            return false;
        }
        if (!postPrepareBuild(config, flowBasePath)) {
            Dew.log.warn("Finished,because [postPrepareBuild] is false");
            return false;
        }
        return true;
    }

    private boolean execPackageCmd(FinalProjectConfig config) {
        String currentPath = Dew.Config.getCurrentProject().getMvnDirectory();
        Optional<String> packageCmdOpt = getPackageCmd(config, currentPath);
        if (!packageCmdOpt.isPresent()) {
            // 不用执行命令
            return true;
        }

        boolean result;
        if (needExecutePreparePackageCmd(config, currentPath)) {
            Optional<String> preparePackageCmdOpt = getPreparePackageCmd(config, currentPath);
            if (!preparePackageCmdOpt.isPresent()) {
                // 失败处理命令失败
                Dew.log.warn("Prepare package command needs to be executed, but the command does not exist");
                return false;
            }
            result = ShellHelper.execCmd("preparePackageCmd", new HashMap<String, String>() {
                {
                    put("NODE_ENV", config.getProfile());
                }
            }, preparePackageCmdOpt.get());
            if (!result) {
                // 预打包命令执行失败
                Dew.log.warn("Prepare package command execution failed");
                return false;
            }
        }
        result = ShellHelper.execCmd("packageCmd", new HashMap<String, String>() {
            {
                put("NODE_ENV", config.getProfile());
            }
        }, packageCmdOpt.get());
        if (result) {
            // 命令执行成功
            return true;
        }
        // 命令执行失败，尝试进行失败处理后重试
        Optional<String> errorCompensationPackageCmdOpt = getErrorCompensationPackageCmd(config, currentPath);
        if (!errorCompensationPackageCmdOpt.isPresent()) {
            // 失败处理命令不存在，无法重试
            return false;
        }
        Dew.log.info("Package command execution failed, try exception recovery");
        // 失败处理操作
        result = ShellHelper.execCmd("errorCompensationPackageCmd", new HashMap<String, String>() {
            {
                put("NODE_ENV", config.getProfile());
            }
        }, errorCompensationPackageCmdOpt.get());
        return result;
    }

}
