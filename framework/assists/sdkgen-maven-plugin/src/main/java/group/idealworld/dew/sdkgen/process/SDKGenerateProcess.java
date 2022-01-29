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

package group.idealworld.dew.sdkgen.process;

import com.ecfront.dew.common.$;
import com.ecfront.dew.common.tuple.Tuple3;
import group.idealworld.dew.sdkgen.helper.NameHelper;
import group.idealworld.dew.sdkgen.maven.SDKGenerateMojo;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.maven.project.MavenProject;

import java.io.File;
import java.util.HashMap;

import static group.idealworld.dew.sdkgen.Constants.GENERATED_BASE_PATH;
import static group.idealworld.dew.sdkgen.Constants.GENERATED_OPENAPI_FILE_NAME;

/**
 * The type Generate process.
 *
 * @author gudaoxuri
 */
@Slf4j
public class SDKGenerateProcess {

    /**
     * Process.
     *
     * @param mojo         the mojo
     * @param mavenProject the maven project
     * @param language     the language
     * @return the tuple: groupId, artifactId, version
     */
    public static Tuple3<String, String, String> process(SDKGenerateMojo mojo,
                                                         MavenProject mavenProject, String language) {
        log.info("Generate SDK by {}", language);

        String groupId = mavenProject.getGroupId();
        String artifactId = mavenProject.getArtifactId() + ".sdk";
        setAndGetIfNotExist(mojo, "groupId", groupId);
        setAndGetIfNotExist(mojo, "artifactId", artifactId);
        setAndGetIfNotExist(mojo, "artifactVersion", mavenProject.getVersion());

        String basePackage = NameHelper.formatPackage(groupId + "." + mavenProject.getArtifactId() + ".sdk");
        setAndGetIfNotExist(mojo, "apiPackage", basePackage + ".api");
        setAndGetIfNotExist(mojo, "modelPackage", basePackage + ".model");
        setAndGetIfNotExist(mojo, "invokerPackage", basePackage + ".invoker");

        String basePath = mavenProject.getBasedir().getPath() + File.separator +
                "target" + File.separator +
                GENERATED_BASE_PATH;
        setValueToParentField(mojo, "output", new File(basePath + File.separator + "sdk"));
        setValueToParentField(mojo, "inputSpec",
                basePath + File.separator + GENERATED_OPENAPI_FILE_NAME);
        if ("java".equals(language)) {
            setValueToParentField(mojo, "language", "group.idealworld.dew.sdkgen.lang.java.DewJavaClientCodegen");
        }
        setAndGetIfNotExist(mojo, "configOptions", new HashMap<String, Object>() {
            {
                put("sourceFolder", "src/main/" + language);
            }
        });
        return new Tuple3<>(groupId, artifactId, mavenProject.getVersion());
    }

    private static <T> T setAndGetIfNotExist(SDKGenerateMojo mojo, String field, T defaultValue) {
        T value = (T) $.bean.getValue(mojo, field);
        if (value != null) {
            return value;
        }
        $.bean.setValue(mojo, field, defaultValue);
        return defaultValue;
    }

    @SneakyThrows
    private static void setValueToParentField(SDKGenerateMojo mojo, String field, Object value) {
        $.bean.setValue(mojo, mojo.getClass().getSuperclass().getDeclaredField(field), value);
    }

}
