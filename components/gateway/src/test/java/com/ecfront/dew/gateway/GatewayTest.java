package com.ecfront.dew.gateway;

import com.ecfront.dew.core.Dew;
import com.ecfront.dew.gateway.auth.LocalCacheContainer;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashSet;

@RunWith(SpringRunner.class)
@SpringBootApplication
@SpringBootTest(classes = GatewayApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ComponentScan(basePackageClasses = {Dew.class, GatewayTest.class})
public class GatewayTest {

    @Test
    public void testAll() throws Exception {
        testPathMatch();
    }

    private void testPathMatch() {
        LocalCacheContainer.addResource("1", "*", "/mange/**");
        LocalCacheContainer.addResource("2", "GET", "/mange/**");
        LocalCacheContainer.addResource("3", "GET", "/mange/account/*");
        LocalCacheContainer.addResource("4", "POST", "/public/login");

        Assert.assertFalse(LocalCacheContainer.getBestMathResourceCode("POST", "/setting").isPresent());
        Assert.assertEquals(LocalCacheContainer.getBestMathResourceCode("GET", "/mange/account/1").get(), "3");
        Assert.assertEquals(LocalCacheContainer.getBestMathResourceCode("GET", "/mange/1").get(), "2");
        Assert.assertEquals(LocalCacheContainer.getBestMathResourceCode("GET", "/mange").get(), "2");
        Assert.assertEquals(LocalCacheContainer.getBestMathResourceCode("PUT", "/mange/account/1").get(), "1");
        Assert.assertEquals(LocalCacheContainer.getBestMathResourceCode("POST", "/public/login").get(), "4");


        LocalCacheContainer.addRole("guest", new HashSet<>());
        LocalCacheContainer.addRole("admin", new HashSet<String>() {{
            add("1");
        }});

        Assert.assertFalse(LocalCacheContainer.auth(new HashSet<String>() {{
            add("guest");
        }}, "1"));
        Assert.assertTrue(LocalCacheContainer.auth(new HashSet<String>() {{
            add("guest");
            add("admin");
        }}, "1"));
    }


}
