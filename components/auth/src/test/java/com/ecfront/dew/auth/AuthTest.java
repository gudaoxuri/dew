package com.ecfront.dew.auth;

import com.ecfront.dew.auth.entity.Tenant;
import com.ecfront.dew.common.Resp;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = AuthTest.class)
public class AuthTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    public void testTenant() throws Exception {
        Tenant tenant = new Tenant();
        Resp<Tenant> tenantR = restTemplate.postForObject("/auth/manage", tenant, Resp.class);
        Assert.assertTrue(tenantR.ok());
    }

}
