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
import com.github.jknack.handlebars.Handlebars;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Objects;
import java.util.stream.Collectors;

import static group.idealworld.dew.sdkgen.Constants.*;

/**
 * The type Test generate process.
 *
 * @author gudaoxuri
 */
@Slf4j
public class TestGenerateProcess {

    private static final String TEST_FILE_NAME = "DewSDKGenTest.java";

    /**
     * Process file.
     *
     * @param basePath    the base path
     * @param openAPIPath the open api path
     * @param mainClass   the main class
     */
    @SneakyThrows
    public static void process(File basePath, String openAPIPath, String mainClass) {
        log.info("Test class generation.");
        if (openAPIPath != null && !openAPIPath.isBlank()) {
            log.debug("Parameter [{}={}], skip test class generation.", FLAG_DEW_SDK_GEN_OPENAPI_PATH, openAPIPath);
            writeOpenAPIFile(basePath, openAPIPath);
            return;
        }
        String baseClassPath;
        if (mainClass == null || mainClass.isBlank()) {
            baseClassPath = scanMainClassPath(basePath);
        } else {
            baseClassPath = mainClass.substring(0, mainClass.lastIndexOf("."));
            log.debug("Parameter [{}={}], skip main class scan.", FLAG_DEW_MAIN_CLASS, mainClass);
        }
        File testFile = new File(basePath.getPath() + File.separator +
                "src" + File.separator +
                "test" + File.separator +
                "java" + File.separator +
                baseClassPath.replaceAll("\\.", File.separator.equals("\\") ? "\\" + File.separator : File.separator) + File.separator +
                TEST_FILE_NAME);
        if (testFile.exists()) {
            log.debug("Test file [{}] already exists, skip test file generation.", testFile.getPath());
            return;
        }
        writeTestFile(testFile.getPath(), baseClassPath);
    }

    private static void writeOpenAPIFile(File basePath, String openAPIPath) throws IOException {
        var openAPIJson = $.http.get(openAPIPath);
        var targetPath = basePath.getPath() + File.separator + "target" + File.separator + "dew-sdkgen";
        Files.createDirectories(Paths.get(targetPath));
        Files.write(Paths.get(targetPath + File.separator + GENERATED_OPENAPI_FILE_NAME), openAPIJson.getBytes());
    }

    private static String scanMainClassPath(File basePath) {
        String[] mainClassPath = {null};
        scanMainClassPath(null, Objects.requireNonNull(new File(basePath.getPath() + File.separator + "src" + File.separator + "main")
                .listFiles((dir, name) ->
                        // TODO 其它语言
                        name.equals("java") || name.equals("scala") || name.equals("groovy")
                )), mainClassPath);
        if (mainClassPath[0] == null) {
            throw new RuntimeException("No main method found.");
        } else {
            return mainClassPath[0];
        }
    }

    private static void scanMainClassPath(String classPackage, File[] files, String[] foundMainClassPath) {
        for (File file : files) {
            if (foundMainClassPath[0] != null) {
                return;
            }
            if (file.isDirectory()) {
                scanMainClassPath(
                        // 为空时表示第一次进入，文件名为java scala groovy 等，要忽略
                        classPackage == null
                                ? "" : classPackage.isBlank()
                                ? file.getName() : classPackage + "." + file.getName(),
                        Objects.requireNonNull(file.listFiles()), foundMainClassPath);
            } else {
                var fileContent = $.file.readAllByFile(file, StandardCharsets.UTF_8);
                if (fileContent.contains("@SpringBootApplication")
                        || fileContent.contains("static void main(String[]")
                        || fileContent.contains("def main(")
                        || fileContent.contains("static void main(")
                ) {
                    foundMainClassPath[0] = classPackage;
                }
            }
        }
    }

    @SneakyThrows
    private static void writeTestFile(String testFilePath, String baseClassPath) {
        String testFileStr = new BufferedReader(new InputStreamReader(
                TestGenerateProcess.class.getResourceAsStream("/testfile/testFile.mustache")))
                .lines().collect(Collectors.joining("\n"));
        var handlebars = new Handlebars();
        var template = handlebars.compileInline(testFileStr);
        testFileStr = template.apply(new HashMap<>() {
            {
                put("package", baseClassPath);
                put("sdkGenPath", GENERATED_BASE_PATH);
            }
        });
        Files.createDirectories(Paths.get(testFilePath).getParent());
        Files.write(Paths.get(testFilePath), testFileStr.getBytes());
    }

}
