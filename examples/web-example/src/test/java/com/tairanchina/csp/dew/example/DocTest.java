package com.tairanchina.csp.dew.example;

import com.ecfront.dew.common.$;
import com.tairanchina.csp.dew.core.doc.DocController;
import com.tairanchina.csp.dew.example.web.WebExampleApplication;
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
    private DocController docController;

    @Test
    public void testDocByJson() throws IOException {
        docController.doGenerateOfflineDoc("Web测试", "测试说明", new HashMap<String, String>() {{
            put("测试环境", "http://test.ecfront.com");
            put("生产环境", "http://prod.ecfront.com");
        }}, new ArrayList<String>() {{
            add($.file.readAllByClassPath("swagger1.json", "UTF-8"));
        }});
    }

    @Test
    public void testDocByUrl() throws IOException {
        docController.generateOfflineDoc("Web测试", "测试说明", new HashMap<String, String>() {{
            put("测试环境", "http://test.ecfront.com");
            put("生产环境", "http://prod.ecfront.com");
        }}, new ArrayList<String>() {{
            add("http://127.0.0.1:80/v2/api-docs");
        }});
    }

}