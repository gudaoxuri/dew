/*
 * Copyright 2020. the original author or authors.
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

package group.idealworld.dew.core.auth.dto;


import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

/**
 * 基础操作用户信息.
 *
 * @param <E> 扩展操作用户信息类型
 * @author gudaoxuri
 */
@Schema(name = "操作用户信息")
public class BasicOptInfo<E> extends OptInfo<E> {

    /**
     * The Mobile.
     */
    @Schema(name = "手机号", required = true)
    protected String mobile;
    /**
     * The Email.
     */
    @Schema(name = "邮箱", required = true)
    protected String email;
    /**
     * The Name.
     */
    @Schema(name = "姓名", required = true)
    protected String name;

    /**
     * The Last login time.
     */
    @Schema(name = "最后一次登录时间", required = true)
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

}
