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

package ms.dew.devops.kernel.config;

import java.util.regex.Pattern;

/**
 * Final project config.
 * <p>
 * 最终生成的项目配置
 *
 * @author gudaoxuri
 */
public class FinalProjectConfig extends DewProfile {

    private static final Pattern GIT_HASH = Pattern.compile("[0-9a-f]{40}");

    // 项目Id,对应于maven project id
    private String id;
    private String gitCommit = "";
    private String scmUrl = "";
    private String appName = "";
    private String appGroup = "";
    private String mvnGroupId;
    private String mvnArtifactId;
    private String mvnDirectory;
    private String mvnTargetDirectory;

    private DewProfile appendProfile;

    /**
     * 是否是自定义版本.
     *
     * @return 是否是自定义版本
     */
    public boolean isCustomVersion() {
        return !GIT_HASH.matcher(this.getGitCommit()).matches();
    }

    /**
     * Gets id.
     *
     * @return the id
     */
    public String getId() {
        return id;
    }

    /**
     * Sets id.
     *
     * @param id the id
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Gets git commit.
     *
     * @return the git commit
     */
    public String getGitCommit() {
        return gitCommit;
    }

    /**
     * Sets git commit.
     *
     * @param gitCommit the git commit
     */
    public void setGitCommit(String gitCommit) {
        this.gitCommit = gitCommit;
    }

    /**
     * Gets scm url.
     *
     * @return the scm url
     */
    public String getScmUrl() {
        return scmUrl;
    }

    /**
     * Sets scm url.
     *
     * @param scmUrl the scm url
     */
    public void setScmUrl(String scmUrl) {
        this.scmUrl = scmUrl;
    }

    /**
     * Gets app name.
     *
     * @return the app name
     */
    public String getAppName() {
        return appName;
    }

    /**
     * Sets app name.
     *
     * @param appName the app name
     */
    public void setAppName(String appName) {
        this.appName = appName;
    }

    /**
     * Gets app group.
     *
     * @return the app group
     */
    public String getAppGroup() {
        return appGroup;
    }

    /**
     * Sets app group.
     *
     * @param appGroup the app group
     */
    public void setAppGroup(String appGroup) {
        this.appGroup = appGroup;
    }

    /**
     * Gets mvn group id.
     *
     * @return the mvn group id
     */
    public String getMvnGroupId() {
        return mvnGroupId;
    }

    /**
     * Sets mvn group id.
     *
     * @param mvnGroupId the mvn group id
     */
    public void setMvnGroupId(String mvnGroupId) {
        this.mvnGroupId = mvnGroupId;
    }

    /**
     * Gets mvn artifact id.
     *
     * @return the mvn artifact id
     */
    public String getMvnArtifactId() {
        return mvnArtifactId;
    }

    /**
     * Sets mvn artifact id.
     *
     * @param mvnArtifactId the mvn artifact id
     */
    public void setMvnArtifactId(String mvnArtifactId) {
        this.mvnArtifactId = mvnArtifactId;
    }

    /**
     * Gets mvn directory.
     *
     * @return the mvn directory
     */
    public String getMvnDirectory() {
        return mvnDirectory;
    }

    /**
     * Sets mvn directory.
     *
     * @param mvnDirectory the mvn directory
     */
    public void setMvnDirectory(String mvnDirectory) {
        this.mvnDirectory = mvnDirectory;
    }

    /**
     * Gets mvn target directory.
     *
     * @return the mvn target directory
     */
    public String getMvnTargetDirectory() {
        return mvnTargetDirectory;
    }

    /**
     * Sets mvn target directory.
     *
     * @param mvnTargetDirectory the mvn target directory
     */
    public void setMvnTargetDirectory(String mvnTargetDirectory) {
        this.mvnTargetDirectory = mvnTargetDirectory;
    }

    /**
     * Gets append profile.
     *
     * @return the append profile
     */
    public DewProfile getAppendProfile() {
        return appendProfile;
    }

    /**
     * Sets append profile.
     *
     * @param appendProfile the append profile
     */
    public void setAppendProfile(DewProfile appendProfile) {
        this.appendProfile = appendProfile;
    }

    /**
     * Get current image name.
     *
     * @return the current image name
     */
    public String getCurrImageName() {
        return getImageName(getGitCommit());
    }

    /**
     * Get image name.
     *
     * @param specTag the spec tag
     * @return the image name
     */
    public String getImageName(String specTag) {
        return getImageName(getDocker().getRegistryHost(), getNamespace(), getAppName(), specTag);
    }

    /**
     * Get image name.
     *
     * @param registryHost the registry host
     * @param namespace    the namespace
     * @param appName      the app name
     * @param specTag      the spec tag
     * @return the image name
     */
    public String getImageName(String registryHost, String namespace, String appName, String specTag) {
        return registryHost + "/"
                + namespace + "/"
                + appName + ":" + specTag;
    }
}
