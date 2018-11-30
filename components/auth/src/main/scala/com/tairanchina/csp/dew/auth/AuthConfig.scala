package com.tairanchina.csp.dew.auth

import com.tairanchina.csp.dew.auth.dto.basic.AuthCacheData
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

import scala.beans.BeanProperty

@Component
@ConfigurationProperties(prefix = "dew.component.auth")
class AuthConfig {

  @BeanProperty
  var tokenExpireSec: Int = 60 * 60 * 24 * 30
  @BeanProperty
  var maxLoginErrorTimes: Int = 3
  @BeanProperty
  var cleanErrorTimeMin: Int = 10
  @BeanProperty
  var passwordSalt: String = "u39$#sFd"
  @BeanProperty
  var emailAccount: String = _

}

object AuthConfig {

  val MQ_PUB_ACCESS_TOKEN = "dew:auth:pub:access-token"
  val MQ_PUB_ALL_CACHE = "dew:auth:pub:all-cache"
  val CACHE_TOKEN = "dew:auth:login:token:"
  val CACHE_LOGIN_ERROR = "dew:auth:login:error:"
  val CACHE_LOGIN_VC = "dew:auth:login:vc:"
  val CACHE_REG_VC_EMAIL = "dew:auth:reg:vc:email:"

  val CACHE: AuthCacheData = new AuthCacheData

}