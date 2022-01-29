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

package group.idealworld.dew.devops.kernel.flow;

import group.idealworld.dew.devops.kernel.DevOps;
import group.idealworld.dew.devops.kernel.config.FinalProjectConfig;
import group.idealworld.dew.devops.kernel.util.DewLog;
import io.kubernetes.client.openapi.ApiException;
import org.slf4j.Logger;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Basic flow.
 *
 * @author gudaoxuri
 */
public abstract class BasicFlow {

    /**
     * The Logger.
     */
    protected Logger logger = DewLog.build(this.getClass());

    /**
     * 执行流程.
     *
     * @param projectId the project id
     * @param mojoName  the mojo name
     * @return the process result
     * @throws ApiException the api exception
     * @throws IOException  the io exception
     */
    public final boolean exec(String projectId, String mojoName) throws ApiException, IOException {
        FinalProjectConfig config = DevOps.Config.getProjectConfig(projectId);
        logger.debug("Executing " + this.getClass().getSimpleName());
        // 为每个mojo创建输出目录
        String flowBasePath = config.getTargetDirectory() + "dew_" + mojoName + File.separator;
        Files.createDirectories(Paths.get(flowBasePath));
        preProcess(config, flowBasePath);
        process(config, flowBasePath);
        if (!postProcess(config, flowBasePath)) {
            logger.debug("Finished,but [postProcess] is false");
            return false;
        }
        return true;
    }

    /**
     * Process.
     *
     * @param config       the project config
     * @param flowBasePath the flow base path
     * @throws ApiException the api exception
     * @throws IOException  the io exception
     */
    protected abstract void process(FinalProjectConfig config, String flowBasePath) throws ApiException, IOException;

    /**
     * Pre process.
     *
     * @param config       the project config
     * @param flowBasePath the flow base path
     * @throws ApiException the api exception
     * @throws IOException  the io exception
     */
    protected void preProcess(FinalProjectConfig config, String flowBasePath) throws ApiException, IOException {
    }

    /**
     * Post process.
     *
     * @param config       the project config
     * @param flowBasePath the flow base path
     * @return the process result
     * @throws ApiException the api exception
     * @throws IOException  the io exception
     */
    protected boolean postProcess(FinalProjectConfig config, String flowBasePath) throws ApiException, IOException {
        return true;
    }

}
