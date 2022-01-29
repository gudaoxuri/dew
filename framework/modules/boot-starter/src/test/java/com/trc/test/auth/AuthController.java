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

import com.ecfront.dew.common.$;
import com.ecfront.dew.common.Resp;
import group.idealworld.dew.Dew;
import group.idealworld.dew.core.DewContext;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

/**
 * Auth controller.
 *
 * @author gudaoxuri
 */
@RestController
@RequestMapping("/auth/")
public class AuthController {

    private static Map<String, UserDTO> MOCK_USER_CONTAINER = new HashMap<>();

    /**
     * Init.
     */
    @PostConstruct
    public void init() {
        DewContext.setOptInfoClazz(OptInfoExt.class);
    }

    /**
     * 模拟用户注册.
     *
     * @param userDTO the user dto
     * @return the resp
     * @throws InterruptedException the interrupted exception
     */
    @PostMapping(value = "register")
    public Resp<Void> register(@RequestBody UserDTO userDTO) throws InterruptedException {
        Thread.sleep(10);
        // 实际注册处理
        userDTO.setId($.field.createUUID());
        MOCK_USER_CONTAINER.put(userDTO.getId(), userDTO);
        return Resp.success(null);
    }

    /**
     * 模拟用户登录.
     *
     * @param loginDTO the login dto
     * @return the resp
     * @throws Exception the exception
     */
    @PostMapping(value = "login")
    public Resp<String> login(@RequestBody LoginDTO loginDTO) throws Exception {
        Thread.sleep(10);
        // 实际登录处理
        UserDTO userDTO = MOCK_USER_CONTAINER.values().stream()
                .filter(u -> u.getIdCard().equals(loginDTO.getIdCard())).findFirst().get();
        if (!loginDTO.getPassword().equals(userDTO.getPassword())) {
            throw Dew.E.e("ASXXX0", new Exception("密码错误"));
        }
        String token = $.field.createUUID();
        var optInfo = new OptInfoExt();
        optInfo.setIdCard(userDTO.getIdCard());
        optInfo.setAccountCode(loginDTO.getIdCard());
        optInfo.setToken(token);
        optInfo.setTokenKind(Dew.context().getTokenKind());
        optInfo.setName(userDTO.getName());
        optInfo.setMobile(userDTO.getPhone());
        optInfo.setRoles(new String[]{userDTO.getRole()});
        Dew.auth.setOptInfo(optInfo);
        return Resp.success(token);
    }

    /**
     * 模拟业务操作.
     *
     * @return the resp
     * @throws InterruptedException the interrupted exception
     */
    @GetMapping(value = "business/someopt")
    public Resp<OptInfoExt> someOpt() throws InterruptedException {
        Thread.sleep(10);
        // 获取登录用户信息
        return Dew.auth.getOptInfo().map(i -> Resp.success((OptInfoExt) i)).orElse(Resp.unAuthorized("用户认证错误"));
    }

    /**
     * 模拟用户注销.
     *
     * @return the resp
     * @throws InterruptedException the interrupted exception
     */
    @DeleteMapping(value = "logout")
    public Resp<Void> logout() throws InterruptedException {
        Thread.sleep(10);
        // 实际注册处理
        Dew.auth.removeOptInfo();
        return Resp.success(null);
    }

    /**
     * Login dto.
     */
    public static class LoginDTO {

        private String idCard;

        private String password;

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
         * Gets password.
         *
         * @return the password
         */
        public String getPassword() {
            return password;
        }

        /**
         * Sets password.
         *
         * @param password the password
         */
        public void setPassword(String password) {
            this.password = password;
        }
    }

    /**
     * User dto.
     */
    public static class UserDTO {

        private String id;

        private String name;

        private String phone;

        private String idCard;

        private String password;

        private String role;

        /**
         * Gets id.
         *
         * @return the id
         */
        public String getId() {
            return id;
        }

        /**
         * Sets id.
         *
         * @param id the id
         */
        public void setId(String id) {
            this.id = id;
        }

        /**
         * Gets name.
         *
         * @return the name
         */
        public String getName() {
            return name;
        }

        /**
         * Sets name.
         *
         * @param name the name
         */
        public void setName(String name) {
            this.name = name;
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
         * Gets password.
         *
         * @return the password
         */
        public String getPassword() {
            return password;
        }

        /**
         * Sets password.
         *
         * @param password the password
         */
        public void setPassword(String password) {
            this.password = password;
        }

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
         * Gets role.
         *
         * @return the role
         */
        public String getRole() {
            return role;
        }

        /**
         * Sets role.
         *
         * @param role the role
         * @return the role
         */
        public UserDTO setRole(String role) {
            this.role = role;
            return this;
        }
    }

}
