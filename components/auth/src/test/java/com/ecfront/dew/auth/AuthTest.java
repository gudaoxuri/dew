package com.ecfront.dew.auth;

import com.ecfront.dew.auth.entity.Tenant;
import com.ecfront.dew.auth.service.TenantService;
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

    @Autowired
    private TenantService tenantService;

    @Test
    public void testTenantById() throws Exception {
        // save
        Tenant tenant = new Tenant();
        tenant.setCode("001");
        tenant.setName("默认租户");
        tenant.setImage("");
        tenant.setCategory("");
        Resp tenantR = testRestTemplate.postForObject("/auth/manage/tenant/", tenant, Resp.class);
        Assert.assertTrue(tenantR.ok());
        // update
        tenant = JsonHelper.toObject(tenantR.getBody(), Tenant.class);
        tenant.setName("默认租户1");
        testRestTemplate.put("/auth/manage/tenant/" + tenant.getId(), tenant);
        // get
        tenantR = testRestTemplate.getForObject("/auth/manage/tenant/" + tenant.getId(), Resp.class);
        tenant = JsonHelper.toObject(tenantR.getBody(), Tenant.class);
        Assert.assertTrue(tenantR.ok() && tenant.getName().equals("默认租户1"));
        // disable
        testRestTemplate.getForObject("/auth/manage/tenant/" + tenant.getId() + "/disable", Resp.class);
        tenant = JsonHelper.toObject(testRestTemplate.getForObject("/auth/manage/tenant/" + tenant.getId(), Resp.class).getBody(), Tenant.class);
        Assert.assertTrue(!tenant.getEnable());
        // enable
        testRestTemplate.getForObject("/auth/manage/tenant/" + tenant.getId() + "/enable", Resp.class);
        tenant = JsonHelper.toObject(testRestTemplate.getForObject("/auth/manage/tenant/" + tenant.getId(), Resp.class).getBody(), Tenant.class);
        Assert.assertTrue(tenant.getEnable());
        // find
        Resp tenantsR = testRestTemplate.getForObject("/auth/manage/tenant/", Resp.class);
        Assert.assertEquals(((List) tenantsR.getBody()).size(), 1);
        // find enable
        tenantsR = testRestTemplate.getForObject("/auth/manage/tenant/?enable=true", Resp.class);
        Assert.assertEquals(((List) tenantsR.getBody()).size(), 1);
        // find disable
        tenantsR = testRestTemplate.getForObject("/auth/manage/tenant/?enable=false", Resp.class);
        Assert.assertEquals(((List) tenantsR.getBody()).size(), 0);
        // paging
        Resp tenantPageR = testRestTemplate.getForObject("/auth/manage/tenant/0/10", Resp.class);
        PageDTO<Tenant> tenantPage = JsonHelper.toObject(tenantPageR.getBody(), PageDTO.class);
        Assert.assertEquals(tenantPage.getObjects().size(), 1);
        // paging enable
        tenantPageR = testRestTemplate.getForObject("/auth/manage/tenant/0/10?enable=true", Resp.class);
        tenantPage = JsonHelper.toObject(tenantPageR.getBody(), PageDTO.class);
        Assert.assertEquals(tenantPage.getObjects().size(), 1);
        // paging disable
        tenantPageR = testRestTemplate.getForObject("/auth/manage/tenant/0/10?enable=false", Resp.class);
        tenantPage = JsonHelper.toObject(tenantPageR.getBody(), PageDTO.class);
        Assert.assertEquals(tenantPage.getObjects().size(), 0);
        // exist
        Assert.assertTrue(tenantService.existById(tenant.getId()).getBody());
        // delete
        testRestTemplate.delete("/auth/manage/tenant/" + tenant.getId());
        tenantsR = testRestTemplate.getForObject("/auth/manage/tenant/", Resp.class);
        Assert.assertEquals(((List) tenantsR.getBody()).size(), 0);
        // exist
        Assert.assertFalse(tenantService.existById(tenant.getId()).getBody());
    }

    @Test
    public void testTenantByCode() throws Exception {
        // save
        Tenant tenant = new Tenant();
        tenant.setCode("001");
        tenant.setName("默认租户");
        tenant.setImage("");
        tenant.setCategory("");
        Resp tenantR = testRestTemplate.postForObject("/auth/manage/tenant/", tenant, Resp.class);
        Assert.assertTrue(tenantR.ok());
        // update
        tenant = JsonHelper.toObject(tenantR.getBody(), Tenant.class);
        tenant.setName("默认租户1");
        testRestTemplate.put("/auth/manage/tenant/code/" + tenant.getCode(), tenant);
        // get
        tenantR = testRestTemplate.getForObject("/auth/manage/tenant/code/" + tenant.getCode(), Resp.class);
        tenant = JsonHelper.toObject(tenantR.getBody(), Tenant.class);
        Assert.assertTrue(tenantR.ok() && tenant.getName().equals("默认租户1"));
        // disable
        testRestTemplate.getForObject("/auth/manage/tenant/code/" + tenant.getCode() + "/disable", Resp.class);
        tenant = JsonHelper.toObject(testRestTemplate.getForObject("/auth/manage/tenant/code/" + tenant.getCode(), Resp.class).getBody(), Tenant.class);
        Assert.assertTrue(!tenant.getEnable());
        // enable
        testRestTemplate.getForObject("/auth/manage/tenant/code/" + tenant.getCode() + "/enable", Resp.class);
        tenant = JsonHelper.toObject(testRestTemplate.getForObject("/auth/manage/tenant/code/" + tenant.getCode(), Resp.class).getBody(), Tenant.class);
        Assert.assertTrue(tenant.getEnable());
        // exist
        Assert.assertTrue(tenantService.existByCode(tenant.getCode()).getBody());
        // delete
        testRestTemplate.delete("/auth/manage/tenant/code/" + tenant.getCode());
        Resp tenantsR = testRestTemplate.getForObject("/auth/manage/tenant/", Resp.class);
        Assert.assertEquals(((List) tenantsR.getBody()).size(), 0);
        // exist
        Assert.assertFalse(tenantService.existByCode(tenant.getCode()).getBody());
    }
}
