package com.tairanchina.csp.dew.example.rest;

import com.ecfront.dew.common.HttpHelper;
import com.tairanchina.csp.dew.Dew;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootApplication
@SpringBootTest(classes = ServiceExampleApplication.class)
@ComponentScan(basePackageClasses = {Dew.class, RequestTest.class})
public class RequestTest {

    @Test
    public void testAll() throws Exception {
        HttpHelper.ResponseWrap result = Dew.EB.get("http://SERVICE-CLOUD-EXAMPLE/example");
        Assert.assertEquals(null, result);
    }

}
