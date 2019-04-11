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

import java.io.IOException;

/**
 * Basic prepare flow.
 *
 * @author gudaoxuri
 */
public abstract class BasicPrepareFlow extends BasicFlow {

    /**
     * Pre prepare build.
     *
     * @param config       the project config
     * @param flowBasePath the flow base path
     * @return build result
     * @throws IOException the io exception
     */
    protected boolean prePrepareBuild(FinalProjectConfig config, String flowBasePath) throws IOException {
        return true;
    }

    @Override
    protected boolean process(FinalProjectConfig config, String flowBasePath) throws IOException {
        if (!config.getReuseLastVersionFromProfile().isEmpty()) {
            // 重用模式下不用再执行准备操作
            return true;
        }
        // 先判断是否存在
        if (!DockerHelper.inst(config.getId()).registry.exist(config.getCurrImageName())
                && !prePrepareBuild(config, flowBasePath)) {
            Dew.log.debug("Finished,because [prePrepareBuild] is false");
            return false;
        }
        return true;
    }

}
