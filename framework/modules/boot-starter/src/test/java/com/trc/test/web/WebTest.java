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
import org.junit.jupiter.api.Assertions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Web test.
 *
 * @author gudaoxuri
 */
@Component
public class WebTest {

    private Logger logger = LoggerFactory.getLogger(WebTest.class);

    @Autowired
    private TestRestTemplate testRestTemplate;

    /**
     * Test all.
     *
     * @throws Exception the exceptionR
     */
    public void testAll() throws Exception {
        testHashCollision();
        testResponseFormat();
        testValidation();
        testTimeConvert();
        testAutoTrim();
    }

    private void testHashCollision() {
        String body = IntStream.range(0, 914748)
                .mapToObj(i -> "\"" + i + "\":0")
                .collect(Collectors.joining(",", "{\"idCard\":\"110101201709013174\",\"age\":20,\"phone\":\"18651111111\",\"dos\":{", "}}"));
        long start = System.currentTimeMillis();
        testRestTemplate.postForObject("/test/hash-collision", body, Void.class);
        /* String str = $.http.post(url + "/valid-create", body);*/
        System.out.println("Use time :" + ((System.currentTimeMillis() - start) / 1000));
        /*WebExampleController.User user = $.json.toObject(str, WebExampleController.User.class);
        Assertions.assertEquals("110101201709013174", user.getIdCard());
        */
    }

    private void testResponseFormat() {
        Resp<String> result = Resp.generic(testRestTemplate.getForObject("/test/success?q=TEST", Resp.class), String.class);
        Assertions.assertEquals("successful", result.getBody());
        result = Resp.generic(testRestTemplate.getForObject("/test/badRequest?q=TEST", Resp.class), String.class);
        Assertions.assertEquals("badrequest", result.getMessage());
        ResponseEntity<Resp> resultEntity = testRestTemplate.getForEntity("/test/customError?q=TEST", Resp.class);
        Assertions.assertEquals(500, resultEntity.getStatusCodeValue());
        Assertions.assertEquals("A000", resultEntity.getBody().getCode());
        resultEntity = testRestTemplate.getForEntity("/test/customHttpState?q=TEST", Resp.class);
        Assertions.assertEquals(401, resultEntity.getStatusCodeValue());
        Assertions.assertEquals("A000", resultEntity.getBody().getCode());
    }

    private void testValidation() {
        // group:create
        WebController.UserDTO userDTO = new WebController.UserDTO();
        userDTO.setAge(11);
        userDTO.setIdCard("110101201709013173");
        ResponseEntity<Resp> createResult = testRestTemplate.postForEntity("/test/valid-create", userDTO, Resp.class);
        Assertions.assertEquals(200, createResult.getStatusCodeValue());
        Assertions.assertEquals("400", createResult.getBody().getCode());
        Assertions.assertTrue(createResult.getBody().getMessage().contains("Detail"));
        userDTO.setIdCard("110101201709013174");
        createResult = testRestTemplate.postForEntity("/test/valid-create", userDTO, Resp.class);
        Assertions.assertEquals(200, createResult.getStatusCodeValue());
        Assertions.assertEquals("400", createResult.getBody().getCode());
        Assertions.assertTrue(createResult.getBody().getMessage().contains("Detail"));
        userDTO.setPhone("15971997041");
        ResponseEntity<WebController.UserDTO> createSuccessResult = testRestTemplate.postForEntity(
                "/test/valid-create", userDTO, WebController.UserDTO.class);
        Assertions.assertEquals(200, createSuccessResult.getStatusCodeValue());
        Assertions.assertEquals("15971997041", createSuccessResult.getBody().getPhone());

        // group:update
        Map<String, Object> map = new HashMap<>();
        map.put("idCard", "110101201709013173");
        ResponseEntity<Resp> updateResult = testRestTemplate.postForEntity("/test/valid-update", map, Resp.class);
        Assertions.assertEquals(200, updateResult.getStatusCodeValue());
        Assertions.assertEquals("400", updateResult.getBody().getCode());
        Assertions.assertTrue(updateResult.getBody().getMessage().contains("Detail"));
        map.put("idCard", "110101201709013174");
        ResponseEntity<WebController.UserDTO> updateSuccessResult = testRestTemplate.postForEntity(
                "/test/valid-update", map, WebController.UserDTO.class);
        Assertions.assertEquals(200, updateSuccessResult.getStatusCodeValue());
        Assertions.assertEquals("110101201709013174", updateSuccessResult.getBody().getIdCard());

        // path
        ResponseEntity<Resp> pathResult = testRestTemplate.getForEntity("/test/valid-method-spring/1", Resp.class);
        Assertions.assertEquals(200, pathResult.getStatusCodeValue());
        Assertions.assertEquals("400", pathResult.getBody().getCode());
        Assertions.assertTrue(pathResult.getBody().getMessage().contains("Detail"));
        pathResult = testRestTemplate.getForEntity("/test/valid-method-own/1865712020", Resp.class);
        Assertions.assertEquals(200, pathResult.getStatusCodeValue());
        Assertions.assertEquals("400", pathResult.getBody().getCode());
        Assertions.assertTrue(pathResult.getBody().getMessage().contains("Detail"));

        // error mapping
        ResponseEntity<Resp> mappingResult = testRestTemplate.getForEntity("/test/error-mapping", Resp.class);
        Assertions.assertEquals(401, mappingResult.getStatusCodeValue());
        Assertions.assertEquals("x00010", mappingResult.getBody().getCode());
        Assertions.assertEquals("[Internal Server Error]认证错误", mappingResult.getBody().getMessage());
        logger.info($.json.toJsonString(mappingResult.getBody()));
    }

    private void testAutoTrim() throws Exception {
        WebController.UserDTO userDTO = new WebController.UserDTO();
        userDTO.setAge(11);
        userDTO.setIdCard("110101201709013174");
        userDTO.setPhone("15971997041");
        WebController.UserDTO result = testRestTemplate.postForObject(
                "/test/valid-create", userDTO, WebController.UserDTO.class);
        Assertions.assertEquals("15971997041", result.getPhone());
        Assertions.assertNull(result.getAddr());
        userDTO.setAddr("   1   ");
        result = testRestTemplate.postForObject(
                "/test/valid-create", userDTO, WebController.UserDTO.class);
        Assertions.assertEquals("15971997041", result.getPhone());
        Assertions.assertEquals("1", result.getAddr());
    }

    private void testTimeConvert() throws IOException {
        Resp timeResult = testRestTemplate.getForObject(
                "/test/time/param?date-time=2013-07-02 17:39:00&date=2013-07-02&time=17:39:12&instant=1509430693548",
                Resp.class);
        Assertions.assertTrue(timeResult.ok());
        WebController.TimeDTO timeDTO = new WebController.TimeDTO();
        timeDTO.setLocalDate(LocalDate.now());
        timeDTO.setLocalTime(LocalTime.now());
        timeDTO.setLocalDateTime(LocalDateTime.now());
        timeResult = testRestTemplate.postForObject("/test/time/body", timeDTO, Resp.class);
        Assertions.assertTrue(timeResult.ok());
    }

}
