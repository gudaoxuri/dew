package com.ecfront.dew.auth.controller;

import com.ecfront.dew.auth.dto.LoginReq;
import com.ecfront.dew.auth.dto.ModifyLoginInfoReq;
import com.ecfront.dew.auth.entity.Account;
import com.ecfront.dew.auth.service.AuthService;
import com.ecfront.dew.common.Resp;
import com.ecfront.dew.core.dto.OptInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;

@RestController
@Api(value = "权限认证", description = "登录、注销、获取登录信息等操作")
@RequestMapping(value = "")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping(value = "/public/auth/login")
    @ApiOperation(value = "登录")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "vo", value = "登录请求VO", paramType = "body", dataType = "LoginReq", required = true),
    })
    public Resp<OptInfo> save(@RequestBody LoginReq vo) throws IOException, NoSuchAlgorithmException {
        return authService.login(vo);
    }

    @PostMapping(value = "/public/auth/captcha")
    @ApiOperation(value = "获取图片验证码", notes = "返回验证码的Base64编码")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "vo", value = "登录请求VO", paramType = "body", dataType = "LoginReq", required = true),
    })
    public String getCaptcha(@RequestBody LoginReq vo) throws IOException {
        return authService.getCaptcha(vo).getBody();
    }

    @DeleteMapping(value = "/auth/logout")
    @ApiOperation(value = "注销")
    public Resp<Void> logout() {
        return authService.logout();
    }

    @GetMapping(value = "/auth/logininfo")
    @ApiOperation(value = "获取当前登录信息")
    public Resp<OptInfo> getLoginInfo() {
        return authService.getLoginInfo();
    }

    @PutMapping(value = "/auth/account/bylogin")
    @ApiOperation(value = "更新登录账号的信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "vo", value = "登录请求VO", paramType = "body", dataType = "ModifyLoginInfoReq", required = true),
    })
    public Resp<Account> updateAccountByLoginInfo(@RequestBody ModifyLoginInfoReq vo) throws NoSuchAlgorithmException {
        return authService.updateAccountByLoginInfo(vo);
    }

}
