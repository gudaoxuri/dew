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

import com.ecfront.dew.common.$;
import com.ecfront.dew.common.HttpHelper;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.*;
import com.github.dockerjava.api.model.AuthConfig;
import com.github.dockerjava.api.model.BuildResponseItem;
import com.github.dockerjava.api.model.PullResponseItem;
import com.github.dockerjava.api.model.PushResponseItem;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientBuilder;
import com.github.dockerjava.core.command.PushImageResultCallback;
import org.slf4j.Logger;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * Docker操作函数类.
 *
 * @author gudaoxuri
 * @see <a href="https://github.com/docker-java/docker-java/wiki">Docker Java 操作</a>
 */
public class DockerOpt {

    /**
     * Image operation.
     */
    public Image image = new Image();
    /**
     * Registry operation.
     */
    public Registry registry;
    /**
     * Log.
     */
    protected Logger log;
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
     * @see <a href="https://docs.docker.com/install/linux/linux-postinstall/#configure-where-the-docker-daemon-listens-for-connections">The Docker Daemon Listens For Connections</a>
     */
    protected DockerOpt(Logger log,
                        String host, String registryUrl,
                        String registryUsername, String registryPassword) {
        this.log = log;
        this.registryUsername = registryUsername;
        this.registryPassword = registryPassword;
        DefaultDockerClientConfig.Builder builder = DefaultDockerClientConfig.createDefaultConfigBuilder();
        if (host != null && !host.isEmpty()) {
            builder.withDockerHost(host);
        }
        if (registryUrl != null) {
            registryUrl = registryUrl.endsWith("/") ? registryUrl.substring(0, registryUrl.length() - 1) : registryUrl;
            registryApiUrl = registryUrl.substring(0, registryUrl.lastIndexOf("/") + 1) + "api/v2.0";
            defaultAuthConfig = new AuthConfig()
                    .withRegistryAddress(registryUrl)
                    .withUsername(registryUsername)
                    .withPassword(registryPassword);
        }
        docker = DockerClientBuilder.getInstance(builder.build()).build();
        if (registryApiUrl.contains("github.com")) {
            registry = new GithubPackagesRegistry();
        } else {
            registry = new HarborRegistry();
        }
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
         * Build.
         *
         * @param imageName      the image name
         * @param dockerfilePath the dockerfile path
         * @param args           the args
         * @return image id
         */
        public String build(String imageName, String dockerfilePath, Map<String, String> args) {
            return build(imageName, dockerfilePath, args, null);
        }

        /**
         * Build.
         *
         * @param imageName      the image name
         * @param dockerfilePath the dockerfile path
         * @param args           the args
         * @param labels         the labels
         * @return image id
         */
        public String build(String imageName, String dockerfilePath, Map<String, String> args, Map<String, String> labels) {
            BuildImageCmd buildImageCmd = docker.buildImageCmd(new File(dockerfilePath));
            if (args != null && !args.isEmpty()) {
                args.forEach(buildImageCmd::withBuildArg);
            }
            if (labels != null && !labels.isEmpty()) {
                buildImageCmd.withLabels(labels);
            }
            buildImageCmd.withTags(new HashSet<>() {
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
         * Inspect inspect image.
         *
         * @param imageName the image name
         * @return the inspect image response
         */
        public InspectImageResponse inspect(String imageName) {
            InspectImageCmd inspectImageCmd = docker.inspectImageCmd(imageName);
            return inspectImageCmd.exec();
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
     * Docker Registry API.
     */
    public interface Registry {

        /**
         * Exist Image.
         *
         * @param imageName the image name
         * @return <b>true</b> if exist
         */
        boolean existImage(String imageName);

        /**
         * Remove Image.
         *
         * @param imageName the image name
         * @return <b>true</b> if success
         */
        boolean removeImage(String imageName);

        /**
         * Copy Image.
         *
         * @param fromImageName    the from image name
         * @param toProjectName    the to project name
         * @param toRepositoryName the to repository name
         * @return the boolean
         */
        boolean copyImage(String fromImageName, String toProjectName, String toRepositoryName);

        /**
         * Create or update label.
         *
         * @param labelName   the label name
         * @param labelValue  the label value
         * @param projectName the project name
         * @return <b>true</b> if success
         */
        boolean createOrUpdateLabel(String labelName, String labelValue, Optional<String> projectName);

        /**
         * Get label description by name.
         *
         * @param labelName   the label name
         * @param projectName the project name
         * @return label description
         */
        Label getLabel(String labelName, Optional<String> projectName);

        /**
         * Create project.
         *
         * @param projectName the project name
         * @return the boolean
         */
        boolean createProject(String projectName);

        /**
         * Delete project.
         *
         * @param projectName the project name
         * @return the boolean
         */
        boolean deleteProject(String projectName);

        /**
         * Get project id by name.
         *
         * @param projectName the project name
         * @return the project id
         */
        Integer getProjectIdByName(String projectName);

        /**
         * Parse image info string [ ].
         *
         * @param imageName the image name
         * @return the string [ ]
         */
        default String[] parseImageInfo(String imageName) {
            String[] imageFragment = imageName.split(":");
            String tag = imageFragment.length == 2 ? imageFragment[1] : null;
            String image = imageFragment[0];
            if (image.split("/").length == 3) {
                // 带host，先去除
                image = image.substring(image.indexOf("/") + 1);
            }
            return new String[]{
                    image.substring(0, image.indexOf("/")),
                    image.substring(image.indexOf("/") + 1),
                    tag};
        }

    }

    /**
     * Harbor Registry API.
     *
     * @see <a href="https://raw.githubusercontent.com/goharbor/harbor/master/docs/swagger.yaml">Goharbor API</a>
     */
    public class HarborRegistry implements Registry {

        @Override
        public boolean existImage(String imageName) {
            String[] item = parseImageInfo(imageName);
            HttpHelper.ResponseWrap responseWrap;
            if (item[2] == null) {
                responseWrap = $.http.getWrap(
                        registryApiUrl + "/projects/" + item[0] + "/repositories/" + item[1],
                        wrapHeader());
                log.debug("Registry exist image result [" + responseWrap.statusCode + "]" + responseWrap.result);
            } else {
                responseWrap = $.http.getWrap(
                        registryApiUrl + "/projects/" + item[0] + "/repositories/" + item[1] + "/artifacts/" + item[2] + "/tags",
                        wrapHeader());
                log.debug("Registry exist image tag result [" + responseWrap.statusCode + "]" + responseWrap.result);
            }
            return responseWrap.statusCode == 200;
        }

        @Override
        public boolean removeImage(String imageName) {
            String[] item = parseImageInfo(imageName);
            HttpHelper.ResponseWrap responseWrap;
            if (item[2] == null) {
                // remove Image
                responseWrap = $.http.deleteWrap(
                        registryApiUrl + "/projects/" + item[0] + "/repositories/" + item[1],
                        wrapHeader());
                if (responseWrap.statusCode == 200) {
                    log.debug("Registry remove image result [" + responseWrap.statusCode + "]" + responseWrap.result);
                    return true;
                }
                log.error("Registry remove image result [" + responseWrap.statusCode + "]" + responseWrap.result);
            } else {
                // remove Tag
                responseWrap = $.http.deleteWrap(
                        registryApiUrl + "/projects/" + item[0] + "/repositories/" + item[1] + "/artifacts/" + item[2] + "/tags/" + item[2],
                        wrapHeader());
                if (responseWrap.statusCode == 200) {
                    log.debug("Registry remove image tag result [" + responseWrap.statusCode + "]" + responseWrap.result);
                    return true;
                }
                log.error("Registry remove image tag result [" + responseWrap.statusCode + "]" + responseWrap.result);
            }
            return false;
        }

        @Override
        public boolean copyImage(String fromImageName, String toProjectName, String toRepositoryName) {
            HttpHelper.ResponseWrap responseWrap = $.http.postWrap(
                    registryApiUrl + "/projects/" + toProjectName + "/repositories/" + toRepositoryName + "/artifacts?from=" + fromImageName,
                    "",
                    wrapHeader());
            if (responseWrap.statusCode == 201) {
                log.debug("Registry copy image result [" + responseWrap.statusCode + "]" + responseWrap.result);
                return true;
            }
            log.error("Registry copy image result [" + responseWrap.statusCode + "]" + responseWrap.result);
            return false;
        }

        @Override
        public boolean createOrUpdateLabel(String labelName, String labelValue, Optional<String> projectName) {
            Integer projectId = 0;
            if (projectName.isPresent()) {
                projectId = getProjectIdByName(projectName.get());
                if (projectId == null) {
                    log.error("Registry create or update label by project [" + projectName + "] not exist");
                    return false;
                }
            }
            Label label = getLabel(labelName, projectName);
            if (label == null) {
                label = new Label();
                label.name = labelName;
                label.description = labelValue;
                label.deleted = false;
                label.scope = projectName.isPresent() ? "p" : "g";
                label.projectId = projectId;
                HttpHelper.ResponseWrap responseWrap = $.http.postWrap(registryApiUrl + "/labels",
                        $.json.toJsonString(label), wrapHeader());
                if (responseWrap.statusCode == 201) {
                    log.debug("Registry add label result [" + responseWrap.statusCode + "]" + responseWrap.result);
                    return true;
                }
                log.error("Registry add label result [" + responseWrap.statusCode + "]" + responseWrap.result);
            } else {
                label.description = labelValue;
                HttpHelper.ResponseWrap responseWrap = $.http.putWrap(registryApiUrl + "/labels/" + label.getId(),
                        $.json.toJsonString(label), wrapHeader());
                if (responseWrap.statusCode == 200) {
                    log.debug("Registry update label result [" + responseWrap.statusCode + "]" + responseWrap.result);
                    return true;
                }
                log.error("Registry update label result [" + responseWrap.statusCode + "]" + responseWrap.result);
            }
            return false;
        }

        @Override
        public Label getLabel(String labelName, Optional<String> projectName) {
            String url = registryApiUrl + "/labels?name=" + labelName;
            if (projectName.isEmpty()) {
                url = url + "&scope=g";
            } else {
                Integer projectId = getProjectIdByName(projectName.get());
                if (projectId == null) {
                    log.error("Registry get labels by project [" + projectName + "] not exist");
                    return null;
                }
                url = url + "&scope=p&project_id=" + projectId;
            }
            HttpHelper.ResponseWrap responseWrap = $.http.getWrap(url, wrapHeader());
            if (responseWrap.statusCode == 200) {
                log.debug("Registry get labels result [" + responseWrap.statusCode + "]" + $.json.toJson(responseWrap.result));
                List<Label> data = $.json.toList(responseWrap.result, Label.class);
                if (data.isEmpty()) {
                    return null;
                }
                return data.get(0);
            }
            log.error("Registry get labels result [" + responseWrap.statusCode + "]" + $.json.toJson(responseWrap.result));
            return null;
        }

        @Override
        public boolean createProject(String projectName) {
            HttpHelper.ResponseWrap responseWrap = $.http.postWrap(registryApiUrl + "/projects", new HashMap<>() {
                {
                    put("project_name", projectName);
                }
            }, wrapHeader());
            if (responseWrap.statusCode == 201) {
                log.debug("Registry create project result [" + responseWrap.statusCode + "]");
                return true;
            }
            log.error("Registry create project result [" + responseWrap.statusCode + "]");
            return false;
        }

        @Override
        public boolean deleteProject(String projectName) {
            Integer projectId = getProjectIdByName(projectName);
            if (projectId == null) {
                log.error("Registry delete project [" + projectName + "] not exist");
                return false;
            }
            HttpHelper.ResponseWrap responseWrap = $.http.deleteWrap(registryApiUrl + "/projects/" + projectId, wrapHeader());
            if (responseWrap.statusCode == 200) {
                log.debug("Registry delete project result [" + responseWrap.statusCode + "]");
                return true;
            }
            log.error("Registry delete project result [" + responseWrap.statusCode + "]");
            return false;
        }

        @Override
        public Integer getProjectIdByName(String projectName) {
            HttpHelper.ResponseWrap responseWrap = $.http.getWrap(registryApiUrl + "/projects?name=" + projectName, wrapHeader());
            if (responseWrap.statusCode == 200
                    && responseWrap.result != null
                    && !responseWrap.result.isBlank()
                    && !responseWrap.result.equals("null")) {
                return (Integer) $.json.toList(responseWrap.result, Map.class).get(0).get("project_id");
            }
            return null;
        }

        private Map<String, String> wrapHeader() {
            Map<String, String> header = new HashMap<>();
            header.put("Content-Type", "application/json");
            header.put("Accept", "application/json");
            header.put("Authorization", "Basic "
                    + $.security.encodeStringToBase64(registryUsername + ":" + registryPassword, StandardCharsets.UTF_8));
            return header;
        }

    }

    /**
     * Github Packages Registry API.
     *
     * @see <a href="https://docs.github.com/en/rest/reference/repos">Github Packages API</a>
     */
    public class GithubPackagesRegistry implements Registry {


        private Map<String, String> wrapHeader() {
            Map<String, String> header = new HashMap<>();
            header.put("Content-Type", "application/json");
            header.put("Accept", "application/vnd.github.v3+json");
            // Github API 中的 Password 对应的就是 Token
            header.put("Authorization", "token " + registryPassword);
            return header;
        }

        @Override
        public boolean existImage(String imageName) {
            return false;
        }

        @Override
        public boolean removeImage(String imageName) {
            return false;
        }

        @Override
        public boolean copyImage(String fromImageName, String toProjectName, String toRepositoryName) {
            return false;
        }

        @Override
        public boolean createOrUpdateLabel(String labelName, String labelValue, Optional<String> projectName) {
            return false;
        }

        @Override
        public Label getLabel(String labelName, Optional<String> projectName) {
            return null;
        }

        @Override
        public boolean createProject(String projectName) {
            return false;
        }

        @Override
        public boolean deleteProject(String projectName) {
            return false;
        }

        @Override
        public Integer getProjectIdByName(String projectName) {
            return null;
        }
    }

    /**
     * Docker label.
     *
     * @author Liuhongcheng
     */
    public static class Label {
        private Integer id;
        private String name;
        private String description;
        @JsonProperty(value = "project_id")
        private Integer projectId;
        private String scope;
        private Boolean deleted;

        /**
         * Gets id.
         *
         * @return the id
         */
        public Integer getId() {
            return id;
        }

        /**
         * Sets id.
         *
         * @param id the id
         */
        public void setId(Integer id) {
            this.id = id;
        }

        /**
         * Gets name.
         *
         * @return the name
         */
        public String getName() {
            return name;
        }

        /**
         * Sets name.
         *
         * @param name the name
         */
        public void setName(String name) {
            this.name = name;
        }

        /**
         * Gets description.
         *
         * @return the description
         */
        public String getDescription() {
            return description;
        }

        /**
         * Sets description.
         *
         * @param description the description
         */
        public void setDescription(String description) {
            this.description = description;
        }

        /**
         * Gets project id.
         *
         * @return the project id
         */
        public Integer getProjectId() {
            return projectId;
        }

        /**
         * Sets project id.
         *
         * @param projectId the project id
         */
        public void setProjectId(Integer projectId) {
            this.projectId = projectId;
        }

        /**
         * Gets scope.
         *
         * @return the scope
         */
        public String getScope() {
            return scope;
        }

        /**
         * Sets scope.
         *
         * @param scope the scope
         */
        public void setScope(String scope) {
            this.scope = scope;
        }

        /**
         * Gets deleted.
         *
         * @return the deleted
         */
        public Boolean getDeleted() {
            return deleted;
        }

        /**
         * Sets deleted.
         *
         * @param deleted the deleted
         */
        public void setDeleted(Boolean deleted) {
            this.deleted = deleted;
        }
    }


    /**
     * The enum Registry kind.
     */
    public enum RegistryKind {
        /**
         * Harbor registry kind.
         */
        HARBOR,
        /**
         * Github packages registry kind.
         */
        GITHUB_PACKAGES
    }
}
