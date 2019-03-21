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

package com.tairanchina.csp.dew.kernel.config;

import static com.tairanchina.csp.dew.mojo.BasicMojo.FLAG_DEW_DEVOPS_DEFAULT_PROFILE;

public class FinalConfig extends DewProfile {

    private String profile = FLAG_DEW_DEVOPS_DEFAULT_PROFILE;
    private String gitCommit = "";
    private String gitBranch = "";
    private String scmUrl = "";
    private String appName = "";
    private String appGroup = "";
    private String appVersion = "";
    private AppKind appKind = null;

    public String getProfile() {
        return profile;
    }

    public void setProfile(String profile) {
        this.profile = profile;
    }

    public String getGitCommit() {
        return gitCommit;
    }

    public void setGitCommit(String gitCommit) {
        this.gitCommit = gitCommit;
    }

    public String getGitBranch() {
        return gitBranch;
    }

    public void setGitBranch(String gitBranch) {
        this.gitBranch = gitBranch;
    }

    public String getScmUrl() {
        return scmUrl;
    }

    public void setScmUrl(String scmUrl) {
        this.scmUrl = scmUrl;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getAppGroup() {
        return appGroup;
    }

    public void setAppGroup(String appGroup) {
        this.appGroup = appGroup;
    }

    public String getAppVersion() {
        return appVersion;
    }

    public void setAppVersion(String appVersion) {
        this.appVersion = appVersion;
    }

    public AppKind getAppKind() {
        return appKind;
    }

    public void setAppKind(AppKind appKind) {
        this.appKind = appKind;
    }

    public String getCurrImageName() {
        return getImageName(gitCommit);
    }

    public String getImageName(String specTag) {
        return getDocker().getRegistryHost() + "/"
                + getNamespace() + "/"
                + getAppName() + ":" +
                specTag;
    }
}
