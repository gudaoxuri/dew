package com.tairanchina.csp.dew.auth.interceptor

import javax.security.auth.message.AuthException
import javax.servlet.http.{HttpServletRequest, HttpServletResponse}

import com.tairanchina.csp.dew.Dew
import com.tairanchina.csp.dew.auth.sdk.AuthSDKConfig
import com.tairanchina.csp.dew.auth.service.BasicService
import com.tairanchina.csp.dew.core.web.error.ErrorController
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter

@Component
class AuthHandlerInterceptor extends HandlerInterceptorAdapter {

  @Autowired
  var authSdkConfig: AuthSDKConfig = _
  @Autowired
  var basicService: BasicService = _

  @throws[Exception]
  override def preHandle(request: HttpServletRequest, response: HttpServletResponse, handler: Any): Boolean = {
    val uri = request.getMethod + "@" + "/" + Dew.Info.name + request.getRequestURI
    if (authSdkConfig.getWhiteList.stream.anyMatch(uri.startsWith(_))) {
      return super.preHandle(request, response, handler)
    }
    val accessToken = request.getHeader(AuthSDKConfig.HTTP_ACCESS_TOKEN)
    if (accessToken == null || accessToken.isEmpty) {
      ErrorController.error(request, response, 401, "缺少AccessToken", classOf[AuthException].getName)
      return false
    }
    val token = request.getHeader(AuthSDKConfig.HTTP_USER_TOKEN)
    val tokenInfoR = basicService.validate(uri, accessToken, if (token == null) "" else token)
    if (tokenInfoR.ok) {
      Dew.auth.setOptInfo(tokenInfoR.getBody)
      super.preHandle(request, response, handler)
    } else {
      ErrorController.error(request, response, 401, tokenInfoR.getMessage, classOf[AuthException].getName)
      false
    }
  }
}