/*
 * Copyright 2022. the original author or authors
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
import group.idealworld.dew.sdkgen.process.SDKGenerateProcess;
import group.idealworld.dew.sdkgen.process.SDKReleaseProcess;
import io.swagger.codegen.v3.maven.plugin.CodeGenMojo;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.BuildPluginManager;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

import java.util.Map;

import static group.idealworld.dew.sdkgen.Constants.*;

/**
 * The type SDK Generate mojo.
 *
 * @author gudaoxuri
 */
@Mojo(name = "sdkGen", defaultPhase = LifecyclePhase.DEPLOY, threadSafe = true)
public class SDKGenerateMojo extends CodeGenMojo {

    /**
     * The Sdk gen.
     */
    @Parameter(property = FLAG_DEW_SDK_GEN)
    protected boolean sdkGen;

    @Parameter(property = FLAG_DEW_SDK_RELEASE_SKIP)
    protected boolean sdkReleaseSkip;

    @Parameter(name = "language", defaultValue = "java")
    private String language;

    // 父插件 inputSpec 必填，此参数没有意义，实现上会以自定义的路径逻辑覆写
    @Parameter(name = "inputSpec", defaultValue = ".")
    private String inputSpec;

    /**
     * The Maven session.
     */
    @Parameter(defaultValue = "${session}", readonly = true)
    protected MavenSession mavenSession;

    /**
     * The Maven project.
     */
    @Parameter(defaultValue = "${project}", readonly = true)
    protected MavenProject mavenProject;

    @Component
    private BuildPluginManager pluginManager;

    @Override
    public void execute() throws MojoExecutionException {
        Map<String, String> props = MavenHelper.getMavenProperties(mavenSession);
        MavenHelper.formatParameters(FLAG_DEW_SDK_GEN, props)
                .ifPresent(obj -> sdkGen = Boolean.parseBoolean(obj));
        MavenHelper.formatParameters(FLAG_DEW_SDK_RELEASE_SKIP, props)
                .ifPresent(obj -> sdkReleaseSkip = Boolean.parseBoolean(obj));
        MavenHelper.formatParameters(FLAG_DEW_SDK_GEN_LANG, props)
                .ifPresent(obj -> language = obj);
        if (!sdkGen) {
            return;
        }
        var sdkMavenInfo = SDKGenerateProcess.process(this, mavenProject, language);
        super.execute();
        if (sdkReleaseSkip) {
            return;
        }
        // 自动部署
        SDKReleaseProcess.process(sdkMavenInfo, mavenProject, mavenSession, pluginManager);
    }


}
