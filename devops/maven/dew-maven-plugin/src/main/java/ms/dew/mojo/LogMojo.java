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

package ms.dew.mojo;

import ms.dew.kernel.flow.log.DefaultLogFlow;
import io.kubernetes.client.ApiException;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import java.io.IOException;

@Mojo(name = "log")
public class LogMojo extends BasicMojo {

    private static final String FLAG_DEW_DEVOPS_POD_NAME = "dew.devops.log.podName";
    private static final String FLAG_DEW_DEVOPS_LOG_FOLLOW = "dew.devops.log.follow";


    @Parameter(property = FLAG_DEW_DEVOPS_POD_NAME)
    private String podName;
    @Parameter(property = FLAG_DEW_DEVOPS_LOG_FOLLOW)
    private boolean follow;

    @Override
    protected boolean executeInternal() throws MojoExecutionException, MojoFailureException, IOException, ApiException {
        return new DefaultLogFlow(podName, follow).exec();
    }

}
