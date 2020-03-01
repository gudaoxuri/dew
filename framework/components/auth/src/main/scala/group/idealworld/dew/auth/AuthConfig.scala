/*
 * Copyright 2020. the original author or authors.
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

package group.idealworld.dew.auth

import group.idealworld.dew.auth.dto.basic.AuthCacheData
import group.idealworld.dew.auth.dto.basic.AuthCacheData
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
