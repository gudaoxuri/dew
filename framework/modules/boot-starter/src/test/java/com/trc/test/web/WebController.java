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

package com.trc.test.web;

import com.ecfront.dew.common.Resp;
import com.ecfront.dew.common.StandardCode;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.tairanchina.csp.dew.Dew;
import com.tairanchina.csp.dew.core.web.validation.CreateGroup;
import com.tairanchina.csp.dew.core.web.validation.IdNumber;
import com.tairanchina.csp.dew.core.web.validation.Phone;
import com.tairanchina.csp.dew.core.web.validation.UpdateGroup;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.junit.Assert;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@RestController
@Api(value = "测试", description = "test")
@RequestMapping(value = "/test/")
@Validated
public class WebController {

    @GetMapping(value = "success")
    @ApiOperation(value = "success")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "q", value = "query", paramType = "query", dataType = "string", required = true),
    })
    public Resp<String> success(@RequestParam String q) {
        return Resp.success("successful");
    }

    @GetMapping(value = "badRequest")
    @ApiOperation(value = "badRequest")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "q", value = "query", paramType = "query", dataType = "string", required = true),
    })
    public Resp<String> badRequest(@RequestParam String q) {
        return Resp.badRequest("badrequest");
    }

    @GetMapping(value = "customError")
    @ApiOperation(value = "customError")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "q", value = "query", paramType = "query", dataType = "string", required = true),
    })
    public Resp<String> customError(@RequestParam String q) throws Exception {
        throw Dew.E.e("A000", new Exception("io error"));
    }

    @GetMapping(value = "throws")
    public Resp<String> throwsE(@RequestParam String q) throws Exception {
        int i = 1 / 0;
        return Resp.success(null);
    }

    @GetMapping(value = "customHttpState")
    @ApiOperation(value = "customHttpState")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "q", value = "query", paramType = "query", dataType = "string", required = true),
    })
    public Resp<String> customHttpState(@RequestParam String q) throws IOException {
        throw Dew.E.e("A000", new IOException("io error"), StandardCode.UNAUTHORIZED);
    }

    @PostMapping(value = "valid-create")
    public UserDTO validCreate(@Validated(CreateGroup.class) @RequestBody UserDTO userDTO) {
        return userDTO;
    }

    @PostMapping(value = "valid-update")
    public UserDTO validUpdate(@Validated(UpdateGroup.class) @RequestBody UserDTO userDTO) {
        return userDTO;
    }

    @GetMapping(value = "valid-method-spring/{age}")
    public String validInMethod(@Min(value = 2, message = "age必须大于2") @PathVariable("age") int age) {
        return String.valueOf(age);
    }

    @GetMapping(value = "valid-method-own/{phone}")
    public String validInMethod(@Phone @PathVariable("phone") String phone) {
        return phone;
    }

    @GetMapping(value = "error-mapping")
    public String errorMapping() {
        throw new AuthException();
    }

    @GetMapping(value = "fallback1")
    public Resp<String> fallbackTest1() {
        return Resp.serverError("ssss").fallback();
    }

    @GetMapping(value = "fallback2")
    public Resp<String> fallbackTest2() {
        throw new EmptyResultDataAccessException(1);
    }

    @GetMapping("time/param")
    public Resp<String> timeConvertParam(@RequestParam("date-time") LocalDateTime localDateTime, @RequestParam("date") LocalDate localDate, @RequestParam("time") LocalTime localTime, @RequestParam("instant") Instant instant) {
        Assert.assertNotNull(localDate);
        Assert.assertNotNull(localDateTime);
        Assert.assertNotNull(localTime);
        Assert.assertNotNull(instant);
        return Resp.success(null);
    }

    @PostMapping("time/body")
    public Resp<String> timeConvertBody(@RequestBody TimeDTO timeDTO) {
        Assert.assertNotNull(timeDTO);
        return Resp.success(null);
    }

    public static class TimeDTO {

        private LocalTime localTime;

        private LocalDate localDate;

        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
        private LocalDateTime localDateTime;

        private Instant instant;

        public LocalTime getLocalTime() {
            return localTime;
        }

        public void setLocalTime(LocalTime localTime) {
            this.localTime = localTime;
        }

        public LocalDate getLocalDate() {
            return localDate;
        }

        public void setLocalDate(LocalDate localDate) {
            this.localDate = localDate;
        }

        public LocalDateTime getLocalDateTime() {
            return localDateTime;
        }

        public void setLocalDateTime(LocalDateTime localDateTime) {
            this.localDateTime = localDateTime;
        }

        public Instant getInstant() {
            return instant;
        }

        public void setInstant(Instant instant) {
            this.instant = instant;
        }
    }

    public static class UserDTO {

        @NotNull(groups = CreateGroup.class)
        @IdNumber(message = "身份证号错误", groups = {CreateGroup.class, UpdateGroup.class})
        private String idCard;

        @Min(value = 10, groups = {CreateGroup.class, UpdateGroup.class})
        private Integer age;

        @NotNull(groups = CreateGroup.class)
        @Phone(message = "手机号错误", groups = {CreateGroup.class, UpdateGroup.class})
        private String phone;


        public String getIdCard() {
            return idCard;
        }

        public void setIdCard(String idCard) {
            this.idCard = idCard;
        }

        public Integer getAge() {
            return age;
        }

        public void setAge(Integer age) {
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
