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
import group.idealworld.dew.sdkgen.process.TestGenerateProcess;
import lombok.extern.slf4j.Slf4j;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import java.util.Map;

import static group.idealworld.dew.sdkgen.Constants.*;

/**
 * The type Test generate mojo.
 *
 * @author gudaoxuri
 */
@Slf4j
@Mojo(name = "testGen", defaultPhase = LifecyclePhase.VALIDATE, threadSafe = true)
public class TestGenerateMojo extends AbstractMojo {

    /**
     * The Sdk gen.
     */
    @Parameter(property = FLAG_DEW_SDK_GEN)
    protected boolean sdkGen;

    @Parameter(name = FLAG_DEW_MAIN_CLASS)
    private String mainClass;

    @Parameter(name = FLAG_DEW_SDK_GEN_OPENAPI_PATH)
    private String openAPIPath;

    /**
     * The Maven session.
     */
    @Parameter(defaultValue = "${session}", readonly = true)
    protected MavenSession mavenSession;

    /**
     * 扩展功能.
     */
    @Override
    public void execute() {
        Map<String, String> props = MavenHelper.getMavenProperties(mavenSession);
        MavenHelper.formatParameters(FLAG_DEW_SDK_GEN, props)
                .ifPresent(obj -> sdkGen = Boolean.parseBoolean(obj));
        MavenHelper.formatParameters(FLAG_DEW_MAIN_CLASS, props)
                .ifPresent(obj -> mainClass = obj);
        MavenHelper.formatParameters(FLAG_DEW_SDK_GEN_OPENAPI_PATH, props)
                .ifPresent(obj -> openAPIPath = obj);
        if (!sdkGen) {
            log.debug("Parameter [{}=false], skip the SDK generation.", FLAG_DEW_SDK_GEN);
            return;
        }
        TestGenerateProcess.process(mavenSession.getCurrentProject().getBasedir(), openAPIPath, mainClass);
    }


}
