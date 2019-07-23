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

package ms.dew.devops.kernel.flow;

import io.kubernetes.client.ApiException;
import ms.dew.devops.kernel.config.FinalProjectConfig;

import java.io.IOException;

/**
 * 不需要执行的缺省流程定义.
 *
 * @author gudaoxuri
 */
public class NoNeedProcessFLow extends BasicFlow {
    @Override
    protected void process(FinalProjectConfig config, String flowBasePath) throws ApiException, IOException {

    }
}
