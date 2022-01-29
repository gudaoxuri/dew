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

package group.idealworld.dew.example.auth;

import com.ecfront.dew.common.$;
import com.ecfront.dew.common.Resp;
import group.idealworld.dew.Dew;
import group.idealworld.dew.core.DewContext;
import group.idealworld.dew.example.auth.dto.OptInfoExt;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Auth example controller.
 *
 * @author gudaoxuri
 */
@RestController
@RequestMapping("/")
public class AuthExampleController {

    private static Map<String, User> MOCK_USER_CONTAINER = new HashMap<>();

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
     * @param user the user
     * @return the resp
     */
    @PostMapping(value = "user/register")
    public Resp<Void> register(@RequestBody User user) {
        // 实际注册处理
        user.setId($.field.createUUID());
        MOCK_USER_CONTAINER.put(user.getId(), user);
        return Resp.success(null);
    }

    /**
     * 模拟用户登录.
     *
     * @param loginDTO the login dto
     * @return the resp
     */
    @PostMapping(value = "auth/login")
    public Resp<String> login(@RequestBody LoginDTO loginDTO) {
        // 实际登录处理
        User user = MOCK_USER_CONTAINER.values().stream().filter(u -> u.getIdCard().equals(loginDTO.getIdCard())).findFirst().get();
        String token = $.field.createUUID();
        var optInfo = new OptInfoExt();
        optInfo.setIdCard(user.getIdCard());
        optInfo.setAccountCode($.field.createShortUUID());
        optInfo.setToken(token);
        optInfo.setName(user.getName());
        optInfo.setMobile(user.getPhone());
        Dew.auth.setOptInfo(optInfo);
        return Resp.success(token);
    }

    /**
     * 模拟业务操作.
     *
     * @return the resp
     */
    @GetMapping(value = "business/someopt")
    public Resp<Void> someOpt() {
        // 获取登录用户信息
        Optional<OptInfoExt> optInfoExtOpt = Dew.auth.getOptInfo();
        if (!optInfoExtOpt.isPresent()) {
            return Resp.unAuthorized("用户认证错误");
        }
        // 登录用户的信息
        optInfoExtOpt.get();
        return Resp.success(null);
    }

    /**
     * 模拟用户注销.
     *
     * @return the resp
     */
    @DeleteMapping(value = "auth/logout")
    public Resp<Void> logout() {
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
     * User.
     */
    public static class User {

        private String id;

        private String name;

        private String phone;

        private String idCard;

        private String password;

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
    }

}
