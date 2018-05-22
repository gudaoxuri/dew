package com.tairanchina.csp.dew.auth.sdk;

import com.tairanchina.csp.dew.Dew;
import com.tairanchina.csp.dew.core.dto.OptInfo;

import java.util.Optional;

public class AuthSDKAdapter implements com.tairanchina.csp.dew.core.auth.AuthAdapter {

    @Override
    public <E extends OptInfo> Optional<E> getOptInfo(String token) {
        return Dew.context().optInfo();
    }

    @Override
    public void removeOptInfo(String token) {
        Dew.context().setInnerOptInfo(Optional.empty());
    }

    @Override
    public <E extends OptInfo> void setOptInfo(E optInfo) {
        Dew.context().setInnerOptInfo(Optional.of(optInfo));
    }
}
