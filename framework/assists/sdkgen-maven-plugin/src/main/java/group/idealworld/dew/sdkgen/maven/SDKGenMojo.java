/*
 * Copyright 2020. the original author or authors.
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

package group.idealworld.dew.sdkgen.maven;

import group.idealworld.dew.sdkgen.helper.MavenHelper;
import io.swagger.codegen.v3.maven.plugin.CodeGenMojo;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.BuildPluginManager;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

import java.io.File;
import java.util.Map;

/**
 * The type Sdk gen mojo.
 *
 * @author gudaoxuri
 */
@Mojo(name = "generate", defaultPhase = LifecyclePhase.DEPLOY, threadSafe = true)
public class SDKGenMojo extends CodeGenMojo {

    private static final String FLAG_DEW_SDK_GEN_SKIP = "dew_sdkgen_skip";
    private static final String FLAG_DEW_SDK_RELEASE_SKIP = "dew_sdkrelease_skip";

    @Parameter(property = FLAG_DEW_SDK_GEN_SKIP)
    protected boolean sdkGenSkip;

    @Parameter(property = FLAG_DEW_SDK_RELEASE_SKIP)
    protected boolean sdkReleaseSkip;

    @Parameter(name = "language", defaultValue = "group.idealworld.dew.sdkgen.lang.java.DewJavaClientCodegen")
    private String language;

    @Parameter(name = "inputSpec")
    private String inputSpec;

    @Parameter(defaultValue = "${session}", readonly = true)
    protected MavenSession mavenSession;

    @Parameter(defaultValue = "${project}", readonly = true)
    protected MavenProject mavenProject;

    @Component
    private BuildPluginManager pluginManager;

    /**
     * 扩展功能.
     *
     * @throws MojoExecutionException
     */
    @Override
    public void execute() throws MojoExecutionException {
        Map<String, String> props = MavenHelper.getMavenProperties(mavenSession);
        MavenHelper.formatParameters(FLAG_DEW_SDK_GEN_SKIP, props)
                .ifPresent(obj -> sdkGenSkip = Boolean.parseBoolean(obj));
        MavenHelper.formatParameters(FLAG_DEW_SDK_RELEASE_SKIP, props)
                .ifPresent(obj -> sdkReleaseSkip = Boolean.parseBoolean(obj));
        if (sdkGenSkip) {
            return;
        }
        // 添加默认参数
        File output = GenerateProcess.process(this, mavenProject, mavenSession, pluginManager, language, inputSpec);
        super.execute();
        if (sdkReleaseSkip) {
            return;
        }
        // 自动部署
        DeployProcess.process(mavenProject, mavenSession, pluginManager, output);

    }


}
