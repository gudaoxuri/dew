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

package group.idealworld.dew.devops.kernel.helper;

import org.slf4j.Logger;

/**
 * Kubernetes操作函数类.
 *
 * @author gudaoxuri
 * @see <a href="https://github.com/kubernetes-client/java">Kubernetes Client</a>
 */
public class KubeHelper extends MultiInstProcessor {

    /**
     * Init.
     *
     * @param instanceId       the instance id
     * @param log              the log
     * @param base64KubeConfig the base 64 kube config
     */
    public static void init(String instanceId, Logger log, String base64KubeConfig) {
        multiInit("KUBE", instanceId,
                () -> new KubeOpt(log, base64KubeConfig), base64KubeConfig);
    }

    /**
     * Fetch KubeOpt instance.
     *
     * @param instanceId the instance id
     * @return KubeOpt instance
     */
    public static KubeOpt inst(String instanceId) {
        return multiInst("KUBE", instanceId);
    }

}

