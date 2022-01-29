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
 * Docker操作函数类.
 *
 * @author gudaoxuri
 * @see <a href="https://github.com/docker-java/docker-java/wiki">Docker Java 操作</a>
 */
public class DockerHelper extends MultiInstProcessor {

    /**
     * Init.
     *
     * @param instanceId       实例Id
     * @param log              日志对象
     * @param host             DOCKER_HOST, e.g. tcp://10.200.131.182:2375
     * @param registryUrl      registry地址， e.g. https://harbor.dew.idealworld.group/v2
     * @param registryUsername registry用户名
     * @param registryPassword registry密码
     * @see <a href="https://docs.docker.com/install/linux/linux-postinstall/#configure-where-the-docker-daemon-listens-for-connections">The Docker Daemon Listens For Connections</a>
     */
    public static void init(String instanceId, Logger log, String host, String registryUrl, String registryUsername, String registryPassword) {
        multiInit("DOCKER", instanceId,
                () -> new DockerOpt(log, host, registryUrl, registryUsername, registryPassword),
                host, registryUrl, registryUsername);
    }

    /**
     * Fetch DockerOpt instance.
     *
     * @param instanceId the instance id
     * @return DockerOpt instance
     */
    public static DockerOpt inst(String instanceId) {
        return multiInst("DOCKER", instanceId);
    }

}
