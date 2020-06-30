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

import com.ecfront.dew.common.$;
import group.idealworld.dew.sdkgen.helper.NameHelper;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.BuildPluginManager;
import org.apache.maven.project.MavenProject;

import java.io.File;
import java.util.HashMap;

/**
 * The type Generate process.
 *
 * @author gudaoxuri
 */
@Slf4j
public class GenerateProcess {

    /**
     * Process.
     *
     * @param mojo          the mojo
     * @param mavenProject  the maven project
     * @param mavenSession  the maven session
     * @param pluginManager the plugin manager
     * @param language      the language
     * @param inputSpec     the input spec
     * @return the file
     */
    public static File process(SDKGenMojo mojo, MavenProject mavenProject, MavenSession mavenSession, BuildPluginManager pluginManager,
                               String language, String inputSpec) {
        log.info("Generate SDK by {}", language);
        /*MavenHelper.invoke("io.swagger.core.v3", "swagger-maven-plugin", "2.1.1",
                "resolve", new HashMap<>() {
                    {
                        put("outputFileName", output.getParent());
                        put("goals", new HashMap<>() {
                            {
                                put("goal", "-P release");
                            }
                        });
                        put("mavenOpts", "");
                    }
                }, mavenProject, mavenSession, pluginManager);*/

        String groupId = mavenProject.getGroupId();
        String artifactId = mavenProject.getArtifactId() + ".sdk";
        String basePackage = NameHelper.formatPackage(groupId + "." + mavenProject.getArtifactId() + ".sdk");
        setAndGetIfNotExist(mojo, "apiPackage", basePackage + ".api");
        setAndGetIfNotExist(mojo, "modelPackage", basePackage + ".model");
        setAndGetIfNotExist(mojo, "invokerPackage", basePackage + ".invoker");
        setAndGetIfNotExist(mojo, "groupId", groupId);
        setAndGetIfNotExist(mojo, "artifactId", artifactId);
        setAndGetIfNotExist(mojo, "artifactVersion", mavenProject.getVersion());
        setValueToParentField(mojo, "language", language);
        setValueToParentField(mojo, "inputSpec", inputSpec);
        String lang;
        switch (language) {
            case "group.idealworld.dew.sdkgen.lang.java.DewJavaClientCodegen":
                lang = "java";
                break;
            default:
                lang = language;
        }
        String finalLang = lang;
        setAndGetIfNotExist(mojo, "configOptions", new HashMap<String, Object>() {
            {
                put("sourceFolder", "src/main/" + finalLang);
            }
        });
        return (File) $.bean.getValue(mojo, "output");
    }

    private static <T> T setAndGetIfNotExist(SDKGenMojo mojo, String field, T defaultValue) {
        T value = (T) $.bean.getValue(mojo, field);
        if (value != null) {
            return value;
        }
        $.bean.setValue(mojo, field, defaultValue);
        return defaultValue;
    }

    @SneakyThrows
    private static void setValueToParentField(SDKGenMojo mojo, String field, Object value) {
        $.bean.setValue(mojo, mojo.getClass().getSuperclass().getDeclaredField(field), value);
    }

}
