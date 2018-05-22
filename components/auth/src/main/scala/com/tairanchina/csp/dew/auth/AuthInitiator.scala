package com.tairanchina.csp.dew.auth

import javax.annotation.PostConstruct

import com.ecfront.dew.common.$
import com.tairanchina.csp.dew.Dew
import com.tairanchina.csp.dew.auth.domain._
import com.tairanchina.csp.dew.auth.dto.management._
import com.tairanchina.csp.dew.auth.repository.{RoleRepository, TenantRepository}
import com.tairanchina.csp.dew.auth.service.{BasicService, ManagementService}
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
