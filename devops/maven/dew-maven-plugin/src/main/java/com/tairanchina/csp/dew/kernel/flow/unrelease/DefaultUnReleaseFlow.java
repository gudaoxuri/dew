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

package com.tairanchina.csp.dew.kernel.flow.unrelease;

import com.tairanchina.csp.dew.helper.KubeHelper;
import com.tairanchina.csp.dew.kernel.Dew;
import com.tairanchina.csp.dew.kernel.flow.BasicFlow;
import io.kubernetes.client.ApiException;
import org.apache.maven.plugin.MojoExecutionException;

import java.io.IOException;

public class DefaultUnReleaseFlow extends BasicFlow {

    public boolean process() throws ApiException, IOException, MojoExecutionException {
        KubeHelper.delete(Dew.Config.getCurrentProject().getAppName(), Dew.Config.getCurrentProject().getNamespace(), KubeHelper.RES.SERVICE, Dew.Config.getCurrentProject().getId());
        return true;
    }

}
