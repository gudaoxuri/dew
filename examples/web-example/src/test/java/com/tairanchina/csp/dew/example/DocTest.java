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

package com.tairanchina.csp.dew.example;

import com.ecfront.dew.common.$;
import com.ecfront.dew.common.Resp;
import com.tairanchina.csp.dew.core.doc.DocService;
import com.tairanchina.csp.dew.example.web.WebExampleApplication;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = WebExampleApplication.class, webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class DocTest {

    @Autowired
    private DocService docService;

    @Test
    public void testDocByJson() throws IOException {
        Resp<String> docR = docService.doGenerateOfflineDoc("Web测试", "测试说明", new HashMap<String, String>() {{
            put("测试环境", "http://test.ecfront.com");
            put("生产环境", "http://prod.ecfront.com");
        }}, new ArrayList<String>() {{
            add($.file.readAllByClassPath("swagger1.json", "UTF-8"));
        }});
        Assert.assertTrue(docR.ok());
        Assert.assertTrue(docR.getBody().contains("/api/aci/deepRequest"));
    }

    @Test
    public void testDocByUrl() throws IOException {
        Resp<String> docR = docService.generateOfflineDoc("Web测试", "测试说明", new HashMap<String, String>() {{
            put("测试环境", "http://test.ecfront.com");
            put("生产环境", "http://prod.ecfront.com");
        }}, new ArrayList<String>() {{
            add("http://127.0.0.1:80/v2/api-docs");
        }});
        Assert.assertTrue(docR.ok());
        Assert.assertTrue(docR.getBody().contains("valid-update-dep"));
    }

}