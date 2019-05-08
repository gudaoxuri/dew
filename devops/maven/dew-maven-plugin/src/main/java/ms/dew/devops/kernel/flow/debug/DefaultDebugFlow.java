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

package ms.dew.devops.kernel.flow.debug;

import io.kubernetes.client.ApiException;
import ms.dew.devops.kernel.helper.KubeHelper;
import ms.dew.devops.kernel.config.FinalProjectConfig;
import ms.dew.devops.kernel.flow.BasicFlow;
import ms.dew.devops.kernel.function.PodSelector;
import ms.dew.devops.kernel.resource.KubeDeploymentBuilder;

import java.io.IOException;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;

/**
 * Default debug flow.
 *
 * @author gudaoxuri
 */
public class DefaultDebugFlow extends BasicFlow {

    private String podName;
    private int forwardPort;

    /**
     * Instantiates a new Default log flow.
     *
     * @param podName     the pod name
     * @param forwardPort the forward port
     */
    public DefaultDebugFlow(String podName, int forwardPort) {
        this.podName = podName;
        this.forwardPort = forwardPort;
    }

    @Override
    protected void process(FinalProjectConfig config, String flowBasePath) throws ApiException, IOException {
        podName = PodSelector.select(config, Optional.ofNullable(podName));
        KubeHelper.inst(config.getId())
                .exec(podName, KubeDeploymentBuilder.FLAG_CONTAINER_NAME, config.getNamespace(),
                        new String[]{
                                "./debug-java.sh"
                        }, System.out::println);
        KubeHelper.inst(config.getId())
                .forward(podName, config.getNamespace(), 5005, forwardPort);
        System.out.println("==================\n"
                + "-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=5005\n"
                + "==================");
        try {
            // 等待手工停止
            new CountDownLatch(1).await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
