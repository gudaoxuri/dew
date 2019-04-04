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

package ms.dew.core.auth.dto;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 基础操作用户信息.
 *
 * @param <E> 扩展操作用户信息类型
 * @author gudaoxuri
 */
@ApiModel(value = "操作用户信息")
public class BasicOptInfo<E> extends OptInfo<E> {

    /**
     * The Mobile.
     */
    @ApiModelProperty(value = "手机号", required = true)
    protected String mobile;
    /**
     * The Email.
     */
    @ApiModelProperty(value = "邮箱", required = true)
    protected String email;
    /**
     * The Name.
     */
    @ApiModelProperty(value = "姓名", required = true)
    protected String name;
    /**
     * The Roles.
     */
    @ApiModelProperty(value = "角色列表", required = true)
    protected List<RoleInfo> roles;
    /**
     * The Last login time.
     */
    @ApiModelProperty(value = "最后一次登录时间", required = true)
    protected LocalDateTime lastLoginTime;

    /**
     * Gets mobile.
     *
     * @return the mobile
     */
    public String getMobile() {
        return mobile;
    }

    /**
     * Sets mobile.
     *
     * @param mobile the mobile
     * @return the mobile
     */
    public E setMobile(String mobile) {
        this.mobile = mobile;
        return (E) this;
    }

    /**
     * Gets email.
     *
     * @return the email
     */
    public String getEmail() {
        return email;
    }

    /**
     * Sets email.
     *
     * @param email the email
     * @return the email
     */
    public E setEmail(String email) {
        this.email = email;
        return (E) this;
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
     * @return the name
     */
    public E setName(String name) {
        this.name = name;
        return (E) this;
    }

    /**
     * Gets roles.
     *
     * @return the roles
     */
    public List<RoleInfo> getRoles() {
        return roles;
    }

    /**
     * Sets roles.
     *
     * @param roles the roles
     * @return the roles
     */
    public E setRoles(List<RoleInfo> roles) {
        this.roles = roles;
        return (E) this;
    }

    /**
     * Gets last login time.
     *
     * @return the last login time
     */
    public LocalDateTime getLastLoginTime() {
        return lastLoginTime;
    }

    /**
     * Sets last login time.
     *
     * @param lastLoginTime the last login time
     * @return the last login time
     */
    public E setLastLoginTime(LocalDateTime lastLoginTime) {
        this.lastLoginTime = lastLoginTime;
        return (E) this;
    }

    /**
     * Role info.
     */
    @ApiModel(value = "角色信息")
    public static class RoleInfo {
        @ApiModelProperty(value = "角色编码", required = true)
        private String code;
        @ApiModelProperty(value = "角色显示名称", required = true)
        private String name;
        @ApiModelProperty(value = "租户编码", required = true)
        private String tenantCode;

        /**
         * Gets code.
         *
         * @return the code
         */
        public String getCode() {
            return code;
        }

        /**
         * Sets code.
         *
         * @param code the code
         * @return the code
         */
        public RoleInfo setCode(String code) {
            this.code = code;
            return this;
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
         * @return the name
         */
        public RoleInfo setName(String name) {
            this.name = name;
            return this;
        }

        /**
         * Gets tenant code.
         *
         * @return the tenant code
         */
        public String getTenantCode() {
            return tenantCode;
        }

        /**
         * Sets tenant code.
         *
         * @param tenantCode the tenant code
         * @return the tenant code
         */
        public RoleInfo setTenantCode(String tenantCode) {
            this.tenantCode = tenantCode;
            return this;
        }
    }

}
