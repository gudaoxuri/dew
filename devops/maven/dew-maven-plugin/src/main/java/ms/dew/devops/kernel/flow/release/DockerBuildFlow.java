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
import ms.dew.devops.kernel.config.FinalProjectConfig;
import ms.dew.devops.kernel.flow.BasicFlow;
import ms.dew.devops.kernel.helper.DockerHelper;
import ms.dew.devops.kernel.helper.DockerOpt;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Optional;

import static ms.dew.devops.kernel.config.DewProfile.REUSE_VERSION_TYPE_LABEL;
import static ms.dew.devops.kernel.config.DewProfile.REUSE_VERSION_TYPE_TAG;

/**
 * Docker build flow.
 *
 * @author gudaoxuri
 */
public class DockerBuildFlow extends BasicFlow {

    private static final String DEPLOYED_TAG = "DEPLOYED";

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
            if (config.getDisableReuseVersion()
                    || ReuseVersionProcessorFactory.processBeforeRelease(config, flowBasePath)) {
                processByNewImage(config, flowBasePath);
            }
            // push 到 registry
            if (config.getDocker().getRegistryUrl() == null
                    || config.getDocker().getRegistryUrl().isEmpty()) {
                logger.warn("Not found docker registry url and push is ignored, which is mostly used for stand-alone testing");
            } else {
                logger.info("Pushing image : " + config.getCurrImageName());
                DockerHelper.inst(config.getId()).image.push(config.getCurrImageName(), true);
            }
        } else {
            logger.info("Ignore build, because image " + config.getCurrImageName() + " already exist");
        }
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

    /**
     * 重用版本处理工厂类.
     */
    public static class ReuseVersionProcessorFactory {

        private static ReuseVersionLabelProcessor reuseVersionLabelProcessor = new ReuseVersionLabelProcessor();
        private static ReuseVersionTagProcessor reuseVersionTagProcessor = new ReuseVersionTagProcessor();

        /**
         * Process before release boolean.
         *
         * @param config       the config
         * @param flowBasePath the flow base path
         * @return the boolean
         * @throws IOException the io exception
         */
        static boolean processBeforeRelease(FinalProjectConfig config, String flowBasePath) throws IOException {
            switch (config.getReuseVersionType()) {
                case REUSE_VERSION_TYPE_LABEL:
                    return reuseVersionLabelProcessor.processBeforeRelease(config, flowBasePath);
                case REUSE_VERSION_TYPE_TAG:
                    return reuseVersionTagProcessor.processBeforeRelease(config, flowBasePath);
                default:
                    return false;
            }
        }

        /**
         * Process after release successful.
         *
         * @param config the config
         * @throws IOException the io exception
         */
        static void processAfterReleaseSuccessful(FinalProjectConfig config) throws IOException {
            switch (config.getReuseVersionType()) {
                case REUSE_VERSION_TYPE_LABEL:
                    reuseVersionLabelProcessor.processAfterReleaseSuccessful(config);
                    break;
                case REUSE_VERSION_TYPE_TAG:
                    reuseVersionTagProcessor.processAfterReleaseSuccessful(config);
                    break;
                default:
                    break;
            }
        }


        /**
         * Gets reuse commit.
         *
         * @param config the config
         * @return the reuse commit
         * @throws IOException the io exception
         */
        public static Optional<String> getReuseCommit(FinalProjectConfig config) throws IOException {
            switch (config.getReuseVersionType()) {
                case REUSE_VERSION_TYPE_LABEL:
                    return reuseVersionLabelProcessor.getReuseCommit(config);
                case REUSE_VERSION_TYPE_TAG:
                    return reuseVersionTagProcessor.getReuseCommit(config);
                default:
                    return Optional.empty();
            }
        }
    }

    private interface ReuseVersionProcessor {

        Logger logger = LoggerFactory.getLogger(ReuseVersionProcessor.class);

        boolean processBeforeRelease(FinalProjectConfig config, String flowBasePath) throws IOException;

        void processAfterReleaseSuccessful(FinalProjectConfig config) throws IOException;

        Optional<String> getReuseCommit(FinalProjectConfig config) throws IOException;

    }

    private static class ReuseVersionLabelProcessor implements ReuseVersionProcessor {

        @Override
        public boolean processBeforeRelease(FinalProjectConfig config, String flowBasePath) throws IOException {
            // 判断是否有最近可用的image
            String reuseImageName = getImageNameByLabelName(config);
            if (StringUtils.isEmpty(reuseImageName)) {
                return false;
            }
            if (!DockerHelper.inst(config.getId() + DevOps.APPEND_FLAG).registry.exist(reuseImageName)) {
                return false;
            }
            DockerHelper.inst(config.getId() + DevOps.APPEND_FLAG).image.pull(reuseImageName, true);
            DockerHelper.inst(config.getId()).image.copy(reuseImageName, config.getCurrImageName());
            return true;
        }

        @Override
        public void processAfterReleaseSuccessful(FinalProjectConfig config) throws IOException {
            logger.info("Add label : " + config.getCurrImageName());
            Integer projectId = DockerHelper.inst(config.getId()).registry.getProjectIdByName(config.getNamespace());
            DockerOpt.Label label = DockerHelper.inst(config.getId()).registry.getLabelByName(config.getAppName(), projectId);
            if (label == null) {
                label = new DockerOpt.Label();
                label.setName(config.getAppName());
                label.setDescription(config.getCurrImageName());
                label.setProjectId(projectId);
                DockerHelper.inst(config.getId()).registry.addLabel(label);
            } else {
                label.setDescription(config.getCurrImageName());
                DockerHelper.inst(config.getId()).registry.updateLabelById(label.getId(), label);
            }
        }

        @Override
        public Optional<String> getReuseCommit(FinalProjectConfig config) throws IOException {
            String imageName = getImageNameByLabelName(config);
            if (StringUtils.isEmpty(imageName)) {
                return Optional.empty();
            }
            return Optional.of(imageName.substring(imageName.lastIndexOf(":") + 1));
        }

        private String getImageNameByLabelName(FinalProjectConfig config) throws IOException {
            Integer projectId = DockerHelper.inst(config.getId() + DevOps.APPEND_FLAG).registry
                    .getProjectIdByName(config.getAppendProfile().getNamespace());
            String reuseImageName = null;
            DockerOpt.Label label = DockerHelper.inst(config.getId() + DevOps.APPEND_FLAG).registry.getLabelByName(config.getAppName(), projectId);
            if (label != null) {
                reuseImageName = label.getDescription();
            }
            return reuseImageName;
        }
    }

    private static class ReuseVersionTagProcessor implements ReuseVersionProcessor {

        @Override
        public boolean processBeforeRelease(FinalProjectConfig config, String flowBasePath) throws IOException {
            // 判断指定tag的image是否存在
            String reuseImageName = config.getImageName(config.getAppendProfile().getDocker().getRegistryHost(),
                    config.getAppendProfile().getNamespace(), config.getAppName(), DEPLOYED_TAG);
            if (DockerHelper.inst(config.getId() + DevOps.APPEND_FLAG).registry.exist(reuseImageName)) {
                // 从目标环境的镜像仓库拉取镜像到本地
                DockerHelper.inst(config.getId() + DevOps.APPEND_FLAG).image.pull(reuseImageName, true);
                DockerHelper.inst(config.getId()).image.copy(reuseImageName, config.getCurrImageName());
                return true;
            }
            return false;
        }

        @Override
        public void processAfterReleaseSuccessful(FinalProjectConfig config) {
            if (config.getDocker().getRegistryUrl() == null
                    || config.getDocker().getRegistryUrl().isEmpty()) {
                logger.warn("Not found docker registry url and push is ignored, which is mostly used for stand-alone testing");
            } else {
                String tagImageName = config.getImageName(DEPLOYED_TAG);
                DockerHelper.inst(config.getId()).image.pull(config.getCurrImageName(), true);
                DockerHelper.inst(config.getId()).image.copy(config.getCurrImageName(), tagImageName);
                logger.info("Pushing available latest image : {}", config.getCurrImageName());
                DockerHelper.inst(config.getId()).image.push(tagImageName, true);
            }
        }

        @Override
        public Optional<String> getReuseCommit(FinalProjectConfig config) throws IOException {
            // TODO
            return Optional.empty();
        }
    }

}
