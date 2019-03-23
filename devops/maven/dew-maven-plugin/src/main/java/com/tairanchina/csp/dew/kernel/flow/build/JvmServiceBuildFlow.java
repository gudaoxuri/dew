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

package com.tairanchina.csp.dew.kernel.flow.build;

import com.ecfront.dew.common.$;
import com.tairanchina.csp.dew.kernel.Dew;
import org.apache.maven.plugin.MojoExecutionException;

import java.io.IOException;
import java.util.HashMap;

public class JvmServiceBuildFlow extends BasicBuildFlow {

    protected boolean preDockerBuild(String buildBasePath) throws IOException, MojoExecutionException {
        // FIXME 构建未成功
        Dew.Invoke.invoke("org.springframework.boot",
                "spring-boot-maven-plugin",
                "2.1.3.RELEASE",
                "repackage",
                new HashMap<String, String>() {{
                    put("outputDirectory", buildBasePath);
                    put("finalName", "serv");
                }});
        $.file.copyStreamToPath(Dew.class.getResourceAsStream("/dockerfile/java/Dockerfile"), buildBasePath + "Dockerfile");
        $.file.copyStreamToPath(Dew.class.getResourceAsStream("/dockerfile/java/run-java.sh"), buildBasePath + "run-java.sh");
        return true;
    }

}
