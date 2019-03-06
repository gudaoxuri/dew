package com.tairanchina.csp.dew.core.auth;

import com.ecfront.dew.common.$;
import com.tairanchina.csp.dew.Dew;
import com.tairanchina.csp.dew.core.DewContext;
import com.tairanchina.csp.dew.core.auth.dto.OptInfo;

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
