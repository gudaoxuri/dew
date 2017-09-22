package com.ecfront.dew.example;

import com.ecfront.dew.common.$;
import com.ecfront.dew.core.Dew;
import com.ecfront.dew.core.doc.DocProcessor;
import com.ecfront.dew.example.web.WebExampleApplication;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Profile;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;

@RunWith(SpringRunner.class)
@SpringBootApplication
@SpringBootTest(classes = WebExampleApplication.class, webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@ComponentScan(basePackageClasses = {Dew.class, WebExampleApplication.class})
public class DocTest {

    @Test
    public void empty() throws IOException {
        DocProcessor.create($.http.get("http://127.0.0.1:8080/v2/api-docs"));
    }

}