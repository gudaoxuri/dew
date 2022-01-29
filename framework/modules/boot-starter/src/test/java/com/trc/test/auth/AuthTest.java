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

package com.trc.test.auth;


import com.ecfront.dew.common.Resp;
import group.idealworld.dew.Dew;
import org.junit.jupiter.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;

/**
 * Auth test.
 *
 * @author gudaoxuri
 */
@Component
public class AuthTest {

    @Autowired
    private TestRestTemplate testRestTemplate;

    /**
     * Test all.
     */
    public void testAll() {
        AuthController.UserDTO userDTO = new AuthController.UserDTO();
        userDTO.setIdCard("331023395739483150");
        userDTO.setName("辰");
        userDTO.setPassword("123456");
        userDTO.setPhone("15957199704");
        userDTO.setRole("admin");
        Resp registerResult = testRestTemplate.postForObject("/auth/register", userDTO, Resp.class);
        Assertions.assertEquals("200", registerResult.getCode());

        AuthController.LoginDTO loginDTO = new AuthController.LoginDTO();
        loginDTO.setIdCard(userDTO.getIdCard());
        loginDTO.setPassword(userDTO.getPassword() + "1");
        Resp<String> loginResult = testRestTemplate.postForObject("/auth/login", loginDTO, Resp.class);
        Assertions.assertEquals("ASXXX0", loginResult.getCode());
        loginDTO.setPassword(userDTO.getPassword());
        loginResult = Resp.generic(testRestTemplate.postForObject("/auth/login", loginDTO, Resp.class), String.class);
        Assertions.assertEquals("200", loginResult.getCode());
        String token = loginResult.getBody();

        HttpHeaders headers = new HttpHeaders();
        headers.set(Dew.dewConfig.getSecurity().getTokenFlag(), token);
        Resp<OptInfoExt> businessResult = Resp.generic(
                testRestTemplate.exchange("/auth/business/someopt",
                        HttpMethod.GET, new HttpEntity<>(null, headers), Resp.class).getBody(), OptInfoExt.class);
        Assertions.assertEquals("200", businessResult.getCode());
        Assertions.assertEquals("331023395739483150", businessResult.getBody().getIdCard());


        testRouter(headers);

        testRestTemplate.exchange("/auth/logout", HttpMethod.DELETE, new HttpEntity<>(null, headers), Resp.class);
        businessResult = Resp.generic(
                testRestTemplate.exchange("/auth/business/someopt",
                        HttpMethod.GET, new HttpEntity<>(null, headers), Resp.class).getBody(), OptInfoExt.class);
        Assertions.assertEquals("401", businessResult.getCode());


        testTokenKind(loginDTO);

    }

    private void testRouter(HttpHeaders headers) {
        // blockUri
        AuthController.UserDTO userDTO = new AuthController.UserDTO();
        userDTO.setIdCard("331023395739483150");
        userDTO.setName("辰");
        userDTO.setPassword("123456");
        userDTO.setPhone("15957199704");

        //["/auth/register/*","/auth/re?","/user/**","/tes[t]"]
        Resp registerResult = testRestTemplate.postForObject("/auth/register/user", userDTO, Resp.class);
        Assertions.assertEquals("403", registerResult.getCode());
        registerResult = testRestTemplate.postForObject("/auth/reg", userDTO, Resp.class);
        Assertions.assertEquals("403", registerResult.getCode());
        registerResult = testRestTemplate.postForObject("/user/register/hello", userDTO, Resp.class);
        Assertions.assertEquals("403", registerResult.getCode());
        registerResult = testRestTemplate.postForObject("/test", userDTO, Resp.class);
        Assertions.assertEquals("403", registerResult.getCode());

        // roleAuth
        // 没有token
        registerResult = testRestTemplate.exchange("/mgr/register/user",
                HttpMethod.GET, new HttpEntity<>(null, null), Resp.class).getBody();
        Assertions.assertEquals("401", registerResult.getCode());
        // admin访问只有user允可的url
        registerResult = testRestTemplate.exchange("/user/only-user-role/xx",
                HttpMethod.GET, new HttpEntity<>(null, headers), Resp.class).getBody();
        Assertions.assertEquals("401", registerResult.getCode());
        // admin访问只有admin允可的url
        registerResult = testRestTemplate.exchange("/mgr/register/user",
                HttpMethod.GET, new HttpEntity<>(null, headers), Resp.class).getBody();
        Assertions.assertEquals("404", registerResult.getCode());
        // admin访问admin和user允可的url
        registerResult = testRestTemplate.exchange("/user/u1",
                HttpMethod.GET, new HttpEntity<>(null, headers), Resp.class).getBody();
        Assertions.assertEquals("404", registerResult.getCode());
    }

    private void testTokenKind(AuthController.LoginDTO loginDTO) {
        // PC不保留历史版本
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(Dew.dewConfig.getSecurity().getTokenKindFlag(), "pc");
        final String token1 = Resp.generic(
                testRestTemplate.exchange("/auth/login",
                        HttpMethod.POST, new HttpEntity<>(loginDTO, httpHeaders), Resp.class).getBody(), String.class).getBody();
        final String token2 = Resp.generic(
                testRestTemplate.exchange("/auth/login",
                        HttpMethod.POST, new HttpEntity<>(loginDTO, httpHeaders), Resp.class).getBody(), String.class).getBody();

        httpHeaders = new HttpHeaders();
        // 上一次Token已失效
        httpHeaders.set(Dew.dewConfig.getSecurity().getTokenFlag(), token1);
        Resp<OptInfoExt> bussinessResult = Resp.generic(
                testRestTemplate.exchange("/auth/business/someopt",
                        HttpMethod.GET, new HttpEntity<>(null, httpHeaders), Resp.class).getBody(),
                OptInfoExt.class);
        Assertions.assertEquals("401", bussinessResult.getCode());
        httpHeaders.set(Dew.dewConfig.getSecurity().getTokenFlag(), token2);
        bussinessResult = Resp.generic(
                testRestTemplate.exchange("/auth/business/someopt",
                        HttpMethod.GET, new HttpEntity<>(null, httpHeaders), Resp.class).getBody(),
                OptInfoExt.class);
        Assertions.assertEquals("200", bussinessResult.getCode());


        // Mobile保留1个历史版本
        httpHeaders = new HttpHeaders();
        httpHeaders.add(Dew.dewConfig.getSecurity().getTokenKindFlag(), "mobile");
        final String token3 = Resp.generic(
                testRestTemplate.exchange("/auth/login",
                        HttpMethod.POST, new HttpEntity<>(loginDTO, httpHeaders), Resp.class).getBody(), String.class).getBody();
        final String token4 = Resp.generic(
                testRestTemplate.exchange("/auth/login",
                        HttpMethod.POST, new HttpEntity<>(loginDTO, httpHeaders), Resp.class).getBody(), String.class).getBody();
        final String token5 = Resp.generic(
                testRestTemplate.exchange("/auth/login",
                        HttpMethod.POST, new HttpEntity<>(loginDTO, httpHeaders), Resp.class).getBody(), String.class).getBody();

        httpHeaders = new HttpHeaders();
        // Token3已失效
        httpHeaders.set(Dew.dewConfig.getSecurity().getTokenFlag(), token3);
        bussinessResult = Resp.generic(
                testRestTemplate.exchange("/auth/business/someopt",
                        HttpMethod.GET, new HttpEntity<>(null, httpHeaders), Resp.class).getBody(),
                OptInfoExt.class);
        Assertions.assertEquals("401", bussinessResult.getCode());
        // Token4有效
        httpHeaders.set(Dew.dewConfig.getSecurity().getTokenFlag(), token4);
        bussinessResult = Resp.generic(
                testRestTemplate.exchange("/auth/business/someopt",
                        HttpMethod.GET, new HttpEntity<>(null, httpHeaders), Resp.class).getBody(),
                OptInfoExt.class);
        Assertions.assertEquals("200", bussinessResult.getCode());
        // Token5有效
        httpHeaders.set(Dew.dewConfig.getSecurity().getTokenFlag(), token5);
        bussinessResult = Resp.generic(
                testRestTemplate.exchange("/auth/business/someopt",
                        HttpMethod.GET, new HttpEntity<>(null, httpHeaders), Resp.class).getBody(),
                OptInfoExt.class);
        Assertions.assertEquals("200", bussinessResult.getCode());
    }


}
