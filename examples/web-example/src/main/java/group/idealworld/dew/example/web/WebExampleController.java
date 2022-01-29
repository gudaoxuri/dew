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

package group.idealworld.dew.example.web;

import com.ecfront.dew.common.Resp;
import group.idealworld.dew.core.web.validation.CreateGroup;
import group.idealworld.dew.core.web.validation.IdNumber;
import group.idealworld.dew.core.web.validation.Phone;
import group.idealworld.dew.core.web.validation.UpdateGroup;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.extensions.Extension;
import io.swagger.v3.oas.annotations.extensions.ExtensionProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Web example controller.
 *
 * @author gudaoxuri
 */
@RestController
@Tag(name = "example", description = "示例应用说明")
@Validated // URL 类型的验证需要使用此注解
public class WebExampleController {

    /**
     * 最基础的Controller示例.
     *
     * @return result
     */
    @GetMapping("example")
    @Operation(summary = "示例方法", extensions = {@Extension(name = "FIN_EXT", properties = @ExtensionProperty(name = "REL", value = "s001,s002"))})
    public Map<String, Integer> example() {
        return new HashMap<>();
    }

    /**
     * 数据验证示例，针对 CreateGroup 这一标识组的 bean认证.
     *
     * @return the user
     */
    @PostMapping(value = "valid-create")
    public RbumCertDto.RbumCertDetailResp validCreate(@Validated(CreateGroup.class) @RequestBody RbumCertDto.RbumCertAddReq rbumCertAddReq) {
        return new RbumCertDto.RbumCertDetailResp();
    }

    /**
     * 数据验证示例，针对 UpdateGroup 这一标识组的 bean认证，传入的是表单形式.
     *
     * @param user the user
     * @return the string
     */
    @Deprecated
    @PutMapping(value = "valid-update-dep")
    public String validUpdateDep(@Validated(UpdateGroup.class) User user) {
        return "";
    }

    /**
     * Valid update list.
     *
     * @param user the user
     * @return the list
     */
    @PutMapping(value = "valid-update")
    public List<String> validUpdate(@Validated(UpdateGroup.class) User user) {
        return new ArrayList<>();
    }

    /**
     * Valid update return user list.
     *
     * @param user the user
     * @return the list
     */
    @PutMapping(value = "valid-update-return-user-list")
    public List<User> validUpdateReturnUserList(@Validated(UpdateGroup.class) User user) {
        return new ArrayList<>();
    }

    /**
     * Valid update return user map.
     *
     * @param user the user
     * @return the list
     */
    @PutMapping(value = "valid-update-return-user-map")
    public List<Map<String, User>> validUpdateReturnUserMap(@Validated(UpdateGroup.class) User user) {
        return new ArrayList<>();
    }

    /**
     * 数据验证示例，URL认证.
     *
     * @param age the age
     * @return the resp
     */
    @GetMapping(value = "valid-method/{age}")
    public Resp<User> validInMethod(@Min(value = 2, message = "age必须大于2") @PathVariable("age") int age) {
        return Resp.success(null);
    }

    /**
     * User.
     */
    @Schema(title = "用户信息")
    public static class User {

        // 仅在CreateGroup组下才校验
        @NotNull(groups = CreateGroup.class)
        @IdNumber(groups = CreateGroup.class)
        private String idCard;

        // CreateGroup、UpdateGroup组下校验
        @Min(value = 10, groups = {CreateGroup.class, UpdateGroup.class})
        private int age;

        // CreateGroup、UpdateGroup组下校验
        @Phone(groups = {CreateGroup.class, UpdateGroup.class})
        private String phone;

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
        public int getAge() {
            return age;
        }

        /**
         * Sets age.
         *
         * @param age the age
         */
        public void setAge(int age) {
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
    }

}
