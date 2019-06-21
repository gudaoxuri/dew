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

package ms.dew.devops.kernel.flow.release;

import com.ecfront.dew.common.$;
import ms.dew.devops.kernel.DevOps;
import ms.dew.devops.kernel.config.DewDockerLabel;
import ms.dew.devops.kernel.config.FinalProjectConfig;
import ms.dew.devops.kernel.flow.BasicFlow;
import ms.dew.devops.kernel.helper.DockerHelper;
import ms.dew.devops.kernel.model.DockerImageTag;
import ms.dew.devops.kernel.util.DewLog;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Docker build flow.
 *
 * @author gudaoxuri
 */
public class DockerBuildFlow extends BasicFlow {

    /**
     * Pre docker build.
     *
     * @param config       the project config
     * @param flowBasePath the flow base path
     * @throws IOException the io exception
     */
    protected void preDockerBuild(FinalProjectConfig config, String flowBasePath) throws IOException {
    }

    @Override
    protected void process(FinalProjectConfig config, String flowBasePath) throws IOException {
        // 先判断是否存在
        if (!DockerHelper.inst(config.getId()).registry.exist(config.getCurrImageName())) {
            new ReuseVersionProcessorFactory().processBeforeRelease(config, flowBasePath);
        } else {
            logger.info("Ignore build, because image " + config.getCurrImageName() + " already exist");
        }
    }

    /**
     * 重用版本模式下的处理.
     *
     * @param config the project config
     */
    private void processByReuse(FinalProjectConfig config) {
        String reuseImageName = config.getImageName(
                config.getAppendProfile().getDocker().getRegistryHost(),
                config.getAppendProfile().getNamespace(),
                config.getAppName(),
                config.getImageVersion());
        logger.info("Reuse image : " + reuseImageName);
        // 从目标环境的镜像仓库拉取镜像到本地
        DockerHelper.inst(config.getId() + DevOps.APPEND_FLAG).image.pull(reuseImageName, true);
        // 打上当前镜像的Tag
        DockerHelper.inst(config.getId() + DevOps.APPEND_FLAG).image.copy(reuseImageName, config.getCurrImageName());
    }

    /**
     * 正常模式下的处理.
     *
     * @param config       the project config
     * @param flowBasePath the flow base path
     * @throws IOException the io exception
     */
    private void processByNewImage(FinalProjectConfig config, String flowBasePath) throws IOException {
        logger.info("Building image : " + config.getCurrImageName());
        preDockerBuild(config, flowBasePath);
        if (config.getDocker().getImage() != null
                && !config.getDocker().getImage().trim().isEmpty()) {
            // 如果存在自定义镜像则替换默认的镜像
            logger.debug("Using custom image : " + config.getDocker().getImage().trim());
            String dockerFileContent = $.file.readAllByFile(new File(flowBasePath + "Dockerfile"), "UTF-8");
            dockerFileContent = dockerFileContent.replaceAll("FROM .*", "FROM " + config.getDocker().getImage().trim());
            Files.write(Paths.get(flowBasePath + "Dockerfile"), dockerFileContent.getBytes());
        }
        DockerHelper.inst(config.getId()).image.build(config.getCurrImageName(), flowBasePath, new HashMap<String, String>() {
            {
                put("PORT", config.getApp().getPort() + "");
            }
        });
    }

    class ReuseVersionProcessorFactory {

        public void processBeforeRelease(FinalProjectConfig config, String flowBasePath) throws IOException {
            switch (config.getReuseVersionType()) {
                case "LABEL":
                    new ReuseVersionLabelProcessor().processBeforeRelease(config, flowBasePath);
                    break;
                case "TAG":
                    new ReuseVersionTagProcessor().processBeforeRelease(config, flowBasePath);
                    break;
                default:
                    break;
            }
        }

        public void processAfterReleaseSuccessful(FinalProjectConfig config) throws IOException {
            switch (config.getReuseVersionType()) {
                case "LABEL":
                    new ReuseVersionLabelProcessor().processAfterReleaseSuccessful(config);
                    break;
                case "TAG":
                    new ReuseVersionTagProcessor().processAfterReleaseSuccessful(config);
                    break;
                default:
                    break;
            }
        }


    }

    private interface ReuseVersionProcessor {

        Logger logger = DewLog.build(ReuseVersionProcessor.class);

        void processBeforeRelease(FinalProjectConfig config, String flowBasePath) throws IOException;

        void processAfterReleaseSuccessful(FinalProjectConfig config) throws IOException;

        default void pushImageToRegistry(FinalProjectConfig config) {
            // push 到 registry
            if (config.getDocker().getRegistryUrl() == null
                    || config.getDocker().getRegistryUrl().isEmpty()) {
                logger.warn("Not found docker registry url and push is ignored, which is mostly used for stand-alone testing");
            } else {
                logger.info("Pushing image : " + config.getCurrImageName());
                DockerHelper.inst(config.getId()).image.push(config.getCurrImageName(), true);
            }
        }

    }

    private class ReuseVersionLabelProcessor implements ReuseVersionProcessor {

        @Override
        public void processBeforeRelease(FinalProjectConfig config, String flowBasePath) throws IOException {
            if (config.getDisableReuseVersion()) {
                processByNewImage(config, flowBasePath);
            } else {
                // 判断是否有最近可用的image
                List<DockerImageTag> dockerImageTags = DockerHelper.inst(config.getId() + DevOps.APPEND_FLAG)
                        .registry.listTags(config.getAppendProfile().getNamespace() + "/" + config.getAppName());
                if (CollectionUtils.isNotEmpty(dockerImageTags)) {
                    DockerImageTag reuseImageTag = dockerImageTags.stream().collect(Collectors.toMap(DockerImageTag::getName,
                            dockerImageTag -> dockerImageTag)).get(config.getImageVersion());
                    if (reuseImageTag == null) {
                        processByNewImage(config, flowBasePath);
                        return;
                    }
                    dockerImageTags = dockerImageTags.stream().filter(dockerImageTag ->
                            dockerImageTag.getCreated().getTime() >= reuseImageTag.getCreated().getTime()
                                    && CollectionUtils.isNotEmpty(dockerImageTag.getLabels()) && dockerImageTag.getLabels().stream()
                                    .map(DewDockerLabel::getName).collect(Collectors.toList()).contains(config.getReuseLastVersionFromProfile()))
                            .collect(Collectors.toList());
                    if (CollectionUtils.isNotEmpty(dockerImageTags)) {
                        dockerImageTags.sort(Comparator.comparing(DockerImageTag::getCreated));
                        String reuseImageName = config.getImageName(
                                config.getAppendProfile().getDocker().getRegistryHost(),
                                config.getAppendProfile().getNamespace(),
                                config.getAppName(),
                                dockerImageTags.get(0).getName());
                        DockerHelper.inst(config.getId() + DevOps.APPEND_FLAG).image.pull(reuseImageName, true);
                        DockerHelper.inst(config.getId() + DevOps.APPEND_FLAG).image.copy(reuseImageName, config.getCurrImageName());
                    } else {
                        processByNewImage(config, flowBasePath);
                    }
                } else {
                    processByNewImage(config, flowBasePath);
                }
            }
            pushImageToRegistry(config);
        }

        @Override
        public void processAfterReleaseSuccessful(FinalProjectConfig config) throws IOException {
            logger.info("Add label to image :  {}", config.getCurrImageName());
            DockerHelper.inst(config.getId()).registry.addLabelToImage(config.getCurrImageName(), config.getDewDockerLabel().getId());
        }
    }

    private class ReuseVersionTagProcessor implements ReuseVersionProcessor {

        @Override
        public void processBeforeRelease(FinalProjectConfig config, String flowBasePath) throws IOException {
            if (config.getDisableReuseVersion()) {
                processByNewImage(config, flowBasePath);
            } else {
                // 判断指定tag的image是否存在
                String reuseImageName = config.getImageName(config.getAppendProfile().getDocker().getRegistryHost(),
                        config.getAppendProfile().getNamespace(), config.getAppName(), config.getReuseLastVersionFromProfile());
                if (DockerHelper.inst(config.getId() + DevOps.APPEND_FLAG).registry.exist(reuseImageName)) {
                    // 从目标环境的镜像仓库拉取镜像到本地
                    DockerHelper.inst(config.getId() + DevOps.APPEND_FLAG).image.pull(reuseImageName, true);
                    DockerHelper.inst(config.getId() + DevOps.APPEND_FLAG).image.copy(reuseImageName, config.getCurrImageName());
                } else {
                    processByNewImage(config, flowBasePath);
                }
            }
            pushImageToRegistry(config);
        }

        @Override
        public void processAfterReleaseSuccessful(FinalProjectConfig config) {
            String tagImageName = config.getImageName(config.getProfile());
            DockerHelper.inst(config.getId()).image.pull(config.getCurrImageName(), true);
            DockerHelper.inst(config.getId()).image.copy(config.getCurrImageName(), tagImageName);
            // push 到 registry
            if (config.getDocker().getRegistryUrl() == null
                    || config.getDocker().getRegistryUrl().isEmpty()) {
                logger.warn("Not found docker registry url and push is ignored, which is mostly used for stand-alone testing");
            } else {
                logger.info("Pushing available latest image : " + config.getCurrImageName());
                DockerHelper.inst(config.getId()).image.push(tagImageName, true);
            }
        }
    }

}
