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

@ApiModel(value = "登录信息")
public class OptInfo<E> {

    @ApiModelProperty(value = "Token", required = true)
    protected String token;
    @ApiModelProperty(value = "账号编码", required = true)
    protected Object accountCode;

    public String getToken() {
        return token;
    }

    public E setToken(String token) {
        this.token = token;
        return (E) this;
    }

    public Object getAccountCode() {
        return accountCode;
    }

    public E setAccountCode(Object accountCode) {
        this.accountCode = accountCode;
        return (E) this;
    }
}
