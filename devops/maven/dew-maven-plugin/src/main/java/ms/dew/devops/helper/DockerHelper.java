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

import com.ecfront.dew.common.$;
import com.ecfront.dew.common.HttpHelper;
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
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * Docker操作函数类
 *
 * @link https://github.com/docker-java/docker-java/wiki
 */
public class DockerHelper {

    private static final ConcurrentHashMap<String, String> EXISTS = new ConcurrentHashMap<>();
    private static final Map<String, Instance> INSTANCES = new HashMap<>();

    private static class Instance {

        public Instance(Log log, DockerClient docker, AuthConfig defaultAuthConfig, String registryApiUrl, String registryUsername, String registryPassword) {
            this.log = log;
            this.docker = docker;
            this.defaultAuthConfig = defaultAuthConfig;
            this.registryApiUrl = registryApiUrl;
            this.registryPassword = registryPassword;
            this.registryUsername = registryUsername;
        }

        private Log log;
        private DockerClient docker;
        private AuthConfig defaultAuthConfig;
        private String registryApiUrl;
        private String registryPassword;
        private String registryUsername;

    }

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
        try {
            String hash = $.security.digest.digest(host + registryUrl + registryUsername, "MD5");
            if (EXISTS.containsKey(hash)) {
                INSTANCES.put(instanceId, INSTANCES.get(EXISTS.get(hash)));
                return;
            }
            EXISTS.put(hash, instanceId);
        } catch (NoSuchAlgorithmException ignore) {
        }
        DockerClient docker;
        AuthConfig defaultAuthConfig = null;
        DefaultDockerClientConfig.Builder builder = DefaultDockerClientConfig.createDefaultConfigBuilder();
        if (host != null && !host.isEmpty()) {
            builder.withDockerHost(host);
        }
        String registryApiUrl = "";
        if (registryUrl != null) {
            registryUrl = registryUrl.endsWith("/") ? registryUrl.substring(0, registryUrl.length() - 1) : registryUrl;
            registryApiUrl = registryUrl.substring(0, registryUrl.lastIndexOf("/") + 1) + "api";
            defaultAuthConfig = new AuthConfig()
                    .withRegistryAddress(registryUrl)
                    .withUsername(registryUsername)
                    .withPassword(registryPassword);
        }
        docker = DockerClientBuilder.getInstance(builder.build()).build();
        INSTANCES.put(instanceId, new Instance(log, docker, defaultAuthConfig, registryApiUrl, registryUsername, registryPassword));
    }

    public static class Image {

        public static void pull(String imageName, boolean auth, String instanceId) {
            pull(imageName, auth, Long.MAX_VALUE, instanceId);
        }

        public static void pull(String imageName, boolean auth, long awaitSec, String instanceId) {
            Instance instance = INSTANCES.get(instanceId);
            PullImageCmd pullImageCmd = instance.docker.pullImageCmd(imageName);
            if (auth) {
                pullImageCmd.withAuthConfig(instance.defaultAuthConfig);
            }
            try {
                pullImageCmd.exec(new PullImageResultCallback() {
                    @Override
                    public void onNext(PullResponseItem item) {
                        super.onNext(item);
                        instance.log.debug(item.toString());
                    }
                }).awaitCompletion(awaitSec, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                instance.log.error("Pull image error.", e);
            }
        }

        public static List<com.github.dockerjava.api.model.Image> list(String instanceId) {
            return INSTANCES.get(instanceId).docker.listImagesCmd().exec();
        }

        public static List<com.github.dockerjava.api.model.Image> list(String imageName, String instanceId) {
            return INSTANCES.get(instanceId).docker.listImagesCmd().withImageNameFilter(imageName).exec();
        }

        public static String build(String imageName, String dockerfilePath, String instanceId) {
            return build(imageName, dockerfilePath, null, instanceId);
        }

        public static String build(String imageName, String dockerfilePath, Map<String, String> args, String instanceId) {
            Instance instance = INSTANCES.get(instanceId);
            BuildImageCmd buildImageCmd = instance.docker.buildImageCmd(new File(dockerfilePath));
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
                    instance.log.debug(item.toString());
                }
            }).awaitImageId();
        }

        public static void push(String imageName, boolean auth, String instanceId) {
            push(imageName, auth, Long.MAX_VALUE, instanceId);
        }

        public static void push(String imageName, boolean auth, long awaitSec, String instanceId) {
            Instance instance = INSTANCES.get(instanceId);
            PushImageCmd pushImageCmd = instance.docker.pushImageCmd(imageName);
            if (auth) {
                pushImageCmd.withAuthConfig(instance.defaultAuthConfig);
            }
            try {
                pushImageCmd.exec(new PushImageResultCallback() {
                    @Override
                    public void onNext(PushResponseItem item) {
                        super.onNext(item);
                        instance.log.debug(item.toString());
                    }
                }).awaitCompletion(awaitSec, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                instance.log.error("Push image error.", e);
            }
        }

        public static void remove(String imageName, String instanceId) {
            List<com.github.dockerjava.api.model.Image> images = list(imageName, instanceId);
            if (!images.isEmpty()) {
                INSTANCES.get(instanceId).docker.removeImageCmd(images.get(0).getId()).withForce(true).exec();
            }
        }

    }

    /**
     * Harbor Registry API
     *
     * @link https://raw.githubusercontent.com/goharbor/harbor/master/docs/swagger.yaml
     */
    public static class Registry {

        public static boolean exist(String imageName, String instanceId) throws IOException {
            String[] item = parseImageInfo(imageName);
            Instance instance = INSTANCES.get(instanceId);
            HttpHelper.ResponseWrap responseWrap = $.http.getWrap(instance.registryApiUrl + "/repositories/" + item[0] + "/tags/" + item[1], wrapHeader(instance));
            instance.log.debug("Registry exist image result [" + responseWrap.statusCode + "]" + responseWrap.result);
            return responseWrap.statusCode == 200;
        }

        public static boolean remove(String imageName, String instanceId) throws IOException {
            String[] item = parseImageInfo(imageName);
            Instance instance = INSTANCES.get(instanceId);
            HttpHelper.ResponseWrap responseWrap = $.http.deleteWrap(instance.registryApiUrl + "/repositories/" + item[0] + "/tags/" + item[1], wrapHeader(instance));
            boolean result = responseWrap.statusCode == 200;
            if (result) {
                instance.log.debug("Registry remove image result [" + responseWrap.statusCode + "]" + responseWrap.result);
            } else {
                instance.log.error("Registry remove image result [" + responseWrap.statusCode + "]" + responseWrap.result);
            }
            return result;
        }

        public static String[] parseImageInfo(String imageName) {
            String[] item = imageName.split(":");
            String tag = item[1];
            String imageNameWithoutHost = item[0].substring(item[0].indexOf("/") + 1);
            return new String[]{imageNameWithoutHost, tag};
        }

        private static Map<String, String> wrapHeader(Instance instance) {
            Map<String, String> header = new HashMap<>();
            try {
                header.put("Content-Type", "application/json");
                header.put("accept", "application/json");
                header.put("authorization", "Basic " + $.security.encodeStringToBase64(instance.registryUsername + ":" + instance.registryPassword, "UTF-8"));
            } catch (UnsupportedEncodingException ignore) {
            }
            return header;
        }

    }

}
