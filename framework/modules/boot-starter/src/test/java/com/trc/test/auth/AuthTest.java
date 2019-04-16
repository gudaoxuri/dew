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

package com.trc.test.auth;


import com.ecfront.dew.common.Resp;
import ms.dew.Dew;
import org.junit.Assert;
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
     *
     * @throws Exception the exception
     */
    public void testAll() throws Exception {
        AuthController.UserDTO userDTO = new AuthController.UserDTO();
        userDTO.setIdCard("331023395739483150");
        userDTO.setName("辰");
        userDTO.setPassword("123456");
        userDTO.setPhone("15957199704");
        Resp registerResult = testRestTemplate.postForObject("/auth/register", userDTO, Resp.class);
        Assert.assertEquals("200", registerResult.getCode());
        //["/auth/register/*","/auth/re?","/user/**","/tes[t]"]
        registerResult = testRestTemplate.postForObject("/auth/register/user", userDTO, Resp.class);
        Assert.assertEquals("403", registerResult.getCode());
        registerResult = testRestTemplate.postForObject("/auth/reg", userDTO, Resp.class);
        Assert.assertEquals("403", registerResult.getCode());
        registerResult = testRestTemplate.postForObject("/user/register/hello", userDTO, Resp.class);
        Assert.assertEquals("403", registerResult.getCode());
        registerResult = testRestTemplate.postForObject("/test", userDTO, Resp.class);
        Assert.assertEquals("403", registerResult.getCode());

        AuthController.LoginDTO loginDTO = new AuthController.LoginDTO();
        loginDTO.setIdCard(userDTO.getIdCard());
        loginDTO.setPassword(userDTO.getPassword() + "1");
        Resp<String> loginResult = testRestTemplate.postForObject("/auth/login", loginDTO, Resp.class);
        Assert.assertEquals("ASXXX0", loginResult.getCode());
        loginDTO.setPassword(userDTO.getPassword());
        loginResult = Resp.generic(testRestTemplate.postForObject("/auth/login", loginDTO, Resp.class), String.class);
        Assert.assertEquals("200", loginResult.getCode());
        String token1 = loginResult.getBody();
        loginResult = Resp.generic(testRestTemplate.postForObject("/auth/login", loginDTO, Resp.class), String.class);
        Assert.assertEquals("200", loginResult.getCode());
        String token2 = loginResult.getBody();

        HttpHeaders headers = new HttpHeaders();
        headers.set(Dew.dewConfig.getSecurity().getTokenFlag(), token1);
        Resp<OptInfoExt> bussinessResult = Resp.generic(
                testRestTemplate.exchange("/auth/business/someopt",
                        HttpMethod.GET, new HttpEntity<>(null, headers), Resp.class).getBody(),
                OptInfoExt.class);
        Assert.assertEquals("401", bussinessResult.getCode());
        headers.set(Dew.dewConfig.getSecurity().getTokenFlag(), token2);
        bussinessResult = Resp.generic(
                testRestTemplate.exchange("/auth/business/someopt",
                        HttpMethod.GET, new HttpEntity<>(null, headers), Resp.class).getBody(), OptInfoExt.class);
        Assert.assertEquals("200", bussinessResult.getCode());
        Assert.assertEquals("331023395739483150", bussinessResult.getBody().getIdCard());

        testRestTemplate.exchange("/auth/logout", HttpMethod.DELETE, new HttpEntity<>(null, headers), Resp.class);
        bussinessResult = Resp.generic(
                testRestTemplate.exchange("/auth/business/someopt",
                        HttpMethod.GET, new HttpEntity<>(null, headers), Resp.class).getBody(), OptInfoExt.class);
        Assert.assertEquals("401", bussinessResult.getCode());
    }

}
