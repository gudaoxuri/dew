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

package ms.dew.core.auth;

import com.ecfront.dew.common.$;
import ms.dew.Dew;
import ms.dew.core.DewContext;
import ms.dew.core.auth.dto.OptInfo;

import java.util.Optional;


public class BasicAuthAdapter implements AuthAdapter {

    // token存储key
    private static final String TOKEN_INFO_FLAG = "dew:auth:token:info:";
    // Token Id 关联 key : dew:auth:token:id:rel:<code> value : <token Id>
    private static final String TOKEN_ID_REL_FLAG = "dew:auth:token:id:rel:";

    @Override
    public <E extends OptInfo> Optional<E> getOptInfo(String token) {
        String optInfoStr = Dew.cluster.cache.get(TOKEN_INFO_FLAG + token);
        if (optInfoStr != null && !optInfoStr.isEmpty()) {
            return Optional.of($.json.toObject(optInfoStr, DewContext.getOptInfoClazz()));
        } else {
            return Optional.empty();
        }
    }

    @Override
    public void removeOptInfo(String token) {
        Optional<OptInfo> tokenInfoOpt = getOptInfo(token);
        if (tokenInfoOpt.isPresent()) {
            Dew.cluster.cache.del(TOKEN_ID_REL_FLAG + tokenInfoOpt.get().getAccountCode());
            Dew.cluster.cache.del(TOKEN_INFO_FLAG + token);
        }
    }

    @Override
    public <E extends OptInfo> void setOptInfo(E optInfo) {
        Dew.cluster.cache.del(TOKEN_INFO_FLAG + Dew.cluster.cache.get(TOKEN_ID_REL_FLAG + optInfo.getAccountCode()));
        Dew.cluster.cache.set(TOKEN_ID_REL_FLAG + optInfo.getAccountCode(), optInfo.getToken());
        Dew.cluster.cache.set(TOKEN_INFO_FLAG + optInfo.getToken(), $.json.toJsonString(optInfo));
    }

}
