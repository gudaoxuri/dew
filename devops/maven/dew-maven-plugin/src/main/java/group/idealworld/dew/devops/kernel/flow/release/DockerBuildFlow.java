/*
 * Copyright 2020. the original author or authors.
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

import com.ecfront.dew.common.$;
import group.idealworld.dew.devops.kernel.DevOps;
import group.idealworld.dew.devops.kernel.flow.BasicFlow;
import group.idealworld.dew.devops.kernel.helper.DockerHelper;
import group.idealworld.dew.devops.kernel.helper.DockerOpt;
import group.idealworld.dew.devops.kernel.config.FinalProjectConfig;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

import static group.idealworld.dew.devops.kernel.config.DewProfile.REUSE_VERSION_TYPE_LABEL;
import static group.idealworld.dew.devops.kernel.config.DewProfile.REUSE_VERSION_TYPE_TAG;

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

    protected Map<String, String> packageDockerFileArg(FinalProjectConfig config) {
        return packageDockerFileArg(config);
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
        DockerHelper.inst(config.getId()).image.build(config.getCurrImageName(), flowBasePath, packageDockerFileArg(config));
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
         */
        static boolean processBeforeRelease(FinalProjectConfig config, String flowBasePath) {
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
         */
        static void processAfterReleaseSuccessful(FinalProjectConfig config) {
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
         * @return the boolean
         */
        public static Optional<String> getReuseCommit(FinalProjectConfig config) {
            switch (config.getReuseVersionType()) {
                case REUSE_VERSION_TYPE_LABEL:
                    return reuseVersionLabelProcessor.getReuseCommit(config);
                case REUSE_VERSION_TYPE_TAG:
                    return reuseVersionTagProcessor.getReuseCommit(config);
                default:
                    return Optional.empty();
            }
        }

        /**
         * Exists reuse version.
         *
         * @param config the config
         * @return the boolean
         */
        public static boolean existsReuseVersion(FinalProjectConfig config) {
            switch (config.getReuseVersionType()) {
                case REUSE_VERSION_TYPE_LABEL:
                    return reuseVersionLabelProcessor.existsReuseVersion(config);
                case REUSE_VERSION_TYPE_TAG:
                    return reuseVersionTagProcessor.existsReuseVersion(config);
                default:
                    return false;
            }
        }
    }

    private interface ReuseVersionProcessor {

        Logger logger = LoggerFactory.getLogger(ReuseVersionProcessor.class);

        boolean processBeforeRelease(FinalProjectConfig config, String flowBasePath) throws IOException;

        void processAfterReleaseSuccessful(FinalProjectConfig config) throws IOException;

        Optional<String> getReuseCommit(FinalProjectConfig config) throws IOException;

        boolean existsReuseVersion(FinalProjectConfig config);

    }

    private static class ReuseVersionLabelProcessor implements ReuseVersionProcessor {

        @Override
        public boolean processBeforeRelease(FinalProjectConfig config, String flowBasePath) {
            // 判断是否有最近可用的image
            String reuseImageName = getImageNameByLabelName(config);
            if (StringUtils.isEmpty(reuseImageName)) {
                return true;
            }
            if (!DockerHelper.inst(config.getId() + DevOps.APPEND_FLAG).registry.exist(reuseImageName)) {
                return true;
            }
            logger.info("Reuse image : " + reuseImageName);
            DockerHelper.inst(config.getId() + DevOps.APPEND_FLAG).image.pull(reuseImageName, true);
            DockerHelper.inst(config.getId()).image.copy(reuseImageName, config.getCurrImageName());
            return false;
        }

        @Override
        public void processAfterReleaseSuccessful(FinalProjectConfig config) {
            logger.info("Add label : " + config.getCurrImageName());
            String projectName = config.getNamespace();
            if (config.getNamespace().contains(config.getProfile())) {
                projectName = config.getNamespace().substring(config.getNamespace().indexOf("-") + 1);
            }
            Integer projectId = DockerHelper.inst(config.getId()).registry.getProjectIdByName(projectName);
            DockerOpt.Label label = DockerHelper.inst(config.getId()).registry
                    .getLabelByName(config.getAppName() + "-" + config.getProfile(), projectId);
            if (label == null) {
                label = new DockerOpt.Label();
                label.setName(config.getAppName() + "-" + config.getProfile());
                label.setDescription(config.getCurrImageName());
                label.setProjectId(projectId);
                DockerHelper.inst(config.getId()).registry.addLabel(label);
            } else {
                label.setDescription(config.getCurrImageName());
                DockerHelper.inst(config.getId()).registry.updateLabelById(label.getId(), label);
            }
        }

        @Override
        public Optional<String> getReuseCommit(FinalProjectConfig config) {
            String imageName = getImageNameByLabelName(config);
            if (StringUtils.isEmpty(imageName)) {
                return Optional.empty();
            }
            return Optional.of(imageName.substring(imageName.lastIndexOf(":") + 1));
        }

        @Override
        public boolean existsReuseVersion(FinalProjectConfig config) {
            String projectName = config.getProjectName();
            if (StringUtils.isBlank(projectName) && config.getAppendProfile().getNamespace().contains(config.getReuseLastVersionFromProfile())) {
                projectName = config.getAppendProfile().getNamespace().substring(config.getAppendProfile().getNamespace().indexOf("-") + 1);
            }
            Integer projectId = DockerHelper.inst(config.getId()).registry.getProjectIdByName(projectName);
            DockerOpt.Label label = DockerHelper.inst(config.getId() + DevOps.APPEND_FLAG).registry
                    .getLabelByName(config.getAppName() + "-" + config.getReuseLastVersionFromProfile(), projectId);
            if (null == label) {
                return false;
            }
            return DockerHelper.inst(config.getId() + DevOps.APPEND_FLAG).registry.exist(label.getDescription());
        }

        private String getImageNameByLabelName(FinalProjectConfig config) {
            Integer projectId = DockerHelper.inst(config.getId() + DevOps.APPEND_FLAG).registry
                    .getProjectIdByName(StringUtils.isBlank(config.getProjectName()) ? config.getAppendProfile().getNamespace()
                            .substring(config.getAppendProfile().getNamespace().indexOf("-") + 1) : config.getProjectName());
            String reuseImageName = null;
            DockerOpt.Label label = DockerHelper.inst(config.getId() + DevOps.APPEND_FLAG).registry
                    .getLabelByName(config.getAppName() + "-" + config.getReuseLastVersionFromProfile(), projectId);
            if (label != null) {
                reuseImageName = label.getDescription();
            }
            return reuseImageName;
        }
    }

    private static class ReuseVersionTagProcessor implements ReuseVersionProcessor {

        @Override
        public boolean processBeforeRelease(FinalProjectConfig config, String flowBasePath) {
            // 判断指定tag的image是否存在
            String reuseImageName = config.getImageName(config.getAppendProfile().getDocker().getRegistryHost(),
                    config.getProjectName(), config.getAppendProfile().getNamespace(), config.getAppName(),
                    DEPLOYED_TAG, config.getReuseLastVersionFromProfile());
            if (DockerHelper.inst(config.getId() + DevOps.APPEND_FLAG).registry.exist(reuseImageName)) {
                // 从目标环境的镜像仓库拉取镜像到本地
                DockerHelper.inst(config.getId() + DevOps.APPEND_FLAG).image.pull(reuseImageName, true);
                DockerHelper.inst(config.getId()).image.copy(reuseImageName, config.getCurrImageName());
                return false;
            }
            return true;
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
        public Optional<String> getReuseCommit(FinalProjectConfig config) {
            String namespace = config.getAppendProfile().getNamespace();
            if (config.getAppendProfile().getNamespace().contains(config.getReuseLastVersionFromProfile())) {
                namespace = config.getAppendProfile().getNamespace().substring(config.getAppendProfile().getNamespace().indexOf("-") + 1);
            }
            namespace = namespace + "/" + config.getReuseLastVersionFromProfile();
            List<DockerOpt.Tag> tags = DockerHelper.inst(config.getId() + DevOps.APPEND_FLAG).registry.getTags(
                    namespace, config.getAppName());
            if (CollectionUtils.isEmpty(tags)) {
                return Optional.empty();
            }
            AtomicReference<Optional<String>> reuseCommitOptional = new AtomicReference<>(Optional.empty());
            tags.stream().filter(tag -> tag.getName().equals(DEPLOYED_TAG)).findFirst().ifPresent(deployedTag ->
                    tags.stream().filter(tag -> tag.getCreated().getTime() == deployedTag.getCreated().getTime())
                            .findFirst().ifPresent(tag -> reuseCommitOptional.set(Optional.of(tag.getName()))));
            return reuseCommitOptional.get();
        }

        @Override
        public boolean existsReuseVersion(FinalProjectConfig config) {
            return DockerHelper.inst(config.getId() + DevOps.APPEND_FLAG).registry
                    .exist(config.getImageName(config.getAppendProfile().getDocker().getRegistryHost(),
                            config.getProjectName(), config.getAppendProfile().getNamespace(), config.getAppName(),
                            DEPLOYED_TAG, config.getReuseLastVersionFromProfile()));
        }
    }

}
