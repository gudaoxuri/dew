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

package ms.dew.devops.kernel.plugin.appkind.jvmlib;

import ms.dew.devops.kernel.config.FinalProjectConfig;
import ms.dew.devops.kernel.flow.BasicFlow;
import ms.dew.devops.kernel.flow.NoNeedProcessFLow;
import ms.dew.devops.kernel.flow.release.MavenReleaseFlow;
import ms.dew.devops.kernel.plugin.appkind.AppKindPlugin;

import java.util.HashMap;
import java.util.Map;

/**
 * JVM lib app kind plugin.
 *
 * @author gudaoxuri
 */
public class JvmLibAppKindPlugin implements AppKindPlugin {

    @Override
    public String getName() {
        return "JVM Lib";
    }

    @Override
    public void customConfig(FinalProjectConfig projectConfig) {
        projectConfig.getApp().setHealthCheckEnabled(false);
        projectConfig.getApp().setTraceLogEnabled(false);
        projectConfig.getApp().setMetricsEnabled(false);
        projectConfig.getApp().setTraceLogSpans(false);
        // 类库工程不需要重用
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
    public BasicFlow rollbackFlow() {
        return new NoNeedProcessFLow();
    }

    @Override
    public BasicFlow scaleFlow(int replicas, boolean autoScale, int minReplicas, int maxReplicas, int cpuAvg) {
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
