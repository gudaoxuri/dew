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

package com.tairanchina.csp.dew.helper;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.BuildImageCmd;
import com.github.dockerjava.api.command.PullImageCmd;
import com.github.dockerjava.api.command.PushImageCmd;
import com.github.dockerjava.api.model.AuthConfig;
import com.github.dockerjava.api.model.BuildResponseItem;
import com.github.dockerjava.api.model.PullResponseItem;
import com.github.dockerjava.api.model.PushResponseItem;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientBuilder;
import com.github.dockerjava.core.command.BuildImageResultCallback;
import com.github.dockerjava.core.command.PullImageResultCallback;
import com.github.dockerjava.core.command.PushImageResultCallback;
import org.apache.maven.plugin.logging.Log;

import java.io.File;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Docker操作函数类
 *
 * @link https://github.com/docker-java/docker-java/wiki
 */
public class DockerHelper {

    private static Log log;

    private static DockerClient docker;

    private static AuthConfig defaultAuthConfig;

    /**
     * @param log              日志对象
     * @param host             DOCKER_HOST, e.g. tcp://10.200.131.182:2375
     * @param registryUrl      registry地址， e.g. https://harbor.dew.env/v2
     * @param registryUsername registry用户名
     * @param registryPassword registry密码
     * @link https://docs.docker.com/install/linux/linux-postinstall/#configure-where-the-docker-daemon-listens-for-connections
     */
    public static void init(Log log, String host, String registryUrl, String registryUsername, String registryPassword) {
        DockerHelper.log = log;
        if (docker == null) {
            DefaultDockerClientConfig.Builder builder = DefaultDockerClientConfig.createDefaultConfigBuilder();
            if (host != null && !host.isEmpty()) {
                builder.withDockerHost(host);
            }
            if (registryUrl != null) {
                defaultAuthConfig = new AuthConfig()
                        .withRegistryAddress(registryUrl)
                        .withUsername(registryUsername)
                        .withPassword(registryPassword);
            }
            docker = DockerClientBuilder.getInstance(builder.build()).build();
        }
    }

    public static class Image {

        public static void pull(String imageName, boolean auth) {
            pull(imageName, auth, Long.MAX_VALUE);
        }

        public static void pull(String imageName, boolean auth, long awaitSec) {
            PullImageCmd pullImageCmd = docker.pullImageCmd(imageName);
            if (auth) {
                pullImageCmd.withAuthConfig(defaultAuthConfig);
            }
            try {
                pullImageCmd.exec(new PullImageResultCallback() {
                    @Override
                    public void onNext(PullResponseItem item) {
                        super.onNext(item);
                        log.debug(item.toString());
                    }
                }).awaitCompletion(awaitSec, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                log.error("Pull image error.", e);
            }
        }

        public static List<com.github.dockerjava.api.model.Image> list() {
            return docker.listImagesCmd().exec();
        }

        public static List<com.github.dockerjava.api.model.Image> list(String imageName) {
            return docker.listImagesCmd().withImageNameFilter(imageName).exec();
        }

        public static String build(String imageName, String dockerfilePath) {
            return build(imageName, dockerfilePath, null);
        }

        public static String build(String imageName, String dockerfilePath, Map<String, String> args) {
            BuildImageCmd buildImageCmd = docker.buildImageCmd(new File(dockerfilePath));
            if (args != null && !args.isEmpty()) {
                args.forEach(buildImageCmd::withBuildArg);
            }
            buildImageCmd.withTags(new HashSet<String>() {{
                add(imageName);
            }});
            return buildImageCmd.exec(new BuildImageResultCallback() {
                @Override
                public void onNext(BuildResponseItem item) {
                    super.onNext(item);
                    log.debug(item.toString());
                }
            }).awaitImageId();
        }

        public static void push(String imageName, boolean auth) {
            push(imageName, auth, Long.MAX_VALUE);
        }

        public static void push(String imageName, boolean auth, long awaitSec) {
            PushImageCmd pushImageCmd = docker.pushImageCmd(imageName);
            if (auth) {
                pushImageCmd.withAuthConfig(defaultAuthConfig);
            }
            try {
                pushImageCmd.exec(new PushImageResultCallback() {
                    @Override
                    public void onNext(PushResponseItem item) {
                        super.onNext(item);
                        log.debug(item.toString());
                    }
                }).awaitCompletion(awaitSec, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                log.error("Push image error.", e);
            }
        }

        public static void remove(String imageId) {
            docker.removeImageCmd(imageId).withForce(true).exec();
        }

    }

}
