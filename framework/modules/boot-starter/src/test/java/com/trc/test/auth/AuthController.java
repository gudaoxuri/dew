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

import com.ecfront.dew.common.$;
import com.ecfront.dew.common.Resp;
import ms.dew.Dew;
import ms.dew.core.DewContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth/")
public class AuthController {

    private static Map<String, UserDTO> MOCK_USER_CONTAINER = new HashMap<>();

    private Logger logger = LoggerFactory.getLogger(AuthController.class);

    @PostConstruct
    public void init() {
        DewContext.setOptInfoClazz(OptInfoExt.class);
    }

    /**
     * 模拟用户注册
     */
    @PostMapping(value = "register")
    public Resp<Void> register(@RequestBody UserDTO userDTO) {
        // 实际注册处理
        userDTO.setId($.field.createUUID());
        MOCK_USER_CONTAINER.put(userDTO.getId(), userDTO);
        return Resp.success(null);
    }

    /**
     * 模拟用户登录
     */
    @PostMapping(value = "login")
    public Resp<String> login(@RequestBody LoginDTO loginDTO) throws Exception {
        // 实际登录处理
        UserDTO userDTO = MOCK_USER_CONTAINER.values().stream().filter(u -> u.getIdCard().equals(loginDTO.getIdCard())).findFirst().get();
        if (!loginDTO.getPassword().equals(userDTO.getPassword())) {
            throw Dew.E.e("ASXXX0", new Exception("密码错误"));
        }
        String token = $.field.createUUID();
        Dew.auth.setOptInfo(new OptInfoExt()
                .setIdCard(userDTO.getIdCard())
                .setAccountCode(loginDTO.getIdCard())
                .setToken(token)
                .setName(userDTO.getName())
                .setMobile(userDTO.getPhone()));
        return Resp.success(token);
    }

    /**
     * 模拟业务操作
     */
    @GetMapping(value = "business/someopt")
    public Resp<OptInfoExt> someOpt() {
        // 获取登录用户信息
        return Dew.auth.getOptInfo().map(i -> Resp.success((OptInfoExt) i)).orElse(Resp.unAuthorized("用户认证错误"));
    }

    /**
     * 模拟用户注销
     */
    @DeleteMapping(value = "logout")
    public Resp<Void> logout() {
        // 实际注册处理
        Dew.auth.removeOptInfo();
        return Resp.success(null);
    }

    public static class LoginDTO {

        private String idCard;

        private String password;

        public String getIdCard() {
            return idCard;
        }

        public void setIdCard(String idCard) {
            this.idCard = idCard;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }
    }

    public static class UserDTO {

        private String id;

        private String name;

        private String phone;

        private String idCard;

        private String password;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getPhone() {
            return phone;
        }

        public void setPhone(String phone) {
            this.phone = phone;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public String getIdCard() {
            return idCard;
        }

        public void setIdCard(String idCard) {
            this.idCard = idCard;
        }
    }

}
