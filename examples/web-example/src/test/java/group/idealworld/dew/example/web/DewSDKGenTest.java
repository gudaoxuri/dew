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

package group.idealworld.dew.example.web;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
* The type Dew sdk gen test.
*
* You can customize (do not change the file name and file path) this file to fit your project.
*
* @author gudaoxuri
*/
@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class DewSDKGenTest {

    @Autowired
    private TestRestTemplate restTemplate;

    /**
    * Open id generate.
    *
    * @throws IOException the io exception
    */
    @Test
    public void openIdGenerate() throws IOException {
        var openAPIJson = restTemplate.getForObject("/v3/api-docs", String.class);
        var targetPath = new File(DewSDKGenTest.class.getResource("/").getPath()).getParent() + File.separator + "dew_sdkgen";
        Files.createDirectories(Paths.get(targetPath));
        Files.write(Paths.get(targetPath + File.separator + "openapi.json"), openAPIJson.getBytes(StandardCharsets.UTF_8));
    }

}