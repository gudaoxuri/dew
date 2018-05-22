package com.tairanchina.csp.dew.auth.controller

import com.ecfront.dew.common.Resp
import com.tairanchina.csp.dew.auth.dto.user._
import com.tairanchina.csp.dew.auth.sdk.TokenInfo
import com.tairanchina.csp.dew.auth.service.UserService
import io.swagger.annotations.Api
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation._

@RestController
@Api(value = "用户操作")
@RequestMapping(value = Array("/user"))
@Validated
class UserController @Autowired()(
                                   val userService: UserService
                                 ) {

  @PostMapping(Array("register/vc"))
  def sendRegisterVC(@Validated @RequestBody req: SendVCReq): Resp[Void] = {
    userService.sendRegisterVC(req)
  }

  @PostMapping(Array("register"))
  def register(@Validated @RequestBody req: RegisterReq): Resp[TokenInfo] = {
    userService.register(req)
  }

  @PostMapping(Array("login/vc"))
  def sendLoginVC(@Validated @RequestBody req: SendVCReq): Resp[Void] = {
    userService.sendLoginVC(req)
  }

  @PostMapping(Array("/login"))
  def login(@Validated @RequestBody req: LoginReq): Resp[TokenInfo] = {
    userService.login(req)
  }

  @DeleteMapping(Array("/logout"))
  def logout(): Resp[Void] = {
    userService.logout()
  }

  @GetMapping(Array("/info"))
  def getCurrentUser: Resp[UserResp] = {
    userService.getCurrentUser
  }

  @PutMapping(Array("/info"))
  def modifyCurrentUser(@Validated @RequestBody req: UserModifyReq): Resp[Void] = {
    userService.modifyCurrentUser(req)
  }

  // TODO 找回密码

}
