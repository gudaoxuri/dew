package com.ecfront.dew.core.test.web.controller;

import com.ecfront.dew.common.Resp;
import com.ecfront.dew.common.StandardCode;
import com.ecfront.dew.core.Dew;
import com.ecfront.dew.core.test.web.AuthException;
import com.ecfront.dew.core.validation.CreateGroup;
import com.ecfront.dew.core.validation.IdNumber;
import com.ecfront.dew.core.validation.Phone;
import com.ecfront.dew.core.validation.UpdateGroup;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.io.IOException;

@RestController
@Api(value = "测试", description = "test")
@RequestMapping(value = "/test/")
@Validated
public class TestController {

    @GetMapping(value = "t")
    @ApiOperation(value = "fun1")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "q", value = "query", paramType = "query", dataType = "string", required = true),
    })
    public Resp<String> t1(@RequestParam String q) {
        return Resp.success("successful");
    }

    @GetMapping(value = "t2")
    @ApiOperation(value = "fun2")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "q", value = "query", paramType = "query", dataType = "string", required = true),
    })
    public Resp<String> t2(@RequestParam String q) {
        return Resp.badRequest("badrequest");
    }

    @GetMapping(value = "t3")
    @ApiOperation(value = "fun3")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "q", value = "query", paramType = "query", dataType = "string", required = true),
    })
    public String t3(@RequestParam String q) throws Exception {
        throw Dew.E.e("A000", new Exception("io error"));
    }

    @GetMapping(value = "t4")
    @ApiOperation(value = "fun4")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "q", value = "query", paramType = "query", dataType = "string", required = true),
    })
    public String t4(@RequestParam String q) throws IOException {
        throw Dew.E.e("A000", new IOException("io error"), StandardCode.UNAUTHORIZED);
    }


    @PostMapping(value = "valid-create")
    public String validCreate(@Validated(CreateGroup.class) @RequestBody User user) {
        return "";
    }

    @PutMapping(value = "valid-update")
    public String validUpdate(@Validated(UpdateGroup.class) User user) {
        return "";
    }

    @GetMapping(value = "valid-method/{age}")
    public String validInMethod(@Min(value = 2, message = "age必须大于2") @PathVariable("age") int age) {
        return "";
    }

    @GetMapping(value = "error-mapping")
    public String errorMapping() {
        throw new AuthException("400", "auth error");
    }

    public static class User {

        @NotNull(groups = CreateGroup.class)
        @IdNumber(message = "身份证号错误", groups = CreateGroup.class)
        private String idCard;

        @Min(value = 10, groups = {CreateGroup.class, UpdateGroup.class})
        private int age;

        @Phone(message = "手机号错误", groups = {CreateGroup.class, UpdateGroup.class})
        private String phone;

        public String getIdCard() {
            return idCard;
        }

        public void setIdCard(String idCard) {
            this.idCard = idCard;
        }

        public int getAge() {
            return age;
        }

        public void setAge(int age) {
            this.age = age;
        }

        public String getPhone() {
            return phone;
        }

        public void setPhone(String phone) {
            this.phone = phone;
        }
    }

}