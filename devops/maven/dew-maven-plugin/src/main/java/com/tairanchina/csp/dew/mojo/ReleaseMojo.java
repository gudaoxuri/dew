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

package com.tairanchina.csp.dew.mojo;

import com.tairanchina.csp.dew.kernel.flow.release.ReleaseFlowFactory;
import io.kubernetes.client.ApiException;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;

import java.io.IOException;

@Mojo(name = "release", requiresDependencyResolution = ResolutionScope.COMPILE)
public class ReleaseMojo extends BasicMojo {

    public static final String FLAG_DEW_DEVOPS_RELEASE_ALL = "dew.devops.release.all";

    @Parameter(property = FLAG_DEW_DEVOPS_RELEASE_ALL)
    private boolean releaseAll;

    @Override
    public void executeInternal() throws ApiException, IOException, MojoExecutionException {
        ReleaseFlowFactory.choose().process(releaseAll);
    }

}
