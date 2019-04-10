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

package ms.dew.devops.kernel.flow.prepare;

import ms.dew.devops.kernel.Dew;
import ms.dew.devops.kernel.config.FinalProjectConfig;

import java.io.IOException;
import java.util.HashMap;

/**
 * Jvm service prepare flow.
 *
 * @author gudaoxuri
 */
public class JvmServicePrepareFlow extends BasicPrepareFlow {

    protected boolean prePrepareBuild(FinalProjectConfig config, String flowBasePath) throws IOException {
        Dew.Invoke.invoke("org.springframework.boot",
                "spring-boot-maven-plugin",
                null,
                "repackage",
                new HashMap<String, String>() {
                    {
                        put("outputDirectory", flowBasePath);
                        put("finalName", "serv");
                    }
                });
        return true;
    }

}
