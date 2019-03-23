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

public class DewProfile {

    private String profile;
    private boolean skip = false;
    private String namespace = "dew-default-ns";

    private DewApp app = new DewApp();
    private DewDocker docker = new DewDocker();
    private DewKube kube = new DewKube();

    public String getProfile() {
        return profile;
    }

    public void setProfile(String profile) {
        this.profile = profile;
    }

    public boolean isSkip() {
        return skip;
    }

    public void setSkip(boolean skip) {
        this.skip = skip;
    }

    public String getNamespace() {
        return namespace;
    }

    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    public DewApp getApp() {
        return app;
    }

    public void setApp(DewApp app) {
        this.app = app;
    }

    public DewDocker getDocker() {
        return docker;
    }

    public void setDocker(DewDocker docker) {
        this.docker = docker;
    }

    public DewKube getKube() {
        return kube;
    }

    public void setKube(DewKube kube) {
        this.kube = kube;
    }
}
