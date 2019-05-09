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

package ms.dew.devops.kernel.plugin.appkind.jvmservice_springboot;

import ms.dew.devops.kernel.DevOps;
import ms.dew.devops.kernel.config.FinalProjectConfig;
import ms.dew.devops.kernel.flow.prepare.BasicPrepareFlow;

import java.util.HashMap;
import java.util.Optional;

/**
 * Spring boot prepare flow.
 *
 * @author gudaoxuri
 */
public class JvmServiceSpringBootPrepareFlow extends BasicPrepareFlow {
    @Override
    protected boolean needExecutePreparePackageCmd(FinalProjectConfig config, String currentPath) {
        return false;
    }

    @Override
    protected Optional<String> getPreparePackageCmd(FinalProjectConfig config, String currentPath) {
        return Optional.empty();
    }

    @Override
    protected Optional<String> getPackageCmd(FinalProjectConfig config, String currentPath) {
        return Optional.empty();
    }

    protected void postPrepareBuild(FinalProjectConfig config, String flowBasePath) {
        DevOps.Invoke.invoke("org.springframework.boot",
                "spring-boot-maven-plugin",
                null,
                "repackage",
                new HashMap<String, String>() {
                    {
                        put("outputDirectory", flowBasePath);
                        put("finalName", "serv");
                    }
                }, DevOps.Config.getFinalConfig(), config);
    }
}
