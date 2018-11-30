package com.tairanchina.csp.dew.core.auth;

import com.tairanchina.csp.dew.Dew;
import com.tairanchina.csp.dew.core.auth.dto.OptInfo;

import java.util.Optional;

public interface AuthAdapter {

    default <E extends OptInfo> Optional<E> getOptInfo() {
        return Dew.context().optInfo();
    }

    <E extends OptInfo> Optional<E> getOptInfo(String token);

    default void removeOptInfo() {
        Optional<OptInfo> tokenInfoOpt = getOptInfo();
        if (tokenInfoOpt.isPresent()) {
            removeOptInfo(tokenInfoOpt.get().getToken());
        }
    }

    void removeOptInfo(String token);

    <E extends OptInfo> void setOptInfo(E optInfo);
}
