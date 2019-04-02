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

package ms.dew.auth

import ms.dew.Dew
import ms.dew.auth.domain.{Ident, Resource}
import ms.dew.auth.dto.basic.{AccessTokenReq, AccessTokenResp}
import ms.dew.auth.dto.management._
import ms.dew.auth.dto.user.{LoginReq, RegisterReq, UserModifyReq}
import ms.dew.auth.sdk.TokenInfo
import ms.dew.auth.service.ManagementService
import ms.dew.test.DewTestAutoConfiguration
import org.junit.runner.RunWith
import org.junit.{Before, Test}
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.transaction.annotation.Transactional

import scala.collection.JavaConverters._

@RunWith(classOf[SpringRunner])
@SpringBootTest(classes = Array(classOf[DewTestAutoConfiguration], classOf[AuthApplication]), webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Transactional
class AuthTest extends BasicTest {

  @Autowired
  var managementService: ManagementService = _

  /*  @Test
    def startup(): Unit = {
      new CountDownLatch(1).await()
    }*/

  @Before
  def init(): Unit = {
    val accessTokenReq = new AccessTokenReq
    accessTokenReq.appId = "SYSTEM"
    accessTokenReq.secret = managementService.getTenant("SYSTEM").getBody.secret
    val accessTokenResp = post("/basic/access-token", accessTokenReq, classOf[AccessTokenResp])
    accessToken = accessTokenResp.getBody.accessToken
    logger.info("Access Token:" + accessToken)
  }

  @Test
  def testManagement(): Unit = {
    val loginReq = new LoginReq
    loginReq.byVC = false
    loginReq.identCategory = Ident.IDENT_CATEGORY_USERNAME
    loginReq.identKey = "admin"
    loginReq.identSecret = "admin"
    val loginR = post("/user/login", loginReq, classOf[TokenInfo])
    token = loginR.getBody.getToken
    assert(loginR.ok())

    val tenantId = testTenant()
    val resourceId = testResource(tenantId)
    val roleId = testRole(tenantId, resourceId)
    val accountId = testAccount(tenantId, resourceId, roleId)
    val identId = testIdent(tenantId, resourceId, roleId, accountId)

    get("/management/publish", classOf[Void])

    testAuth(tenantId)

    testRegister()
  }

  def testTenant(): String = {
    // Add
    val addReq = new TenantAddReq()
    addReq.name = "默认"
    val addR = post("/management/tenant", addReq, classOf[TenantResp])
    assert(addR.getBody.name == "默认"
      && addR.getBody.createTime == addR.getBody.getUpdateTime
      && addR.getBody.enabled)
    // Modify
    val modifyReq = new TenantModifyReq()
    modifyReq.name = "默认租户"
    put("/management/tenant/" + addR.getBody.getId, modifyReq, classOf[Void])
    // Get
    val getR = get("/management/tenant/" + addR.getBody.getId, classOf[TenantResp])
    assert(getR.getBody.name == "默认租户"
      && getR.getBody.createTime != getR.getBody.getUpdateTime
      && getR.getBody.enabled)
    // Page
    val pageR = getPage("/management/tenants/0", classOf[TenantResp])
    assert(pageR.getBody.getPageTotal == 1
      && pageR.getBody.getPageNumber == 0
      && pageR.getBody.getObjects.get(1).name == "默认租户")
    // Disable
    delete("/management/tenant/" + addR.getBody.getId)
    assert(!get("/management/tenant/" + addR.getBody.getId, classOf[TenantResp]).getBody.enabled)
    // Enable
    put("/management/tenant/" + addR.getBody.getId + "/enable", null, classOf[Void])
    assert(get("/management/tenant/" + addR.getBody.getId, classOf[TenantResp]).getBody.enabled)
    getR.getBody.id
  }

  def testResource(tenantId: String): Int = {
    // Add
    val addReq = new ResourceAddReq()
    addReq.uri = "GET@/" + Dew.Info.name + "/test/auth/**"
    addReq.name = "测试权限"
    addReq.category = Resource.CATEGORY_API
    val addR = post("/management/resource", addReq, classOf[ResourceResp])
    assert(addR.getBody.name == "测试权限")
    // Modify
    val modifyReq = new ResourceModifyReq()
    modifyReq.uri = "GET@/" + Dew.Info.name + "/test/auth/**"
    modifyReq.name = "权限测试"
    modifyReq.category = Resource.CATEGORY_API
    put("/management/resource/" + addR.getBody.getId, modifyReq, classOf[Void])
    // Get
    val getR = get("/management/resource/" + addR.getBody.getId, classOf[ResourceResp])
    assert(getR.getBody.name == "权限测试")
    // Page
    val pageR = getPage("/management/resources/0", classOf[ResourceResp])
    assert(pageR.getBody.getPageTotal == 1 && pageR.getBody.getPageNumber == 0)
    getR.getBody.id
  }

  def testRole(tenantId: String, resourceId: Int): String = {
    // Add
    val addReq = new RoleAddReq()
    addReq.name = "普通用户"
    addReq.resourceIds = Set(resourceId).asJava
    val addR = post("/management/role", addReq, classOf[RoleResp])
    assert(addR.getBody.name == "普通用户")
    // Modify
    var modifyReq = new RoleModifyReq()
    modifyReq.name = "普通用户"
    put("/management/role/" + addR.getBody.getId, modifyReq, classOf[Void])
    // Get
    var getR = get("/management/role/" + addR.getBody.getId, classOf[RoleResp])
    assert(getR.getBody.name == "普通用户" && getR.getBody.resourceIds.isEmpty)
    modifyReq = new RoleModifyReq()
    modifyReq.name = "普通用户"
    modifyReq.resourceIds = Set(resourceId).asJava
    put("/management/role/" + addR.getBody.getId, modifyReq, classOf[Void])
    getR = get("/management/role/" + addR.getBody.getId, classOf[RoleResp])
    assert(getR.getBody.name == "普通用户" && getR.getBody.resourceIds.asScala.nonEmpty)
    // Page
    val pageR = getPage("/management/roles/0", classOf[RoleResp])
    assert(pageR.getBody.getPageTotal == 1 && pageR.getBody.getPageNumber == 0)
    // Disable
    delete("/management/role/" + addR.getBody.getId)
    assert(!get("/management/role/" + addR.getBody.getId, classOf[RoleResp]).getBody.enabled)
    // Enable
    put("/management/role/" + addR.getBody.getId + "/enable", null, classOf[Void])
    assert(get("/management/role/" + addR.getBody.getId, classOf[RoleResp]).getBody.enabled)
    getR.getBody.id
  }

  def testAccount(tenantId: String, resourceId: Int, roleId: String): String = {
    // Add
    val addReq = new AccountAddReq()
    addReq.name = "蒋震宇"
    addReq.password = "123"
    addReq.tenantId = tenantId
    val addR = post("/management/account", addReq, classOf[AccountResp])
    assert(addR.getBody.name == "蒋震宇")
    var getR = get("/management/account/" + addR.getBody.getId, classOf[AccountResp])
    assert(getR.getBody.name == "蒋震宇" && getR.getBody.roleIds.isEmpty)
    // Modify
    val modifyReq = new AccountModifyReq()
    modifyReq.name = "蒋震宇"
    modifyReq.roleIds = Set(roleId).asJava
    modifyReq.tenantId = tenantId
    put("/management/account/" + addR.getBody.getId, modifyReq, classOf[Void])
    getR = get("/management/account/" + addR.getBody.getId, classOf[AccountResp])
    assert(getR.getBody.name == "蒋震宇" && getR.getBody.roleIds.size == 1)
    // Page
    val pageR = getPage("/management/accounts/0", classOf[AccountResp])
    assert(pageR.getBody.getPageTotal == 1 && pageR.getBody.getPageNumber == 0)
    // Disable
    delete("/management/account/" + addR.getBody.getId)
    assert(!get("/management/account/" + addR.getBody.getId, classOf[AccountResp]).getBody.enabled)
    // Enable
    put("/management/account/" + addR.getBody.getId + "/enable", null, classOf[Void])
    assert(get("/management/account/" + addR.getBody.getId, classOf[AccountResp]).getBody.enabled)
    getR.getBody.id
  }

  def testIdent(tenantId: String, resourceId: Int, roleId: String, accountId: String): Int = {
    // Add
    val addReq = new IdentAddReq()
    addReq.category = Ident.IDENT_CATEGORY_USERNAME
    addReq.key = "gudaoxuri"
    addReq.accountId = accountId
    addReq.tenantId = tenantId
    val addR = post("/management/ident", addReq, classOf[IdentResp])
    assert(addR.getBody.key == "gudaoxuri")
    // Modify
    val modifyReq = new IdentModifyReq()
    modifyReq.category = Ident.IDENT_CATEGORY_USERNAME
    modifyReq.key = "gdxr"
    modifyReq.accountId = accountId
    modifyReq.tenantId = tenantId
    put("/management/ident/" + addR.getBody.getId, modifyReq, classOf[Void])
    val getR = get("/management/ident/" + addR.getBody.getId, classOf[IdentResp])
    assert(getR.getBody.key == "gdxr")
    // Page
    val pageR = getPage("/management/idents/0", classOf[IdentResp])
    assert(pageR.getBody.getPageTotal == 1 && pageR.getBody.getPageNumber == 0)
    // Disable
    delete("/management/ident/" + addR.getBody.getId)
    assert(!get("/management/ident/" + addR.getBody.getId, classOf[IdentResp]).getBody.enabled)
    // Enable
    put("/management/ident/" + addR.getBody.getId + "/enable", null, classOf[Void])
    assert(get("/management/ident/" + addR.getBody.getId, classOf[IdentResp]).getBody.enabled)
    getR.getBody.id
  }

  def testAuth(tenantId: String): Unit = {
    // Change Tenant
    val accessTokenReq = new AccessTokenReq
    accessTokenReq.appId = tenantId
    accessTokenReq.secret = managementService.getTenant(tenantId).getBody.secret
    val accessTokenResp = post("/basic/access-token", accessTokenReq, classOf[AccessTokenResp])
    accessToken = accessTokenResp.getBody.accessToken

    Thread.sleep(500)
    var getR = get("/test", classOf[Void])
    assert(getR.ok())
    getR = get("/test/auth/t", classOf[Void])
    assert(!getR.ok())
    // login
    val loginReq = new LoginReq
    loginReq.byVC = false
    loginReq.identCategory = Ident.IDENT_CATEGORY_USERNAME
    loginReq.identKey = "gdxr"
    loginReq.identSecret = "12"
    var loginR = post("/user/login", loginReq, classOf[TokenInfo])
    assert(!loginR.ok())
    loginReq.identSecret = "123"
    loginR = post("/user/login", loginReq, classOf[TokenInfo])
    token = loginR.getBody.getToken
    assert(loginR.ok())
    getR = get("/test", classOf[Void])
    assert(getR.ok())
    getR = get("/test/auth/t", classOf[Void])
    assert(getR.ok())
    // get user
    val infoR = get("/user/info", classOf[TokenInfo])
    assert(infoR.getBody.getName == "蒋震宇" && infoR.getBody.getRoles.containsValue("普通用户"))
    // modify user
    val user = new UserModifyReq
    user.name = "蒋震宇"
    user.password = "456"
    put("/user/info", user, classOf[Void])
    getR = get("/test/auth/t", classOf[Void])
    assert(!getR.ok())
    token = ""
    // logout
    loginReq.identSecret = "456"
    loginR = post("/user/login", loginReq, classOf[TokenInfo])
    assert(loginR.ok())
    token = loginR.getBody.getToken
    delete("/user/logout")
    getR = get("/test/auth/t", classOf[Void])
    assert(!getR.ok())
  }

  def testRegister(): Unit = {
    token = ""

    val register = new RegisterReq
    register.name = "注册用户1"
    register.identCategory = Ident.IDENT_CATEGORY_USERNAME
    register.identKey = "regUser1"
    register.identSecret = "qwe"
    val tokenInfoR = post("/user/register", register, classOf[TokenInfo])
    assert(tokenInfoR.ok())
    token = tokenInfoR.getBody.getToken

    // modify user
    val user = new UserModifyReq
    user.name = "注册用户A"
    user.password = "qwe"
    put("/user/info", user, classOf[Void])
    token = ""

    // login
    val loginReq = new LoginReq
    loginReq.byVC = false
    loginReq.identCategory = Ident.IDENT_CATEGORY_USERNAME
    loginReq.identKey = "regUser1"
    loginReq.identSecret = "qwe"
    val loginR = post("/user/login", loginReq, classOf[TokenInfo])
    assert(loginR.ok())
    token = loginR.getBody.getToken

    // getUser
    var infoR = get("/user/info", classOf[TokenInfo])
    assert(infoR.getBody.getName == "注册用户A" && infoR.getBody.getRoles.isEmpty)

    // logout
    delete("/user/logout")
    infoR = get("/user/info", classOf[TokenInfo])
    assert(!infoR.ok())

  }
}
