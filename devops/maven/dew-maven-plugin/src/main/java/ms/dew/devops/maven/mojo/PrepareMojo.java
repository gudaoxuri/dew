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

import io.kubernetes.client.ApiException;
import ms.dew.devops.kernel.flow.prepare.PrepareFlowFactory;
import ms.dew.devops.kernel.function.NeedProcessChecker;
import org.apache.maven.plugins.annotations.Execute;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.ResolutionScope;

import java.io.IOException;

/**
 * Prepare mojo.
 * <p>
 * NOTE: 此mojo不能单独调用，仅用于 build 或 release 内部调用
 *
 * @author gudaoxuri
 */
@Mojo(name = "prepare", defaultPhase = LifecyclePhase.PACKAGE, requiresDependencyResolution = ResolutionScope.COMPILE_PLUS_RUNTIME)
@Execute(phase = LifecyclePhase.VALIDATE, goal = "init")
public class PrepareMojo extends BasicMojo {

    @Override
    protected void preExecute() {
        NeedProcessChecker.checkNeedProcessProjects(quiet, ignoreExistMavenVersion);
    }

    @Override
    protected boolean executeInternal() throws IOException, ApiException {
        return PrepareFlowFactory.choose().exec(getMojoName());
    }
}
