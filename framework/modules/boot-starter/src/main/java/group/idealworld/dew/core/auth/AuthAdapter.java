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

package group.idealworld.dew.core.auth;

import group.idealworld.dew.Dew;
import group.idealworld.dew.core.auth.dto.OptInfo;

import java.util.Optional;

/**
 * 登录鉴权适配器.
 *
 * @author gudaoxuri
 */
public interface AuthAdapter {

    /**
     * 获取当前登录的操作用户信息.
     *
     * @param <E> 扩展操作用户信息类型
     * @return 操作用户信息
     */
    default <E extends OptInfo> Optional<E> getOptInfo() {
        return Dew.context().optInfo();
    }

    /**
     * 根据Token获取操作用户信息.
     *
     * @param <E>   扩展操作用户信息类型
     * @param token 登录Token
     * @return 操作用户信息
     */
    <E extends OptInfo> Optional<E> getOptInfo(String token);

    /**
     * 设置操作用户信息类型.
     *
     * @param <E>     扩展操作用户信息类型
     * @param optInfo 扩展操作用户信息
     */
    <E extends OptInfo> void setOptInfo(E optInfo);

    /**
     * 删除当前登录的操作用户信息.
     * <p>
     * 对指注销登录
     */
    default void removeOptInfo() {
        getOptInfo().ifPresent(optInfo -> removeOptInfo(optInfo.getToken()));
    }

    /**
     * 根据Token删除操作用户信息.
     *
     * @param token 登录Token
     */
    void removeOptInfo(String token);
}
