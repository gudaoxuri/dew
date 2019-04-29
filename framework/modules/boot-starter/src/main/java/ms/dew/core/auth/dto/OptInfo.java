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

/**
 * 操作用户信息.
 *
 * @param <E> 扩展操作用户信息类型
 * @author gudaoxuri
 */
@ApiModel(value = "操作用户信息")
public class OptInfo<E> {

    /**
     * The Token.
     */
    @ApiModelProperty(value = "Token", required = true)
    protected String token;
    /**
     * The Account code.
     */
    @ApiModelProperty(value = "账号编码", required = true)
    protected Object accountCode;
    /**
     * The Token kind.
     */
    @ApiModelProperty(value = "Token类型")
    protected String tokenKind = "";

    /**
     * Gets token.
     *
     * @return the token
     */
    public String getToken() {
        return token;
    }

    /**
     * Sets token.
     *
     * @param token the token
     * @return the token
     */
    public E setToken(String token) {
        this.token = token;
        return (E) this;
    }

    /**
     * Gets account code.
     *
     * @return the account code
     */
    public Object getAccountCode() {
        return accountCode;
    }

    /**
     * Sets account code.
     *
     * @param accountCode the account code
     * @return the account code
     */
    public E setAccountCode(Object accountCode) {
        this.accountCode = accountCode;
        return (E) this;
    }

    /**
     * Gets token kind.
     *
     * @return the token kind
     */
    public String getTokenKind() {
        return tokenKind;
    }

    /**
     * Sets token kind.
     *
     * @param tokenKind the token kind
     */
    public E setTokenKind(String tokenKind) {
        this.tokenKind = tokenKind;
        return (E) this;
    }
}
