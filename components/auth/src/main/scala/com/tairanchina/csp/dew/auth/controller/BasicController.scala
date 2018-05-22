package com.tairanchina.csp.dew.auth.controller

import com.ecfront.dew.common.Resp
import com.tairanchina.csp.dew.auth.dto.basic.{AccessTokenReq, AccessTokenResp}
import com.tairanchina.csp.dew.auth.sdk.{AuthSDKConfig, TokenInfo}
import com.tairanchina.csp.dew.auth.service.BasicService
import io.swagger.annotations.Api
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation._

@RestController
@Api(value = "基础操作")
@RequestMapping(value = Array("/basic"))
@Validated
class BasicController @Autowired()(
                                    val basicService: BasicService
                                  ) {

  @PostMapping(Array("/access-token"))
  def getAccessToken(@Validated @RequestBody req: AccessTokenReq): Resp[AccessTokenResp] = {
    basicService.getAccessToken(req)
  }

  @GetMapping(Array("/auth/validate"))
  def validate(@RequestHeader(AuthSDKConfig.HTTP_URI) uri: String,
               @RequestHeader(AuthSDKConfig.HTTP_ACCESS_TOKEN) accessToken: String,
               @RequestHeader(value = AuthSDKConfig.HTTP_USER_TOKEN, required = false) token: String): Resp[TokenInfo] = {
    basicService.validate(uri, accessToken, token)
  }

}
