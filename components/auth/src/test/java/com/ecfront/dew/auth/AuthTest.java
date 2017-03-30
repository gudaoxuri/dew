package com.ecfront.dew.auth;

import com.ecfront.dew.auth.entity.Tenant;
import com.ecfront.dew.common.JsonHelper;
import com.ecfront.dew.common.Resp;
import com.ecfront.dew.core.Dew;
import com.ecfront.dew.core.dto.PageDTO;
import com.ecfront.dew.core.repository.DewRepositoryFactoryBean;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootApplication
@EnableJpaRepositories(
        repositoryFactoryBeanClass = DewRepositoryFactoryBean.class
)
@SpringBootTest(classes = AuthTest.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ComponentScan(basePackageClasses = {AuthTest.class, Dew.class})
public class AuthTest {

    @Autowired
    private TestRestTemplate testRestTemplate;

    @Test
    public void testTenant() throws Exception {
        Tenant tenant = new Tenant();
        tenant.setCode("001");
        tenant.setName("默认租户");
        Resp tenantR = testRestTemplate.postForObject("/auth/manage", tenant, Resp.class);
        Assert.assertTrue(tenantR.ok());
        tenant = JsonHelper.toObject(tenantR.getBody(), Tenant.class);
        tenant.setCode("000");
        testRestTemplate.put("/auth/manage/" + tenant.getId(), tenant);
        tenantR = testRestTemplate.getForObject("/auth/manage/" + tenant.getId(), Resp.class);
        tenant = JsonHelper.toObject(tenantR.getBody(), Tenant.class);
        Assert.assertTrue(tenantR.ok() && tenant.getCode().equals("000"));
        Resp tenantsR = testRestTemplate.getForObject("/auth/manage/", Resp.class);
        Assert.assertEquals(((List) tenantsR.getBody()).size(), 1);
        Resp tenantPageR = testRestTemplate.getForObject("/auth/manage/0/10", Resp.class);
        PageDTO<Tenant> tenantPage = JsonHelper.toObject(tenantPageR.getBody(), PageDTO.class);
        Assert.assertEquals(tenantPage.getObjects().size(), 1);
        testRestTemplate.delete("/auth/manage/" + tenant.getId());
        tenantsR = testRestTemplate.getForObject("/auth/manage/", Resp.class);
        Assert.assertEquals(((List) tenantsR.getBody()).size(), 0);
    }

}
