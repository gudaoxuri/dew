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

package com.tairanchina.csp.dew.auth.service

import com.ecfront.dew.common.{$, Resp}
import com.tairanchina.csp.dew.Dew
import com.tairanchina.csp.dew.auth.AuthConfig
import com.tairanchina.csp.dew.auth.dto.basic.{AccessTokenReq, AccessTokenResp}
import com.tairanchina.csp.dew.auth.repository._
import com.tairanchina.csp.dew.auth.sdk.TokenInfo
import com.typesafe.scalalogging.LazyLogging
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.util.AntPathMatcher

import scala.collection.JavaConverters._

@Service
class BasicService @Autowired()(
                                 val tenantRepository: TenantRepository,
                                 val resourceRepository: ResourceRepository,
                                 val roleRepository: RoleRepository,
                                 val identRepository: IdentRepository,
                                 val accountRepository: AccountRepository,
                                 val authConfig: AuthConfig
                               ) extends LazyLogging {

  private val pathMatcher = new AntPathMatcher

  @Transactional
  def getAccessToken(req: AccessTokenReq): Resp[AccessTokenResp] = {
    val tenant = tenantRepository.findById(req.getAppId).get()
    if (tenant != null && tenant.enabled && tenant.secret == req.secret) {
      val resp = new AccessTokenResp()
      resp.serverCurrentTimestamp = System.currentTimeMillis()
      resp.expireTimestamp = resp.serverCurrentTimestamp + tenant.effectiveTimeMS
      resp.accessToken = $.field.createUUID()
      tenantRepository.updateAccessToken(tenant.id, resp.accessToken, resp.expireTimestamp)
      Dew.cluster.mq.publish(AuthConfig.MQ_PUB_ACCESS_TOKEN, tenant.id)
      Resp.success(resp)
    } else {
      logger.error("[{}] Auth Error", req.appId)
      Resp.unAuthorized("AccessToken 获取失败")
    }
  }

  @Transactional(readOnly = true)
  def cacheAccessToken(tenantId: String): Unit = {
    val accessToken = tenantRepository.findById(tenantId).get().accessToken
    val oldTenantOpt = AuthConfig.CACHE.tenants.find(_._2 == tenantId)
    if (oldTenantOpt.isDefined) {
      AuthConfig.CACHE.tenants -= oldTenantOpt.get._1
    }
    AuthConfig.CACHE.tenants += accessToken -> tenantId
  }

  @Transactional(readOnly = true)
  def cacheAll(): Unit = {
    AuthConfig.CACHE.tenants = collection.mutable.Map(
      tenantRepository.findByEnabled().asScala
        .filter(_.accessToken.nonEmpty)
        .map(tenant => tenant.accessToken -> tenant.id).toSeq: _*)
    val resources = roleRepository.findByEnabled().asScala
      .flatMap(role => role.resources.asScala.map(res => (res.tenantId, res.uri, role.id)))
      .groupBy(_._1)
      .map(res => res._1 -> res._2.groupBy(_._2).map(r => r._1 -> r._2.map(_._3).toSet))
    AuthConfig.CACHE.resources = resources
  }

  @Transactional(readOnly = true)
  def validate(uri: String, accessToken: String, token: String): Resp[TokenInfo] = {
    val tenantIdOpt = AuthConfig.CACHE.tenants.get(accessToken)
    if (tenantIdOpt.isEmpty) {
      return Resp.unAuthorized(s"AccessToken [$accessToken] 不合法")
    }
    var tokenInfo: TokenInfo = new TokenInfo
    if (token != null && token.nonEmpty) {
      val tokenInfoStr = Dew.cluster.cache.get(AuthConfig.CACHE_TOKEN + token)
      if (tokenInfoStr == null || tokenInfoStr.isEmpty) {
        return Resp.unAuthorized(s"Token [$token] 不合法")
      }
      tokenInfo = $.json.toObject(tokenInfoStr, classOf[TokenInfo])
    }
    tokenInfo.setTenantId(tenantIdOpt.get)
    val matchedRoles =
      AuthConfig.CACHE.resources.getOrElse(tenantIdOpt.get, Map[String, Set[String]]())
        .filter(res => pathMatcher.`match`(res._1, uri))
        .flatMap(res => res._2)
        .toSet ++ AuthConfig.CACHE.resources.getOrElse("", Map[String, Set[String]]())
        .filter(res => pathMatcher.`match`(res._1, uri))
        .flatMap(res => res._2)
        .toSet
    if (matchedRoles.nonEmpty) {
      if (tokenInfo.getAccountCode != null
        && tokenInfo.getRoles != null
        && tokenInfo.getRoles.keySet().stream().anyMatch(role => matchedRoles.contains(role))) {
        Resp.success(tokenInfo)
      } else {
        Resp.unAuthorized(s"Uri [$uri] Account [${tokenInfo.getTenantId} - ${tokenInfo.getName}] 没有权限")
      }
    } else {
      Resp.success(tokenInfo)
    }
  }
}
