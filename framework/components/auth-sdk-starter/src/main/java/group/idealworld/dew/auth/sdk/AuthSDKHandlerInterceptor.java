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

package group.idealworld.dew.auth.sdk;

import com.ecfront.dew.common.$;
import com.ecfront.dew.common.Resp;
import group.idealworld.dew.Dew;
import group.idealworld.dew.core.web.error.ErrorController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.security.auth.message.AuthException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;

/**
 * Auth sdk handler interceptor.
 *
 * @author gudaoxuri
 */
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
        String authResult = $.http.get(authSdkConfig.getServerUrl() + "/basic/auth/validate", new HashMap<String, String>() {
            {
                put(AuthSDKConfig.HTTP_URI, uri);
                put(AuthSDKConfig.HTTP_ACCESS_TOKEN, accessToken);
                put(AuthSDKConfig.HTTP_USER_TOKEN, token == null ? "" : token);
            }
        });
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
