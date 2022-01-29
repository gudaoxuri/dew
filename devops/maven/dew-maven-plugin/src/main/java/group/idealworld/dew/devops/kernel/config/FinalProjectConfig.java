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

package group.idealworld.dew.devops.kernel.config;

import group.idealworld.dew.devops.kernel.plugin.appkind.AppKindPlugin;
import group.idealworld.dew.devops.kernel.plugin.deploy.DeployPlugin;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.BuildPluginManager;
import org.apache.maven.project.MavenProject;

import java.util.LinkedHashSet;
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
    // 当前的git commit 或是 重用版本下被重用的git commit
    private String gitCommit;
    private String scmUrl;
    private String appName;
    private String appShowName;
    private String appGroup;
    // 用于指定应用的版本
    // app version 对应于当前的 git commit
    private String appVersion;
    // 用于指定 docker 的版本
    // 如果在重用版本模式下指向被重用的 git commit
    private String imageVersion;
    private String skipReason = "";
    private SkipCodeEnum skipCode;
    private boolean hasError = false;
    private Set<String> executeSuccessfulMojos = new LinkedHashSet<>();

    private String directory;
    private String targetDirectory;

    private DewProfile appendProfile;

    private AppKindPlugin appKindPlugin;
    private DeployPlugin deployPlugin;

    private MavenProject mavenProject;
    private MavenSession mavenSession;
    private BuildPluginManager pluginManager;

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
     * Gets directory.
     *
     * @return the directory
     */
    public String getDirectory() {
        return directory;
    }

    /**
     * Sets directory.
     *
     * @param directory the directory
     */
    public void setDirectory(String directory) {
        this.directory = directory;
    }

    /**
     * Gets target directory.
     *
     * @return the target directory
     */
    public String getTargetDirectory() {
        return targetDirectory;
    }

    /**
     * Sets target directory.
     *
     * @param targetDirectory the target directory
     */
    public void setTargetDirectory(String targetDirectory) {
        this.targetDirectory = targetDirectory;
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
     * Gets skip code.
     *
     * @return the skip code
     */
    public SkipCodeEnum getSkipCode() {
        return skipCode;
    }

    /**
     * Sets skip code.
     *
     * @param skipCode the skip code
     */
    public void setSkipCode(SkipCodeEnum skipCode) {
        this.skipCode = skipCode;
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
     * Sets has error.
     *
     * @param hasError the has error
     */
    public void setHasError(boolean hasError) {
        this.hasError = hasError;
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
     * Gets maven project.
     *
     * @return the maven project
     */
    public MavenProject getMavenProject() {
        return mavenProject;
    }

    /**
     * Sets maven project.
     *
     * @param mavenProject the maven project
     */
    public void setMavenProject(MavenProject mavenProject) {
        this.mavenProject = mavenProject;
    }

    /**
     * Gets maven session.
     *
     * @return the maven session
     */
    public MavenSession getMavenSession() {
        return mavenSession;
    }

    /**
     * Sets maven session.
     *
     * @param mavenSession the maven session
     */
    public void setMavenSession(MavenSession mavenSession) {
        this.mavenSession = mavenSession;
    }

    /**
     * Gets plugin manager.
     *
     * @return the plugin manager
     */
    public BuildPluginManager getPluginManager() {
        return pluginManager;
    }

    /**
     * Sets plugin manager.
     *
     * @param pluginManager the plugin manager
     */
    public void setPluginManager(BuildPluginManager pluginManager) {
        this.pluginManager = pluginManager;
    }

    /**
     * Gets image version.
     *
     * @return the image version
     */
    public String getImageVersion() {
        return imageVersion;
    }

    /**
     * Sets image version.
     *
     * @param imageVersion the image version
     */
    public void setImageVersion(String imageVersion) {
        this.imageVersion = imageVersion;
    }

    /**
     * Gets app kind plugin.
     *
     * @return the app kind plugin
     */
    public AppKindPlugin getAppKindPlugin() {
        return appKindPlugin;
    }

    /**
     * Sets app kind plugin.
     *
     * @param appKindPlugin the app kind plugin
     */
    public void setAppKindPlugin(AppKindPlugin appKindPlugin) {
        this.appKindPlugin = appKindPlugin;
    }

    /**
     * Gets deploy plugin.
     *
     * @return the deploy plugin
     */
    public DeployPlugin getDeployPlugin() {
        return deployPlugin;
    }

    /**
     * Sets deploy plugin.
     *
     * @param deployPlugin the deploy plugin
     */
    public void setDeployPlugin(DeployPlugin deployPlugin) {
        this.deployPlugin = deployPlugin;
    }

    /**
     * Get current image name.
     *
     * @return the current image name
     */
    public String getCurrImageName() {
        return getImageName(getImageVersion());
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

    public enum SkipCodeEnum {

        // dew文件中配置
        SELF_CONFIG,

        // 非dew文件中配置
        NON_SELF_CONFIG
    }

}
