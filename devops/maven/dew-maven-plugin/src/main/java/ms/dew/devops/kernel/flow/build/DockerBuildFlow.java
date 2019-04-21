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

package ms.dew.devops.kernel.flow.build;

import com.ecfront.dew.common.$;
import io.kubernetes.client.ApiException;
import ms.dew.devops.helper.DockerHelper;
import ms.dew.devops.kernel.Dew;
import ms.dew.devops.kernel.config.FinalProjectConfig;
import ms.dew.devops.kernel.flow.BasicFlow;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Docker build flow.
 *
 * @author gudaoxuri
 */
public abstract class DockerBuildFlow extends BasicFlow {

    /**
     * Pre docker build.
     *
     * @param config       the project config
     * @param flowBasePath the flow base path
     * @return the build result
     * @throws ApiException the api exception
     * @throws IOException  the io exception
     */
    protected boolean preDockerBuild(FinalProjectConfig config, String flowBasePath) throws ApiException, IOException {
        return true;
    }

    @Override
    protected boolean process(FinalProjectConfig config, String flowBasePath) throws ApiException, IOException {
        // 先判断是否存在
        if (!DockerHelper.inst(config.getId()).registry.exist(config.getCurrImageName())) {
            boolean result;
            if (config.getDisableReuseVersion()) {
                result = processByNewImage(config, flowBasePath);
            } else {
                result = processByReuse(config, flowBasePath);
            }
            if (!result) {
                return false;
            }
            // push 到 registry
            if (config.getDocker().getRegistryUrl() == null
                    || config.getDocker().getRegistryUrl().isEmpty()) {
                Dew.log.warn("Not found docker registry url and push is ignored, which is mostly used for stand-alone testing");
            } else {
                Dew.log.info("Pushing image : " + config.getCurrImageName());
                DockerHelper.inst(config.getId()).image.push(config.getCurrImageName(), true);
            }
        } else {
            Dew.log.info("Ignore build, because image " + config.getCurrImageName() + " already exist");
        }
        return true;
    }

    /**
     * 重用版本模式下的处理.
     *
     * @param config       the project config
     * @param flowBasePath the flow base path
     * @return the build result
     * @throws ApiException the api exception
     * @throws IOException  the io exception
     */
    private boolean processByReuse(FinalProjectConfig config, String flowBasePath) throws IOException, ApiException {
        String reuseImageName = config.getImageName(
                config.getAppendProfile().getDocker().getRegistryHost(),
                config.getAppendProfile().getNamespace(),
                config.getAppName(),
                config.getGitCommit());
        Dew.log.info("Reuse image : " + reuseImageName);
        // 从目标环境的镜像仓库拉取镜像到本地
        DockerHelper.inst(config.getId() + "-append").image.pull(reuseImageName, true);
        // 打上当前镜像的Tag
        DockerHelper.inst(config.getId() + "-append").image.copy(reuseImageName, config.getCurrImageName());
        return true;
    }

    /**
     * 正常模式下的处理.
     *
     * @param config       the project config
     * @param flowBasePath the flow base path
     * @return the build result
     * @throws ApiException the api exception
     * @throws IOException  the io exception
     */
    private boolean processByNewImage(FinalProjectConfig config, String flowBasePath) throws IOException, ApiException {
        Dew.log.info("Building image : " + config.getCurrImageName());
        if (!preDockerBuild(config, flowBasePath)) {
            Dew.log.debug("Finished,because [preDockerBuild] is false");
            return false;
        }
        if (config.getDocker().getImage() != null
                && !config.getDocker().getImage().trim().isEmpty()) {
            // 如果存在自定义镜像则替换默认的镜像
            Dew.log.debug("Using custom image : " + config.getDocker().getImage().trim());
            String dockerFileContent = $.file.readAllByFile(new File(flowBasePath + "Dockerfile"), "UTF-8");
            dockerFileContent = dockerFileContent.replaceAll("FROM .*", "FROM " + config.getDocker().getImage().trim());
            Files.write(Paths.get(flowBasePath + "Dockerfile"), dockerFileContent.getBytes());
        }
        DockerHelper.inst(config.getId()).image.build(config.getCurrImageName(), flowBasePath);
        return true;
    }

}
