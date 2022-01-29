/*
 * Copyright 2022. the original author or authors
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

import com.ecfront.dew.common.$;
import com.ecfront.dew.common.Resp;
import com.ecfront.dew.common.StandardCode;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.JsonNode;
import group.idealworld.dew.Dew;
import group.idealworld.dew.core.web.validation.CreateGroup;
import group.idealworld.dew.core.web.validation.IdNumber;
import group.idealworld.dew.core.web.validation.Phone;
import group.idealworld.dew.core.web.validation.UpdateGroup;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.junit.jupiter.api.Assertions;
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
import java.util.Map;

/**
 * Web controller.
 *
 * @author gudaoxuri
 */
@RestController
@Tag(name = "test", description = "Test API")
@RequestMapping(value = "/test/")
@Validated
public class WebController {

    /**
     * Hash 碰撞测试.
     *
     * @param body the body
     * @return the void
     */
    @PostMapping(value = "hash-collision")
    public Void hashCollision(@RequestBody String body) {
        long start = System.currentTimeMillis();
        JsonNode json = $.json.toJson(body);
        System.out.println("to Json Use time :" + ((System.currentTimeMillis() - start) / 1000));
        start = System.currentTimeMillis();
        $.json.toObject(body, UserDTO.class);
        System.out.println("to User Use time :" + ((System.currentTimeMillis() - start) / 1000));
        start = System.currentTimeMillis();
        Map<String, Integer> map = $.json.toMap(json.get("dos"), String.class, Integer.class);
        System.out.println("to Map Use time :" + ((System.currentTimeMillis() - start) / 1000));
        return null;
    }

    /**
     * Success resp.
     *
     * @param q the q
     * @return the resp
     */
    @GetMapping(value = "success")
    @Operation(summary = "success")
    public Resp<String> success(
            @Parameter(name = "q", in = ParameterIn.QUERY, required = true) @RequestParam String q) {
        return Resp.success("successful");
    }

    @GetMapping(value = "ident-info")
    public Resp<String> getIdentInfo() {
        return Resp.success($.json.toJsonString(Dew.context().optInfo().get()));
    }

    /**
     * Bad request resp.
     *
     * @param q the q
     * @return the resp
     */
    @GetMapping(value = "badRequest")
    @Operation(summary = "badRequest")
    public Resp<String> badRequest(
            @Parameter(name = "q", in = ParameterIn.QUERY, required = true) @RequestParam String q) {
        return Resp.badRequest("badrequest");
    }

    /**
     * Custom error resp.
     *
     * @param q the q
     * @return the resp
     * @throws Exception the exception
     */
    @GetMapping(value = "customError")
    @Operation(summary = "customError")
    public Resp<String> customError(
            @Parameter(name = "q", in = ParameterIn.QUERY, required = true) @RequestParam String q) throws Exception {
        throw Dew.E.e("A000", new Exception("io error"));
    }

    /**
     * Throws e resp.
     *
     * @param q the q
     * @return the resp
     * @throws Exception the exception
     */
    @GetMapping(value = "throws")
    public Resp<String> throwsE(@RequestParam String q) {
        int i = 1 / 0;
        return Resp.success(i + "");
    }

    /**
     * Custom http state resp.
     *
     * @param q the q
     * @return the resp
     * @throws IOException the io exception
     */
    @GetMapping(value = "customHttpState")
    @Operation(summary = "customHttpState")
    public Resp<String> customHttpState(
            @Parameter(name = "q", in = ParameterIn.QUERY, required = true) @RequestParam String q) throws IOException {
        throw Dew.E.e("A000", new IOException("io error"), StandardCode.UNAUTHORIZED);
    }

    /**
     * Valid create user dto.
     *
     * @param userDTO the user dto
     * @return the user dto
     */
    @PostMapping(value = "valid-create")
    public UserDTO validCreate(@Validated(CreateGroup.class) @RequestBody UserDTO userDTO) {
        return userDTO;
    }

    /**
     * Valid update user dto.
     *
     * @param userDTO the user dto
     * @return the user dto
     */
    @PostMapping(value = "valid-update")
    public UserDTO validUpdate(@Validated(UpdateGroup.class) @RequestBody UserDTO userDTO) {
        return userDTO;
    }

    /**
     * Valid in method.
     *
     * @param age the age
     * @return the string
     */
    @GetMapping(value = "valid-method-spring/{age}")
    public String validInMethod(@Min(value = 2, message = "age必须大于2") @PathVariable("age") int age) {
        return String.valueOf(age);
    }

    /**
     * Valid in method.
     *
     * @param phone the phone
     * @return the string
     */
    @GetMapping(value = "valid-method-own/{phone}")
    public String validInMethod(@Phone @PathVariable("phone") String phone) {
        return phone;
    }

    /**
     * Error mapping.
     *
     * @return the string
     */
    @GetMapping(value = "error-mapping")
    public String errorMapping() {
        throw new AuthException();
    }

    /**
     * Fallback test 1 resp.
     *
     * @return the resp
     */
    @GetMapping(value = "fallback1")
    public Resp<String> fallbackTest1() {
        return Resp.serverError("ssss").fallback();
    }

    /**
     * Fallback test 2 resp.
     *
     * @return the resp
     */
    @GetMapping(value = "fallback2")
    public Resp<String> fallbackTest2() {
        throw new EmptyResultDataAccessException(1);
    }

    /**
     * Time convert param resp.
     *
     * @param localDateTime the local date time
     * @param localDate     the local date
     * @param localTime     the local time
     * @param instant       the instant
     * @return the resp
     */
    @GetMapping("time/param")
    public Resp<String> timeConvertParam(@RequestParam("date-time") LocalDateTime localDateTime,
                                         @RequestParam("date") LocalDate localDate,
                                         @RequestParam("time") LocalTime localTime,
                                         @RequestParam("instant") Instant instant) {
        Assertions.assertNotNull(localDate);
        Assertions.assertNotNull(localDateTime);
        Assertions.assertNotNull(localTime);
        Assertions.assertNotNull(instant);
        return Resp.success(null);
    }

    /**
     * Time convert body resp.
     *
     * @param timeDTO the time dto
     * @return the resp
     */
    @PostMapping("time/body")
    public Resp<String> timeConvertBody(@RequestBody TimeDTO timeDTO) {
        Assertions.assertNotNull(timeDTO);
        return Resp.success(null);
    }

    /**
     * Time dto.
     */
    public static class TimeDTO {

        private LocalTime localTime;

        private LocalDate localDate;

        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
        private LocalDateTime localDateTime;

        private Instant instant;

        /**
         * Gets local time.
         *
         * @return the local time
         */
        public LocalTime getLocalTime() {
            return localTime;
        }

        /**
         * Sets local time.
         *
         * @param localTime the local time
         */
        public void setLocalTime(LocalTime localTime) {
            this.localTime = localTime;
        }

        /**
         * Gets local date.
         *
         * @return the local date
         */
        public LocalDate getLocalDate() {
            return localDate;
        }

        /**
         * Sets local date.
         *
         * @param localDate the local date
         */
        public void setLocalDate(LocalDate localDate) {
            this.localDate = localDate;
        }

        /**
         * Gets local date time.
         *
         * @return the local date time
         */
        public LocalDateTime getLocalDateTime() {
            return localDateTime;
        }

        /**
         * Sets local date time.
         *
         * @param localDateTime the local date time
         */
        public void setLocalDateTime(LocalDateTime localDateTime) {
            this.localDateTime = localDateTime;
        }

        /**
         * Gets instant.
         *
         * @return the instant
         */
        public Instant getInstant() {
            return instant;
        }

        /**
         * Sets instant.
         *
         * @param instant the instant
         */
        public void setInstant(Instant instant) {
            this.instant = instant;
        }
    }

    /**
     * User dto.
     */
    public static class UserDTO {

        @NotNull(groups = CreateGroup.class)
        @IdNumber(message = "身份证号错误", groups = {CreateGroup.class, UpdateGroup.class})
        private String idCard;

        @Min(value = 10, groups = {CreateGroup.class, UpdateGroup.class})
        private Integer age;

        @NotNull(groups = CreateGroup.class)
        @Phone(message = "手机号错误", groups = {CreateGroup.class, UpdateGroup.class})
        private String phone;

        private String addr;

        /**
         * Gets id card.
         *
         * @return the id card
         */
        public String getIdCard() {
            return idCard;
        }

        /**
         * Sets id card.
         *
         * @param idCard the id card
         */
        public void setIdCard(String idCard) {
            this.idCard = idCard;
        }

        /**
         * Gets age.
         *
         * @return the age
         */
        public Integer getAge() {
            return age;
        }

        /**
         * Sets age.
         *
         * @param age the age
         */
        public void setAge(Integer age) {
            this.age = age;
        }

        /**
         * Gets phone.
         *
         * @return the phone
         */
        public String getPhone() {
            return phone;
        }

        /**
         * Sets phone.
         *
         * @param phone the phone
         */
        public void setPhone(String phone) {
            this.phone = phone;
        }

        /**
         * Gets addr.
         *
         * @return the addr
         */
        public String getAddr() {
            return addr;
        }

        /**
         * Sets addr.
         *
         * @param addr the addr
         * @return the addr
         */
        public UserDTO setAddr(String addr) {
            this.addr = addr;
            return this;
        }
    }

}
