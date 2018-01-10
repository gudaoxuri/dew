package com.ecfront.dew.core.auth;

import com.ecfront.dew.Dew;
import com.ecfront.dew.core.dto.OptInfo;
import com.ecfront.dew.Dew;
import com.ecfront.dew.core.dto.OptInfo;

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
