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

import ms.dew.notification.NotifyConfig;

/**
 * Dew profile.
 *
 * @author gudaoxuri
 */
public class DewProfile {

    // 环境名称
    private String profile;
    // 是否跳过
    private boolean skip = false;
    // 项目类型
    private AppKind kind;
    // 命名空间
    private String namespace = "dew-default-ns";
    // 是否重用最后一个版本，值为重用的目标环境名称
    private String reuseLastVersionFromProfile = "";

    private DewApp app = new DewApp();
    private DewDocker docker = new DewDocker();
    private DewKube kube = new DewKube();
    private NotifyConfig notify = null;

    /**
     * Gets profile.
     *
     * @return the profile
     */
    public String getProfile() {
        return profile;
    }

    /**
     * Sets profile.
     *
     * @param profile the profile
     */
    public void setProfile(String profile) {
        this.profile = profile;
    }

    /**
     * Is skip boolean.
     *
     * @return the boolean
     */
    public boolean isSkip() {
        return skip;
    }

    /**
     * Sets skip.
     *
     * @param skip the skip
     */
    public void setSkip(boolean skip) {
        this.skip = skip;
    }

    /**
     * Gets kind.
     *
     * @return the kind
     */
    public AppKind getKind() {
        return kind;
    }

    /**
     * Sets kind.
     *
     * @param kind the kind
     */
    public void setKind(AppKind kind) {
        this.kind = kind;
    }

    /**
     * Gets namespace.
     *
     * @return the namespace
     */
    public String getNamespace() {
        return namespace;
    }

    /**
     * Sets namespace.
     *
     * @param namespace the namespace
     */
    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    /**
     * Gets reuse last version from profile.
     *
     * @return the reuse last version from profile
     */
    public String getReuseLastVersionFromProfile() {
        return reuseLastVersionFromProfile;
    }

    /**
     * Sets reuse last version from profile.
     *
     * @param reuseLastVersionFromProfile the reuse last version from profile
     */
    public void setReuseLastVersionFromProfile(String reuseLastVersionFromProfile) {
        this.reuseLastVersionFromProfile = reuseLastVersionFromProfile;
    }

    /**
     * Gets app.
     *
     * @return the app
     */
    public DewApp getApp() {
        return app;
    }

    /**
     * Sets app.
     *
     * @param app the app
     */
    public void setApp(DewApp app) {
        this.app = app;
    }

    /**
     * Gets docker.
     *
     * @return the docker
     */
    public DewDocker getDocker() {
        return docker;
    }

    /**
     * Sets docker.
     *
     * @param docker the docker
     */
    public void setDocker(DewDocker docker) {
        this.docker = docker;
    }

    /**
     * Gets kube.
     *
     * @return the kube
     */
    public DewKube getKube() {
        return kube;
    }

    /**
     * Sets kube.
     *
     * @param kube the kube
     */
    public void setKube(DewKube kube) {
        this.kube = kube;
    }

    /**
     * Gets notify.
     *
     * @return the notify
     */
    public NotifyConfig getNotify() {
        return notify;
    }

    /**
     * Sets notify.
     *
     * @param notify the notify
     */
    public void setNotify(NotifyConfig notify) {
        this.notify = notify;
    }
}
