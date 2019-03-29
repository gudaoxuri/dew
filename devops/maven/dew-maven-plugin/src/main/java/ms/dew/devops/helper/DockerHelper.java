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
 * Docker操作函数类
 *
 * @link https://github.com/docker-java/docker-java/wiki
 */
public class DockerHelper extends MultiInstProcessor {

    /**
     * @param instanceId       实例Id
     * @param log              日志对象
     * @param host             DOCKER_HOST, e.g. tcp://10.200.131.182:2375
     * @param registryUrl      registry地址， e.g. https://harbor.dew.env/v2
     * @param registryUsername registry用户名
     * @param registryPassword registry密码
     * @link https://docs.docker.com/install/linux/linux-postinstall/#configure-where-the-docker-daemon-listens-for-connections
     */
    public static void init(String instanceId, Log log, String host, String registryUrl, String registryUsername, String registryPassword) {
        multiInit(instanceId,
                () -> new DockerOpt(log, host, registryUrl, registryUsername, registryPassword),
                host, registryUrl, registryUsername);
    }

    public static DockerOpt inst(String instanceId) {
        return multiInst(instanceId);
    }

}
