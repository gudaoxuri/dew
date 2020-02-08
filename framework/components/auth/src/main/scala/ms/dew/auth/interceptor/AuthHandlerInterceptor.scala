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

package ms.dew.auth.interceptor

import com.typesafe.scalalogging.LazyLogging
import javax.security.auth.message.AuthException
import javax.servlet.http.{HttpServletRequest, HttpServletResponse}
import ms.dew.Dew
import ms.dew.auth.sdk.AuthSDKConfig
import ms.dew.auth.service.BasicService
import ms.dew.core.web.error.ErrorController
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter

@Component
class AuthHandlerInterceptor extends HandlerInterceptorAdapter with LazyLogging {

  @Autowired
  var authSdkConfig: AuthSDKConfig = _
  @Autowired
  var basicService: BasicService = _

  @throws[Exception]
  override def preHandle(request: HttpServletRequest, response: HttpServletResponse, handler: Any): Boolean = {
    val uri = request.getMethod + "@" + "/" + Dew.Info.name + request.getRequestURI
    logger.trace("Auth by " + uri)
    if (authSdkConfig.getWhiteList.stream.anyMatch(uri.startsWith(_))) {
      return super.preHandle(request, response, handler)
    }
    val accessToken = request.getHeader(AuthSDKConfig.HTTP_ACCESS_TOKEN)
    if (accessToken == null || accessToken.isEmpty) {
      logger.error("Missing " + AuthSDKConfig.HTTP_ACCESS_TOKEN + " in Header")
      ErrorController.error(request, response, 401, "缺少AccessToken", classOf[AuthException].getName)
      return false
    }
    val token = request.getHeader(AuthSDKConfig.HTTP_USER_TOKEN)
    val tokenInfoR = basicService.validate(uri, accessToken, if (token == null) "" else token)
    if (tokenInfoR.ok) {
      Dew.auth.setOptInfo(tokenInfoR.getBody)
      super.preHandle(request, response, handler)
    } else {
      logger.error("Token " + token + " invalid")
      ErrorController.error(request, response, 401, tokenInfoR.getMessage, classOf[AuthException].getName)
      false
    }
  }
}
