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

import com.ecfront.dew.common.$
import javax.annotation.PostConstruct
import ms.dew.Dew
import ms.dew.auth.domain.{Ident, Resource, Role, Tenant}
import ms.dew.auth.dto.management.{AccountAddReq, IdentAddReq, ResourceAddReq}
import ms.dew.auth.repository.{RoleRepository, TenantRepository}
import ms.dew.auth.service.{BasicService, ManagementService}
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

import scala.collection.JavaConverters._

@Component
class AuthInitiator @Autowired()(
                                  val basicService: BasicService,
                                  val tenantRepository: TenantRepository,
                                  val roleRepository: RoleRepository,
                                  val managementService: ManagementService
                                ) {

  @PostConstruct
  @Transactional
  def init(): Unit = {
    initData()
    basicService.cacheAll()
    Dew.cluster.mq.subscribe(AuthConfig.MQ_PUB_ACCESS_TOKEN, tenantId => {
      basicService.cacheAccessToken(tenantId)
    })
    Dew.cluster.mq.subscribe(AuthConfig.MQ_PUB_ALL_CACHE, _ => {
      basicService.cacheAll()
    })
    Dew.Timer.periodic(60, 60 * 60, () => {
      tenantRepository.findAll().asScala
        .filter(tenant =>
          tenant.accessTokenExpireMS != 0 && tenant.accessTokenExpireMS <= System.currentTimeMillis())
        .foreach(tenant => {
          tenantRepository.updateAccessToken(tenant.id, "", 0)
          AuthConfig.CACHE.tenants -= tenant.accessToken
        })
    })
  }

  private def initData(): Unit = {
    if (tenantRepository.findAll().isEmpty) {
      // 添加 系统管理 租户
      val tenant = new Tenant
      tenant.id = "SYSTEM"
      tenant.name = "系统管理"
      tenant.secret = $.security.digest.digest($.field.createUUID(), "MD5")
      tenant.accessToken = ""
      tenant.enabled = true
      tenantRepository.save(tenant)
      // 添加 系统管理 租户对应的 资源
      var res = new ResourceAddReq
      res.uri = "GET@/" + Dew.Info.name + "/management/**"
      res.name = "权限管理"
      res.category = Resource.CATEGORY_API
      res.tenantId = tenant.id
      val resId1 = managementService.addResource(res).getBody.id
      res = new ResourceAddReq
      res.uri = "POST@/" + Dew.Info.name + "/management/**"
      res.name = "权限管理"
      res.category = Resource.CATEGORY_API
      res.tenantId = tenant.id
      val resId2 = managementService.addResource(res).getBody.id
      res = new ResourceAddReq
      res.uri = "PUT@/" + Dew.Info.name + "/management/**"
      res.name = "权限管理"
      res.category = Resource.CATEGORY_API
      res.tenantId = tenant.id
      val resId3 = managementService.addResource(res).getBody.id
      res = new ResourceAddReq
      res.uri = "DELETE@/" + Dew.Info.name + "/management/**"
      res.name = "权限管理"
      res.category = Resource.CATEGORY_API
      res.tenantId = tenant.id
      val resId4 = managementService.addResource(res).getBody.id
      // 添加 系统管理员 角色
      val role = new Role
      role.id = "SYS_ADMIN"
      role.name = "系统管理员"
      role.tenantId = tenant.id
      role.resources = Set(Resource(resId1), Resource(resId2), Resource(resId3), Resource(resId4)).asJava
      roleRepository.save(role)
      // 添加 系统管理员 账号
      val account = new AccountAddReq
      account.name = "系统管理员"
      account.password = "admin"
      account.roleIds = Set(role.id).asJava
      account.tenantId = tenant.id
      val accountId = managementService.addAccount(account).getBody.id
      // 添加 凭证
      val ident = new IdentAddReq
      ident.category = Ident.IDENT_CATEGORY_USERNAME
      ident.key = "admin"
      ident.accountId = accountId
      ident.tenantId = tenant.id
      managementService.addIdent(ident)
    }
  }

}
