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

import group.idealworld.dew.core.notification.NotifyConfig;
import group.idealworld.dew.devops.kernel.util.DewLog;
import org.slf4j.Logger;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Dew profile.
 *
 * @author gudaoxuri
 */
public class DewProfile {

    protected static final Logger logger = DewLog.build(DewProfile.class);

    // 环境名称，内部使用，不需要显式指定
    private String profile;
    // 命名空间
    private String namespace = "default";
    // 是否跳过
    private Boolean skip = false;
    // 是否禁用重用版本，默认情况前端工程为true（node编译期会混入环境信息导致无法重用），其它工程为false
    private Boolean disableReuseVersion;
    // 重用版本的目标环境名称，默认会尝试使用 pre-prod/pre-production/uat 为名称（找到当前项目第一个存在的环境），都不存在时需要显式指定
    private String reuseLastVersionFromProfile;
    // 忽略变更文件列表，此列表指定的文件不用于是否有变更要部署的判断依据
    // 支持 glob , @see https://en.wikipedia.org/wiki/Glob_(programming)
    private Set<String> ignoreChangeFiles = new HashSet<>();
    // 应用配置
    private DewApp app = new DewApp();
    // Docker配置
    private DewDocker docker = new DewDocker();
    // Kubernetes配置
    private DewKube kube = new DewKube();
    // 通知配置
    private List<NotifyConfig> notifies = null;

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
     * Gets skip.
     *
     * @return the skip
     */
    public Boolean getSkip() {
        return skip;
    }

    /**
     * Sets skip.
     *
     * @param skip the skip
     */
    public void setSkip(Boolean skip) {
        this.skip = skip;
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
     * Gets disable reuse version.
     *
     * @return the disable reuse version
     */
    public Boolean getDisableReuseVersion() {
        return disableReuseVersion;
    }

    /**
     * Sets disable reuse version.
     *
     * @param disableReuseVersion the disable reuse version
     */
    public void setDisableReuseVersion(Boolean disableReuseVersion) {
        this.disableReuseVersion = disableReuseVersion;
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
     * Gets ignore change files.
     *
     * @return the ignore change files
     */
    public Set<String> getIgnoreChangeFiles() {
        return ignoreChangeFiles;
    }

    /**
     * Sets ignore change files.
     *
     * @param ignoreChangeFiles the ignore change files
     */
    public void setIgnoreChangeFiles(Set<String> ignoreChangeFiles) {
        this.ignoreChangeFiles = ignoreChangeFiles;
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
     * Gets notifies.
     *
     * @return the notifies
     */
    public List<NotifyConfig> getNotifies() {
        return notifies;
    }

    /**
     * Sets notifies.
     *
     * @param notifies the notifies
     */
    public void setNotifies(List<NotifyConfig> notifies) {
        this.notifies = notifies;
    }
}
