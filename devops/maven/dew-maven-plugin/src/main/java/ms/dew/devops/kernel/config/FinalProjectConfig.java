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

import java.util.HashSet;
import java.util.Set;

/**
 * Final project config.
 * <p>
 * 最终生成的项目配置
 *
 * @author gudaoxuri
 */
public class FinalProjectConfig extends DewProfile {

    // 项目Id,对应于maven project id
    private String id;
    // 用于指定 docker的版本
    // 当前的git commit 或是 重用版本下被重用的git commit
    private String gitCommit = "";
    private String scmUrl = "";
    private String appName = "";
    private String appShowName = "";
    private String appGroup = "";
    // 用于指定应用的版本
    // app version 对应于当前的 git commit
    // 如果在非重用版本模式下等同于 git commit
    // 如果在重用版本模式下git commit 指向被重用的 git commit
    private String appVersion = "";
    private String mvnDirectory;
    private String mvnTargetDirectory;
    private String skipReason = "";
    private boolean hasError = false;
    private Set<String> executeSuccessfulMojos = new HashSet<>();

    private DewProfile appendProfile;

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
     * Gets app show name.
     *
     * @return the app show name
     */
    public String getAppShowName() {
        return appShowName;
    }

    /**
     * Sets app show name.
     *
     * @param appShowName the app show name
     */
    public void setAppShowName(String appShowName) {
        this.appShowName = appShowName;
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
     * Gets app version.
     *
     * @return the app version
     */
    public String getAppVersion() {
        return appVersion;
    }

    /**
     * Sets app version.
     *
     * @param appVersion the app version
     */
    public void setAppVersion(String appVersion) {
        this.appVersion = appVersion;
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
     * Gets skip reason.
     *
     * @return the skip reason
     */
    public String getSkipReason() {
        return skipReason;
    }

    /**
     * Sets skip reason.
     *
     * @param skipReason the skip reason
     */
    public void setSkipReason(String skipReason) {
        this.skipReason = skipReason;
    }

    /**
     * Is has error.
     *
     * @return result boolean
     */
    public boolean isHasError() {
        return hasError;
    }

    /**
     * Gets execute successful mojos.
     *
     * @return the execute successful mojos
     */
    public Set<String> getExecuteSuccessfulMojos() {
        return executeSuccessfulMojos;
    }

    /**
     * Sets execute successful mojos.
     *
     * @param executeSuccessfulMojos the execute successful mojos
     */
    public void setExecuteSuccessfulMojos(Set<String> executeSuccessfulMojos) {
        this.executeSuccessfulMojos = executeSuccessfulMojos;
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


    /**
     * Skip this project.
     *
     * @param reason  the reason
     * @param isError the is error
     */
    public void skip(String reason, boolean isError) {
        super.setSkip(true);
        hasError = isError;
        this.setSkipReason(reason);
    }

}
