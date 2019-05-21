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

package ms.dew.devops.maven.mojo;

import ms.dew.devops.kernel.function.StatusReporter;
import ms.dew.devops.maven.MavenDevOps;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.ResolutionScope;

/**
 * Init mojo.
 * <p>
 * NOTE: 此mojo不能单独调用，仅用于 build 或 release 内部调用
 *
 * @author gudaoxuri
 */
@Mojo(name = "init", requiresDependencyResolution = ResolutionScope.COMPILE)
public class InitMojo extends BasicMojo {

    @Override
    protected boolean executeInternal() {
        if (mavenSession.isParallel()) {
            StatusReporter.report();
        }
        if (mavenSession.getGoals().stream().map(String::toLowerCase)
                .anyMatch(s ->
                        s.contains("ms.dew:dew-maven-plugin:release")
                                || s.contains("dew:release"))) {
            MavenDevOps.Process.needProcessCheck(quiet);
        }
        if (mavenSession.getGoals().stream().map(String::toLowerCase)
                .anyMatch(s -> s.contains("ms.dew:dew-maven-plugin:scale")
                        || s.contains("dew:scale"))) {
            if (!autoScale && replicas == 0) {
                logger.error("Parameter error, When autoScale disabled, dew_devops_scale_replicas can't be 0");
                return false;
            }
            if (autoScale && (minReplicas == 0 || maxReplicas == 0 || minReplicas >= maxReplicas || cpuAvg == 0)) {
                logger.error("Parameter error, Current mode is autoScale model");
                return false;
            }
        }
        return true;
    }

}
