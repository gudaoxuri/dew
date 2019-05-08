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

package ms.dew.devops.kernel.helper;

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
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Docker操作函数类.
 *
 * @author gudaoxuri
 * @link https ://github.com/docker-java/docker-java/wiki
 */
public class DockerOpt {

    /**
     * Image operation.
     */
    public Image image = new Image();
    /**
     * Registry operation.
     */
    public Registry registry = new Registry();
    /**
     * Log.
     */
    protected Log log;
    /**
     * Docker native client.
     */
    private DockerClient docker;
    /**
     * The Default auth config.
     */
    private AuthConfig defaultAuthConfig;
    /**
     * The Registry api url.
     */
    private String registryApiUrl;
    /**
     * The Registry password.
     */
    private String registryPassword;
    /**
     * The Registry username.
     */
    private String registryUsername;

    /**
     * Instantiates a new Docker opt.
     *
     * @param log              日志对象
     * @param host             DOCKER_HOST, e.g. tcp://10.200.131.182:2375
     * @param registryUrl      registry地址， e.g. https://harbor.dew.env/v2
     * @param registryUsername registry用户名
     * @param registryPassword registry密码
     * @link https ://docs.docker.com/install/linux/linux-postinstall/#configure-where-the-docker-daemon-listens-for-connections
     */
    protected DockerOpt(Log log, String host, String registryUrl, String registryUsername, String registryPassword) {
        this.log = log;
        this.registryUsername = registryUsername;
        this.registryPassword = registryPassword;
        DefaultDockerClientConfig.Builder builder = DefaultDockerClientConfig.createDefaultConfigBuilder();
        if (host != null && !host.isEmpty()) {
            builder.withDockerHost(host);
        }
        if (registryUrl != null) {
            registryUrl = registryUrl.endsWith("/") ? registryUrl.substring(0, registryUrl.length() - 1) : registryUrl;
            registryApiUrl = registryUrl.substring(0, registryUrl.lastIndexOf("/") + 1) + "api";
            defaultAuthConfig = new AuthConfig()
                    .withRegistryAddress(registryUrl)
                    .withUsername(registryUsername)
                    .withPassword(registryPassword);
        }
        docker = DockerClientBuilder.getInstance(builder.build()).build();
    }

    /**
     * Image operation.
     */
    public class Image {

        /**
         * Pull.
         *
         * @param imageName the image name
         * @param auth      the auth
         */
        public void pull(String imageName, boolean auth) {
            pull(imageName, auth, Long.MAX_VALUE);
        }

        /**
         * Pull.
         *
         * @param imageName the image name
         * @param auth      the auth
         * @param awaitSec  the await sec
         */
        public void pull(String imageName, boolean auth, long awaitSec) {
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

        /**
         * Copy.
         *
         * @param originImageName origin image name
         * @param newImageName    new image name
         */
        public void copy(String originImageName, String newImageName) {
            String[] newImageFragment = newImageName.split(":");
            if (!originImageName.contains(":")) {
                originImageName += ":latest";
            }
            newImageName = newImageFragment[0];
            String newTag = newImageFragment.length == 2 ? newImageFragment[1] : "latest";
            docker.tagImageCmd(originImageName, newImageName, newTag).exec();
        }

        /**
         * List.
         *
         * @return image list
         */
        public List<com.github.dockerjava.api.model.Image> list() {
            return docker.listImagesCmd().exec();
        }

        /**
         * List.
         *
         * @param imageName the image name
         * @return image list
         */
        public List<com.github.dockerjava.api.model.Image> list(String imageName) {
            return docker.listImagesCmd().withImageNameFilter(imageName).exec();
        }

        /**
         * Build.
         *
         * @param imageName      the image name
         * @param dockerfilePath the dockerfile path
         * @return image id
         */
        public String build(String imageName, String dockerfilePath) {
            return build(imageName, dockerfilePath, null);
        }

        /**
         * Build string.
         *
         * @param imageName      the image name
         * @param dockerfilePath the dockerfile path
         * @param args           the args
         * @return image id
         */
        public String build(String imageName, String dockerfilePath, Map<String, String> args) {
            BuildImageCmd buildImageCmd = docker.buildImageCmd(new File(dockerfilePath));
            if (args != null && !args.isEmpty()) {
                args.forEach(buildImageCmd::withBuildArg);
            }
            buildImageCmd.withTags(new HashSet<String>() {
                {
                    add(imageName);
                }
            });
            return buildImageCmd.exec(new BuildImageResultCallback() {
                @Override
                public void onNext(BuildResponseItem item) {
                    super.onNext(item);
                    log.debug(item.toString());
                }
            }).awaitImageId();
        }

        /**
         * Push.
         *
         * @param imageName the image name
         * @param auth      the auth
         */
        public void push(String imageName, boolean auth) {
            push(imageName, auth, Long.MAX_VALUE);
        }

        /**
         * Push.
         *
         * @param imageName the image name
         * @param auth      the auth
         * @param awaitSec  the await sec
         */
        public void push(String imageName, boolean auth, long awaitSec) {
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

        /**
         * Remove.
         *
         * @param imageName the image name
         */
        public void remove(String imageName) {
            List<com.github.dockerjava.api.model.Image> images = list(imageName);
            if (!images.isEmpty()) {
                removeById(images.get(0).getId());
            }
        }

        /**
         * Remove by id.
         *
         * @param imageId the image id
         */
        public void removeById(String imageId) {
            docker.removeImageCmd(imageId).withForce(true).exec();
        }

    }

    /**
     * Harbor Registry API.
     *
     * @link https ://raw.githubusercontent.com/goharbor/harbor/master/docs/swagger.yaml
     */
    public class Registry {

        /**
         * Exist.
         *
         * @param imageName the image name
         * @return <b>true</b> if exist
         * @throws IOException the io exception
         */
        public boolean exist(String imageName) throws IOException {
            String[] item = parseImageInfo(imageName);
            HttpHelper.ResponseWrap responseWrap = $.http.getWrap(registryApiUrl + "/repositories/" + item[0] + "/tags/" + item[1], wrapHeader());
            log.debug("Registry exist image result [" + responseWrap.statusCode + "]" + responseWrap.result);
            return responseWrap.statusCode == 200;
        }

        /**
         * Remove.
         *
         * @param imageName the image name
         * @return <b>true</b> if success
         * @throws IOException the io exception
         */
        public boolean remove(String imageName) throws IOException {
            String[] item = parseImageInfo(imageName);
            HttpHelper.ResponseWrap responseWrap = $.http.deleteWrap(registryApiUrl + "/repositories/" + item[0] + "/tags/" + item[1], wrapHeader());
            boolean result = responseWrap.statusCode == 200;
            if (result) {
                log.debug("Registry remove image result [" + responseWrap.statusCode + "]" + responseWrap.result);
            } else {
                log.error("Registry remove image result [" + responseWrap.statusCode + "]" + responseWrap.result);
            }
            return result;
        }

        private String[] parseImageInfo(String imageName) {
            String[] imageFragment = imageName.split(":");
            String tag = imageFragment.length == 2 ? imageFragment[1] : "latest";
            if (imageName.split("/").length == 1) {
                // 不带host
                return new String[]{imageFragment[0], tag};
            } else {
                return new String[]{imageFragment[0].substring(imageFragment[0].indexOf("/") + 1), tag};
            }
        }

        private Map<String, String> wrapHeader() {
            Map<String, String> header = new HashMap<>();
            try {
                header.put("Content-Type", "application/json");
                header.put("accept", "application/json");
                header.put("authorization", "Basic " + $.security.encodeStringToBase64(registryUsername + ":" + registryPassword, "UTF-8"));
            } catch (UnsupportedEncodingException ignore) {
                throw new RuntimeException(ignore);
            }
            return header;
        }

    }

}
