package com.tairanchina.csp.dew.auth.sdk;

import com.ecfront.dew.common.$;
import com.ecfront.dew.common.Resp;
import com.tairanchina.csp.dew.Dew;
import com.tairanchina.csp.dew.core.web.error.ErrorController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.security.auth.message.AuthException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;

@Component
public class AuthSDKHandlerInterceptor extends HandlerInterceptorAdapter {

    @Autowired
    private AuthSDKConfig authSdkConfig;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String uri = request.getMethod() + "@" + "/" + Dew.Info.name + request.getRequestURI();
        if (authSdkConfig.getWhiteList().stream().anyMatch(uri::startsWith)) {
            return super.preHandle(request, response, handler);
        }
        String accessToken = request.getHeader(AuthSDKConfig.HTTP_ACCESS_TOKEN);
        if (accessToken == null || accessToken.isEmpty()) {
            ErrorController.error(request, response, 401, "缺少AccessToken", AuthException.class.getName());
            return false;
        }
        String token = request.getHeader(AuthSDKConfig.HTTP_USER_TOKEN);
        String authResult = $.http.get(authSdkConfig.getServerUrl() + "/basic/auth/validate", new HashMap<String, String>() {{
            put(AuthSDKConfig.HTTP_URI, uri);
            put(AuthSDKConfig.HTTP_ACCESS_TOKEN, accessToken);
            put(AuthSDKConfig.HTTP_USER_TOKEN, token == null ? "" : token);
        }});
        Resp<TokenInfo> tokenInfoR = Resp.generic(authResult, TokenInfo.class);
        if (tokenInfoR.ok()) {
            Dew.auth.setOptInfo(tokenInfoR.getBody());
            return super.preHandle(request, response, handler);
        } else {
            ErrorController.error(request, response, 401, tokenInfoR.getMessage(), AuthException.class.getName());
            return false;
        }
    }

}
