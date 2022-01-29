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

package group.idealworld.dew.devops.kernel.plugin.appkind.frontend.node_non_natvie;

import group.idealworld.dew.devops.kernel.flow.BasicFlow;
import group.idealworld.dew.devops.kernel.flow.NoNeedProcessFLow;
import group.idealworld.dew.devops.kernel.flow.log.DefaultLogFlow;
import group.idealworld.dew.devops.kernel.flow.scale.DefaultScaleFlow;
import group.idealworld.dew.devops.kernel.config.FinalProjectConfig;
import group.idealworld.dew.devops.kernel.flow.refresh.DefaultRefreshFlow;
import group.idealworld.dew.devops.kernel.flow.release.KubeReleaseFlow;
import group.idealworld.dew.devops.kernel.flow.rollback.DefaultRollbackFlow;
import group.idealworld.dew.devops.kernel.flow.unrelease.DefaultUnReleaseFlow;
import group.idealworld.dew.devops.kernel.plugin.appkind.AppKindPlugin;

import java.util.HashMap;
import java.util.Map;


/**
 * Node frontend app kind plugin.
 *
 * @author gudaoxuri
 */
public class FrontendNonNativeNodeAppKindPlugin implements AppKindPlugin {

    @Override
    public String getName() {
        return "Non-Native Node Frontend";
    }

    @Override
    public void customConfig(FinalProjectConfig projectConfig) {
        if (projectConfig.getApp().getPort() == null) {
            projectConfig.getApp().setPort(80);
        }
        projectConfig.getApp().setHealthCheckEnabled(false);
        projectConfig.getApp().setTraceLogEnabled(false);
        projectConfig.getApp().setMetricsEnabled(false);
        projectConfig.getApp().setTraceLogSpans(false);
        // 前端工程由于在编译时混入了环境信息，所以不允许重用版本，每次部署都要重新编译
        projectConfig.setDisableReuseVersion(true);
    }

    @Override
    public BasicFlow prepareFlow() {
        return new FrontendNonNativeNodePrepareFlow();
    }

    @Override
    public BasicFlow buildFlow() {
        return new FrontendNonNativeNodeBuildFlow();
    }

    @Override
    public BasicFlow releaseFlow() {
        return new KubeReleaseFlow();
    }

    @Override
    public BasicFlow unReleaseFlow() {
        return new DefaultUnReleaseFlow();
    }

    @Override
    public BasicFlow rollbackFlow(boolean history, String version) {
        return new DefaultRollbackFlow(history, version);
    }

    @Override
    public BasicFlow scaleFlow(int replicas, boolean autoScale, int minReplicas, int maxReplicas, int cpuAvg) {
        return new DefaultScaleFlow(replicas, autoScale, minReplicas, maxReplicas, cpuAvg);
    }

    @Override
    public BasicFlow refreshFlow() {
        return new DefaultRefreshFlow();
    }

    @Override
    public BasicFlow logFlow(String podName, boolean follow) {
        return new DefaultLogFlow(podName, follow);
    }

    @Override
    public BasicFlow debugFlow(String podName, int forwardPort) {
        return new NoNeedProcessFLow();
    }

    @Override
    public Map<String, String> getEnv(FinalProjectConfig projectConfig) {
        return new HashMap<>();
    }

}
