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

package ms.dew.devops.helper;

import org.apache.maven.plugin.logging.Log;

/**
 * Kubernetes操作函数类
 *
 * @link https://github.com/kubernetes-client/java
 */
public class KubeHelper extends MultiInstProcessor {

    public static void init(String instanceId, Log log, String base64KubeConfig) {
        multiInit(instanceId,
                () -> new KubeOpt(log, base64KubeConfig), base64KubeConfig);
    }

    public static KubeOpt inst(String instanceId) {
        return multiInst(instanceId);
    }

}

