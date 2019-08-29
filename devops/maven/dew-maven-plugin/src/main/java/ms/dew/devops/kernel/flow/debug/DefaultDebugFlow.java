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
import io.kubernetes.client.models.V1Service;
import io.kubernetes.client.models.V1ServicePort;
import ms.dew.devops.kernel.config.FinalProjectConfig;
import ms.dew.devops.kernel.flow.BasicFlow;
import ms.dew.devops.kernel.function.PodSelector;
import ms.dew.devops.kernel.helper.KubeHelper;
import ms.dew.devops.kernel.helper.KubeRES;
import ms.dew.devops.kernel.resource.KubeDeploymentBuilder;
import ms.dew.devops.kernel.resource.KubeServiceBuilder;
import sun.misc.Signal;
import sun.misc.SignalHandler;

import java.io.IOException;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.stream.Collectors;

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
        final Signal sig = new Signal(getOSSignalType());
        podName = PodSelector.select(config, Optional.ofNullable(podName));
        KubeHelper.inst(config.getId())
                .exec(podName, KubeDeploymentBuilder.FLAG_CONTAINER_NAME, config.getNamespace(),
                        new String[]{
                                "./debug-java.sh"
                        }, System.out::println);
        V1Service service = KubeHelper.inst(config.getId()).read(config.getAppName(), config.getNamespace(), KubeRES.SERVICE, V1Service.class);
        new KubeServiceBuilder().buildDebugPort(service, config.getApp().getDebugPort(), 5005);
        KubeHelper.inst(config.getId()).replace(service);
        service = KubeHelper.inst(config.getId()).read(config.getAppName(), config.getNamespace(), KubeRES.SERVICE, V1Service.class);
        System.out.println("Http-debug nodePort:["
                + service.getSpec().getPorts().stream().filter(v1ServicePort -> v1ServicePort.getPort().equals(5005))
                .map(V1ServicePort::getNodePort).collect(Collectors.toList()).get(0)
                + "].   Http-new nodePort: [" + service.getSpec().getPorts().stream().filter(v1ServicePort -> v1ServicePort.getPort().equals(9000))
                .map(V1ServicePort::getNodePort).collect(Collectors.toList()).get(0) + "]");
        System.out.println("==================\n"
                + "-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=5005\n"
                + "==================");
        Signal.handle(sig, new ShutdownHandler(service, config));
        try {
            // 等待手工停止
            new CountDownLatch(1).await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private String getOSSignalType() {
        return System.getProperties().getProperty("os.name").toLowerCase().startsWith("win") ? "INT" : "USR2";
    }

    class ShutdownHandler implements SignalHandler {

        public ShutdownHandler(V1Service service, FinalProjectConfig config) {
            registerShutdownHook(service, config);
        }

        public void handle(Signal signal) {

            if (signal.getName().equals("INT") || signal.getName().equals("USR2")) {
                Runtime.getRuntime().exit(0);
            }
        }

        private void registerShutdownHook(V1Service service, FinalProjectConfig config) {
            Thread t = new Thread(new ShutdownHook(service, config), "ShutdownHook-Thread");
            Runtime.getRuntime().addShutdownHook(t);
        }
    }

    class ShutdownHook implements Runnable {

        private V1Service service;

        private FinalProjectConfig config;

        public ShutdownHook(V1Service service, FinalProjectConfig config) {
            this.service = service;
            this.config = config;
        }

        @Override
        public void run() {
            System.out.println("Debug shutdown execute start...");
            new KubeServiceBuilder().clearDebugPort(service, config.getApp().getDebugPort(), 5005);
            try {
                KubeHelper.inst(config.getId()).replace(service);
                KubeHelper.inst(config.getId())
                        .exec(podName, KubeDeploymentBuilder.FLAG_CONTAINER_NAME, config.getNamespace(),
                                new String[]{
                                        "./debug-clear.sh"
                                }, System.out::println);
            } catch (ApiException | IOException e) {
                e.printStackTrace();
            }
            System.out.println("Debug shutdown execute end...");
        }
    }


}
