package com.ecfront.dew.auth;

import com.ecfront.dew.auth.dto.LoginReq;
import com.ecfront.dew.auth.dto.ModifyLoginInfoReq;
import com.ecfront.dew.auth.entity.Account;
import com.ecfront.dew.auth.entity.Resource;
import com.ecfront.dew.auth.entity.Role;
import com.ecfront.dew.auth.entity.Tenant;
import com.ecfront.dew.auth.repository.AccountRepository;
import com.ecfront.dew.auth.repository.ResourceRepository;
import com.ecfront.dew.auth.repository.RoleRepository;
import com.ecfront.dew.auth.repository.TenantRepository;
import com.ecfront.dew.auth.service.*;
import com.ecfront.dew.common.JsonHelper;
import com.ecfront.dew.common.Resp;
import com.ecfront.dew.common.StandardCode;
import com.ecfront.dew.core.Dew;
import com.ecfront.dew.core.dto.OptInfo;
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

import java.util.HashSet;
import java.util.List;
import java.util.Map;

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
    @Autowired
    private ResourceService resourceService;
    @Autowired
    private RoleService roleService;
    @Autowired
    private AccountService accountService;
    @Autowired
    private AuthService authService;
    @Autowired
    private TenantRepository tenantRepository;
    @Autowired
    private ResourceRepository resourceRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private CacheManager cacheManager;

    @Test
    public void testAll() throws Exception {
        testTenantById();
        testTenantByCode();
        testAccount();
        testMange();
        testAuth();
    }

    private void testTenantById() throws Exception {
        tenantRepository.deleteAll();
        // save
        Tenant tenant = new Tenant();
        tenant.setCode("001");
        tenant.setName("默认租户");
        tenant.setImage("");
        tenant.setCategory("");
        Resp tenantR = testRestTemplate.postForObject("/manage/tenant/", tenant, Resp.class);
        Assert.assertTrue(tenantR.ok());
        // update
        tenant = JsonHelper.toObject(tenantR.getBody(), Tenant.class);
        tenant.setName("默认租户1");
        testRestTemplate.put("/manage/tenant/" + tenant.getId(), tenant);
        // get
        tenantR = testRestTemplate.getForObject("/manage/tenant/" + tenant.getId(), Resp.class);
        tenant = JsonHelper.toObject(tenantR.getBody(), Tenant.class);
        Assert.assertTrue(tenantR.ok() && tenant.getName().equals("默认租户1"));
        // disable
        testRestTemplate.delete("/manage/tenant/" + tenant.getId() + "/disable", Resp.class);
        tenant = JsonHelper.toObject(testRestTemplate.getForObject("/manage/tenant/" + tenant.getId(), Resp.class).getBody(), Tenant.class);
        Assert.assertTrue(!tenant.getEnable());
        // enable
        testRestTemplate.put("/manage/tenant/" + tenant.getId() + "/enable", "", Resp.class);
        tenant = JsonHelper.toObject(testRestTemplate.getForObject("/manage/tenant/" + tenant.getId(), Resp.class).getBody(), Tenant.class);
        Assert.assertTrue(tenant.getEnable());
        // find
        Resp tenantsR = testRestTemplate.getForObject("/manage/tenant/", Resp.class);
        Assert.assertEquals(((List) tenantsR.getBody()).size(), 1);
        // find enable
        tenantsR = testRestTemplate.getForObject("/manage/tenant/?enable=true", Resp.class);
        Assert.assertEquals(((List) tenantsR.getBody()).size(), 1);
        // find disable
        tenantsR = testRestTemplate.getForObject("/manage/tenant/?enable=false", Resp.class);
        Assert.assertEquals(((List) tenantsR.getBody()).size(), 0);
        // paging
        Resp tenantPageR = testRestTemplate.getForObject("/manage/tenant/0/10", Resp.class);
        PageDTO<Tenant> tenantPage = JsonHelper.toObject(tenantPageR.getBody(), PageDTO.class);
        Assert.assertEquals(tenantPage.getObjects().size(), 1);
        // paging enable
        tenantPageR = testRestTemplate.getForObject("/manage/tenant/0/10?enable=true", Resp.class);
        tenantPage = JsonHelper.toObject(tenantPageR.getBody(), PageDTO.class);
        Assert.assertEquals(tenantPage.getObjects().size(), 1);
        // paging disable
        tenantPageR = testRestTemplate.getForObject("/manage/tenant/0/10?enable=false", Resp.class);
        tenantPage = JsonHelper.toObject(tenantPageR.getBody(), PageDTO.class);
        Assert.assertEquals(tenantPage.getObjects().size(), 0);
        // exist
        Assert.assertTrue(tenantService.existById(tenant.getId()).getBody());
        tenantRepository.deleteAll();
    }

    private void testTenantByCode() throws Exception {
        tenantRepository.deleteAll();
        // save
        Tenant tenant = new Tenant();
        tenant.setCode("001");
        tenant.setName("默认租户");
        tenant.setImage("");
        tenant.setCategory("");
        Resp tenantR = testRestTemplate.postForObject("/manage/tenant/", tenant, Resp.class);
        Assert.assertTrue(tenantR.ok());
        // update
        tenant = JsonHelper.toObject(tenantR.getBody(), Tenant.class);
        tenant.setName("默认租户1");
        testRestTemplate.put("/manage/tenant/code/" + tenant.getCode(), tenant);
        // get
        tenantR = testRestTemplate.getForObject("/manage/tenant/code/" + tenant.getCode(), Resp.class);
        tenant = JsonHelper.toObject(tenantR.getBody(), Tenant.class);
        Assert.assertTrue(tenantR.ok() && tenant.getName().equals("默认租户1"));
        // disable
        testRestTemplate.delete("/manage/tenant/code/" + tenant.getCode() + "/disable", Resp.class);
        tenant = JsonHelper.toObject(testRestTemplate.getForObject("/manage/tenant/code/" + tenant.getCode(), Resp.class).getBody(), Tenant.class);
        Assert.assertTrue(!tenant.getEnable());
        // enable
        testRestTemplate.put("/manage/tenant/code/" + tenant.getCode() + "/enable", "", Resp.class);
        tenant = JsonHelper.toObject(testRestTemplate.getForObject("/manage/tenant/code/" + tenant.getCode(), Resp.class).getBody(), Tenant.class);
        Assert.assertTrue(tenant.getEnable());
        // exist
        Assert.assertTrue(tenantService.existByCode(tenant.getCode()).getBody());
        tenantRepository.deleteAll();
    }

    private void testAccount() throws Exception {
        accountRepository.deleteAll();
        Resp<Account> account1R = accountService.save(Account.build("root", "", "", "123", "管理员", new HashSet<>()));
        Assert.assertTrue(account1R.ok());
        Resp<Account> account2R = accountService.save(Account.build("", "123", "", "123", "管理员", new HashSet<>()));
        Assert.assertTrue(account2R.ok());
        Resp<Account> account3R = accountService.save(Account.build("", "", "123@123.com", "123", "管理员", new HashSet<>()));
        Assert.assertTrue(account3R.ok());
        Resp<Account> account4R = accountService.save(Account.build("root1", "1234", "1234@123.com", "123", "管理员", new HashSet<>()));
        Assert.assertTrue(account4R.ok());
        Assert.assertEquals(accountService.save(Account.build("root", "", "", "123", "管理员", new HashSet<>())).getCode(), StandardCode.CONFLICT.toString());
        Assert.assertEquals(accountService.save(Account.build("", "123", "", "123", "管理员", new HashSet<>())).getCode(), StandardCode.CONFLICT.toString());
        Assert.assertEquals(accountService.save(Account.build("", "", "123@123.com", "123", "管理员", new HashSet<>())).getCode(), StandardCode.CONFLICT.toString());
        Assert.assertEquals(accountService.save(Account.build("", "", "123@123", "123", "管理员", new HashSet<>())).getCode(), StandardCode.BAD_REQUEST.toString());

        Assert.assertTrue(accountService.updateByCode(account1R.getBody().getCode(), account1R.getBody()).ok());
        account1R.getBody().setLoginId("new_root");
        Assert.assertTrue(accountService.updateByCode(account1R.getBody().getCode(), account1R.getBody()).ok());

        Assert.assertTrue(accountService.updateByCode(account2R.getBody().getCode(), account2R.getBody()).ok());
        account2R.getBody().setMobile("0123");
        Assert.assertTrue(accountService.updateByCode(account2R.getBody().getCode(), account2R.getBody()).ok());

        Assert.assertTrue(accountService.updateByCode(account3R.getBody().getCode(), account3R.getBody()).ok());
        account3R.getBody().setEmail("0123@123.com");
        Assert.assertTrue(accountService.updateByCode(account3R.getBody().getCode(), account3R.getBody()).ok());
        accountRepository.deleteAll();
    }

    private void testMange() {
        tenantRepository.deleteAll();
        resourceRepository.deleteAll();
        roleRepository.deleteAll();
        accountRepository.deleteAll();
        // add 2 tenants
        Tenant tenant1 = tenantService.save(Tenant.build("测试租户1")).getBody();
        Tenant tenant2 = tenantService.save(Tenant.build("测试租户2")).getBody();
        // add 3 resources
        Resource resource0 = resourceService.save(Resource.build("/manage/**", "*", "认证管理", "")).getBody();
        Resource resource1 = resourceService.save(Resource.build("/app1/**", "*", "APP1", tenant1.getCode())).getBody();
        Resource resource2 = resourceService.save(Resource.build("/app2/**", "*", "APP2", tenant2.getCode())).getBody();
        // add 3 roles
        Role role0 = roleService.save(Role.build("系统管理员", "", new HashSet<String>() {{
            add(resource0.getCode());
            add(resource1.getCode());
            add(resource2.getCode());
        }})).getBody();
        Role role1 = roleService.save(Role.build("普通用户", tenant1.getCode(), new HashSet<String>() {{
            add(resource1.getCode());
        }})).getBody();
        Role role2 = roleService.save(Role.build("普通用户", tenant2.getCode(), new HashSet<String>() {{
            add(resource2.getCode());
        }})).getBody();
        // add 3 accounts
        Account account0 = accountService.save(Account.build("root", "", "", "123", "管理员", new HashSet<String>() {{
            add(role0.getCode());
        }})).getBody();
        Account account1 = accountService.save(Account.build("abc", "", "", "123", "用户1", new HashSet<String>() {{
            add(role1.getCode());
        }})).getBody();
        Account account2 = accountService.save(Account.build("root", "", "", "123", "用户2", new HashSet<String>() {{
            add(role2.getCode());
        }})).getBody();

        // get role
        Role role = roleService.getByCode(role0.getCode()).getBody();
        Assert.assertEquals(role.getName(), "系统管理员");
        Assert.assertEquals(role.getResources().size(), 3);

        // update account
        accountService.addRoleCode(account0, role1.getCode());
        accountService.addRoleCode(account0, role2.getCode());
        accountService.removeRoleCode(account0, role0.getCode());
        accountService.updateByCode(account0.getCode(), account0);

        // get account
        Account account = accountService.getByCode(account0.getCode()).getBody();
        Assert.assertEquals(account.getName(), "管理员");
        Assert.assertEquals(account.getRoles().size(), 2);
        // find account
        List<Account> accounts = accountService.findEnable().getBody();
        Assert.assertEquals(accounts.size(), 2);
        Assert.assertEquals(accounts.get(0).getRoles().size(), 2);

        // delete role
        roleService.deleteById(role1.getId());
        account = accountService.getByCode(account0.getCode()).getBody();
        Assert.assertEquals(account.getRoles().size(), 1);

        // add menu resource
        Resource resourceMenu = Resource.build("tab://xx", "*", "菜单示例", "");
        resourceMenu.setCategory(Resource.CATEGORY_MENU);
        resourceService.save(resourceMenu).getBody();
        Map<String, List<Resource>> resources = resourceService.findByTenantCodeGroupByCategory(tenant2.getCode()).getBody();
        Assert.assertEquals(resources.keySet().size(), 2);
        Assert.assertEquals(resources.get(Resource.CATEGORY_DEFAULT).size(), 2);
        Assert.assertEquals(resources.get(Resource.CATEGORY_MENU).size(), 1);
        tenantRepository.deleteAll();
        resourceRepository.deleteAll();
        roleRepository.deleteAll();
        accountRepository.deleteAll();
    }

    private void testAuth() throws Exception {
        tenantRepository.deleteAll();
        resourceRepository.deleteAll();
        roleRepository.deleteAll();
        accountRepository.deleteAll();
        Tenant tenant0 = tenantService.save(Tenant.build("测试租户0")).getBody();
        Resource resource0 = resourceService.save(Resource.build("/manage/**", "*", "认证管理", "")).getBody();
        Role role0 = roleService.save(Role.build("系统管理员", "", new HashSet<String>() {{
            add(resource0.getCode());
        }})).getBody();
        Account account0 = accountService.save(Account.build("root", "123", "123@123.com", "123", "管理员", new HashSet<String>() {{
            add(role0.getCode());
        }})).getBody();
        // login
        LoginReq loginReq = new LoginReq();
        loginReq.setLoginId("root");
        loginReq.setPassword("123");
        Resp optInfoR = testRestTemplate.postForObject("/public/login", loginReq, Resp.class);
        Assert.assertTrue(optInfoR.ok());
        OptInfo optInfo = JsonHelper.toObject(optInfoR.getBody(), OptInfo.class);
        Assert.assertEquals(optInfo.getLoginId(), "root");
        // get login info
        optInfoR = testRestTemplate.getForObject("/logininfo?__dew_token__=" + optInfo.getToken(), Resp.class);
        Assert.assertTrue(optInfoR.ok());
        optInfo = JsonHelper.toObject(optInfoR.getBody(), OptInfo.class);
        Assert.assertEquals(optInfo.getLoginId(), "root");
        // update login info
        ModifyLoginInfoReq modifyLoginInfoReq = new ModifyLoginInfoReq();
        modifyLoginInfoReq.setEmail("123456@123.com");
        modifyLoginInfoReq.setOldPassword("123");
        testRestTemplate.put("/account/bylogin?__dew_token__=" + optInfo.getToken(), modifyLoginInfoReq);
        optInfoR = testRestTemplate.getForObject("/logininfo?__dew_token__=" + optInfo.getToken(), Resp.class);
        optInfo = JsonHelper.toObject(optInfoR.getBody(), OptInfo.class);
        Assert.assertEquals(optInfo.getEmail(), "123456@123.com");
        modifyLoginInfoReq.setNewPassword("1234");
        testRestTemplate.put("/account/bylogin?__dew_token__=" + optInfo.getToken(), modifyLoginInfoReq);
        optInfoR = testRestTemplate.getForObject("/logininfo?__dew_token__=" + optInfo.getToken(), Resp.class);
        Assert.assertFalse(optInfoR.ok());
        // login error (captcha)
        loginReq.setEmail("123456@123.com");
        loginReq.setPassword("123");
        optInfoR = testRestTemplate.postForObject("/public/login", loginReq, Resp.class);
        Assert.assertFalse(optInfoR.ok());
        optInfoR = testRestTemplate.postForObject("/public/login", loginReq, Resp.class);
        Assert.assertFalse(optInfoR.ok());
        optInfoR = testRestTemplate.postForObject("/public/login", loginReq, Resp.class);
        Assert.assertFalse(optInfoR.ok());
        optInfoR = testRestTemplate.postForObject("/public/login", loginReq, Resp.class);
        Assert.assertFalse(optInfoR.ok());
        loginReq.setPassword("1234");
        optInfoR = testRestTemplate.postForObject("/public/login", loginReq, Resp.class);
        Assert.assertFalse(optInfoR.ok());
        loginReq.setCaptcha(cacheManager.getLogin().getCaptchaText(authService.packageTryLoginInfo(loginReq)));
        optInfoR = testRestTemplate.postForObject("/public/login", loginReq, Resp.class);
        optInfo = JsonHelper.toObject(optInfoR.getBody(), OptInfo.class);
        Assert.assertTrue(optInfoR.ok());
        // logout
        testRestTemplate.delete("/logout?__dew_token__=" + optInfo.getToken());
    }
}
