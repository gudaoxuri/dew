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
