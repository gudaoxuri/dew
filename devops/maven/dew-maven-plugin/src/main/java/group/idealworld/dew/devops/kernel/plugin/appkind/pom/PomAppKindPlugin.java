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

package group.idealworld.dew.devops.kernel.plugin.appkind.pom;

import group.idealworld.dew.devops.kernel.flow.BasicFlow;
import group.idealworld.dew.devops.kernel.flow.NoNeedProcessFLow;
import group.idealworld.dew.devops.kernel.plugin.appkind.AppKindPlugin;
import group.idealworld.dew.devops.kernel.config.FinalProjectConfig;
import group.idealworld.dew.devops.kernel.flow.release.MavenReleaseFlow;

import java.util.HashMap;
import java.util.Map;

/**
 * POM app kind plugin.
 *
 * @author gudaoxuri
 */
public class PomAppKindPlugin implements AppKindPlugin {

    @Override
    public String getName() {
        return "POM";
    }

    @Override
    public void customConfig(FinalProjectConfig projectConfig) {
        projectConfig.getApp().setHealthCheckEnabled(false);
        projectConfig.getApp().setTraceLogEnabled(false);
        projectConfig.getApp().setMetricsEnabled(false);
        projectConfig.getApp().setTraceLogSpans(false);
        // POM工程不需要重用
        projectConfig.setDisableReuseVersion(true);
    }

    @Override
    public BasicFlow prepareFlow() {
        return new NoNeedProcessFLow();
    }

    @Override
    public BasicFlow buildFlow() {
        return new NoNeedProcessFLow();
    }

    @Override
    public BasicFlow releaseFlow() {
        return new MavenReleaseFlow();
    }

    @Override
    public BasicFlow unReleaseFlow() {
        return new NoNeedProcessFLow();
    }

    @Override
    public BasicFlow rollbackFlow(boolean history, String version) {
        return new NoNeedProcessFLow();
    }

    @Override
    public BasicFlow scaleFlow(int replicas, boolean autoScale, int minReplicas, int maxReplicas, int cpuAvg) {
        return new NoNeedProcessFLow();
    }

    @Override
    public BasicFlow refreshFlow() {
        return new NoNeedProcessFLow();
    }

    @Override
    public BasicFlow logFlow(String podName, boolean follow) {
        return new NoNeedProcessFLow();
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
