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

package com.trc.test;


import com.ecfront.dew.common.$;
import com.ecfront.dew.common.Resp;
import group.idealworld.dew.Dew;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.nio.charset.StandardCharsets;


/**
 * Ident info test.
 *
 * @author gudaoxuri
 */
@ExtendWith({SpringExtension.class})
@SpringBootTest(classes = BootTestApplication.class, webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@ActiveProfiles("ident")
public class IdentInfoTest {

    @Autowired
    private TestRestTemplate testRestTemplate;

    @Test
    public void testIdentInfo() throws Exception {
        var response = testRestTemplate.getForEntity("/ident-info", Resp.class);
        Assertions.assertEquals(200, response.getStatusCode().value());
        Assertions.assertEquals("400", response.getBody().getCode());
        Assertions.assertEquals("[]The request is missing [" + Dew.dewConfig.getSecurity().getIdentInfoFlag() + "] in header", response.getBody().getMessage());

        HttpHeaders headers = new HttpHeaders();
        headers.set(Dew.dewConfig.getSecurity().getIdentInfoFlag(), $.security.encodeStringToBase64("{\"token\":\"t1\",roles:[\"r1\"]}", StandardCharsets.UTF_8));
        var result = testRestTemplate.exchange("/test/ident-info",
                HttpMethod.GET, new HttpEntity<>("", headers), Resp.class).getBody().getBody();
        var resultJson = $.json.toJson(result);
        Assertions.assertEquals("t1", resultJson.get("token").asText());
        Assertions.assertEquals("r1", resultJson.get("roles").get(0).asText());

    }

}
