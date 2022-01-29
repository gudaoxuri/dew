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

import com.ecfront.dew.common.$;
import group.idealworld.dew.devops.kernel.DevOps;
import group.idealworld.dew.devops.kernel.config.FinalProjectConfig;
import group.idealworld.dew.devops.kernel.flow.BasicFlow;
import group.idealworld.dew.devops.kernel.helper.DockerHelper;
import group.idealworld.dew.devops.kernel.helper.DockerOpt;
import group.idealworld.dew.devops.kernel.util.DewLog;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Optional;

/**
 * Docker build flow.
 *
 * @author gudaoxuri
 */
public abstract class DockerBuildFlow extends BasicFlow {

    /**
     * The constant logger.
     */
    protected static Logger logger = DewLog.build(DockerBuildFlow.class);

    /**
     * Pre docker build.
     *
     * @param config       the project config
     * @param flowBasePath the flow base path
     * @throws IOException the io exception
     */
    protected abstract void preDockerBuild(FinalProjectConfig config, String flowBasePath) throws IOException;

    /**
     * Package docker file arg map.
     *
     * @param config the config
     * @return the map
     */
    protected abstract Map<String, String> packageDockerFileArg(FinalProjectConfig config);

    @Override
    protected void process(FinalProjectConfig config, String flowBasePath) throws IOException {
        // 先判断是否存在
        if (DockerHelper.inst(config.getId()).registry.existImage(config.getCurrImageName())) {
            logger.info("Ignore build, because image " + config.getCurrImageName() + " already exist");
            return;
        }
        boolean reused = false;
        if (!config.getDisableReuseVersion()) {
            // 版本复用
            String reuseImageName = getImageNameByLabelName(config);
            if (!StringUtils.isEmpty(reuseImageName)
                    && DockerHelper.inst(config.getId() + DevOps.APPEND_FLAG).registry.existImage(reuseImageName)) {
                logger.info("Reuse image : " + reuseImageName);
                DockerHelper.inst(config.getId() + DevOps.APPEND_FLAG).image.pull(reuseImageName, true);
                DockerHelper.inst(config.getId()).image.copy(reuseImageName, config.getCurrImageName());
                reused = true;
            }
        }
        if (!reused) {
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
            dockerFileContent = dockerFileContent.replaceAll("FROM [^\\s]*", "FROM " + config.getDocker().getImage().trim());
            Files.write(Paths.get(flowBasePath + "Dockerfile"), dockerFileContent.getBytes());
        }
        DockerHelper.inst(config.getId()).image.build(config.getCurrImageName(),
                flowBasePath,
                packageDockerFileArg(config));
    }

    /**
     * Process after release successful.
     *
     * @param config the config
     */
    public static void processAfterReleaseSuccessful(FinalProjectConfig config) {
        if (config.getDocker().getRegistryUrl() == null
                || config.getDocker().getRegistryUrl().isEmpty()) {
            logger.warn("Not found docker registry url and push is ignored, which is mostly used for stand-alone testing");
        } else {
            logger.info("Add label : " + config.getCurrImageName());
            DockerHelper.inst(config.getId()).registry
                    .createOrUpdateLabel(config.getAppName() + "-" + config.getProfile(),
                            config.getCurrImageName(),
                            Optional.of(config.getNamespace()));
        }
    }

    /**
     * Gets reuse commit.
     *
     * @param config the config
     * @return the reuse commit
     */
    public static Optional<String> getReuseCommit(FinalProjectConfig config) {
        String imageName = getImageNameByLabelName(config);
        if (StringUtils.isEmpty(imageName)) {
            return Optional.empty();
        }
        return Optional.of(imageName.substring(imageName.lastIndexOf(":") + 1));
    }

    /**
     * Exists reuse version.
     *
     * @param config the config
     * @return the boolean
     */
    public static boolean existsReuseVersion(FinalProjectConfig config) {
        DockerOpt.Label label = DockerHelper.inst(config.getId() + DevOps.APPEND_FLAG).registry
                .getLabel(config.getAppName() + "-" + config.getReuseLastVersionFromProfile(),
                        Optional.of(config.getAppendProfile().getNamespace()));
        if (null == label) {
            return false;
        }
        return DockerHelper.inst(config.getId() + DevOps.APPEND_FLAG).registry.existImage(label.getDescription());
    }

    private static String getImageNameByLabelName(FinalProjectConfig config) {
        DockerOpt.Label label = DockerHelper.inst(config.getId() + DevOps.APPEND_FLAG).registry
                .getLabel(config.getAppName() + "-" + config.getReuseLastVersionFromProfile(),
                        Optional.of(config.getAppendProfile().getNamespace()));
        if (label != null) {
            return label.getDescription();
        }
        return null;
    }

}
