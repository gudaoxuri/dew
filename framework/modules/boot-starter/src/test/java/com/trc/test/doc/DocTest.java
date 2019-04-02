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

package com.trc.test.doc;


import org.junit.Assert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

@Component
public class DocTest {

    @Autowired
    private TestRestTemplate testRestTemplate;

    public void testAll() throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> entity = new HttpEntity<>(
                "{\"docName\":\"Dew API 文档\",\n" +
                        "\"docDesc\":\"此为Dew的接口文档\\n描述可以换行\",\n" +
                        "\"visitUrls\":{\n" +
                        "        \"开发环境\":\"http://dev.dew.ecfront.com\",\n" +
                        "        \"测试环境\":\"http://test.dew.ecfront.com\"\n" +
                        "  }," +
                        "\"swaggerJsonUrls\":[\"http://localhost:8080/v2/api-docs\"]" +
                        "}", headers);
        String docStr = testRestTemplate
                .exchange("/management/doc/offline", HttpMethod.PUT, entity, String.class)
                .getBody();

        Assert.assertTrue(docStr.contains("此为Dew的接口文档"));
        Assert.assertTrue(docStr.contains("**服务URI**: /boot-start-test/"));
        Assert.assertTrue(docStr.contains("请求URI: ``GET /test/valid-method-spring/{age}``"));
    }


}
