package com.tairanchina.csp.dew.example.auth;

import com.ecfront.dew.common.$;
import com.ecfront.dew.common.Resp;
import com.tairanchina.csp.dew.Dew;
import com.tairanchina.csp.dew.core.web.error.ErrorController;
import com.tairanchina.csp.foundation.sdk.CSPKernelSDK;
import com.tairanchina.csp.foundation.sdk.SDKKernelConfig;
import com.tairanchina.csp.foundation.sdk.dto.APIAuthResponse;
import com.tairanchina.csp.foundation.sdk.dto.TokenDeliverDTO;
import com.tairanchina.csp.foundation.sdk.enumeration.TokenType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.security.auth.message.AuthException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created on 2019/3/7.
 *
 * @author 迹_Jason
 */
@Component
public class AuthFilterHandleIntercepter extends HandlerInterceptorAdapter {
    private static final Logger logger = LoggerFactory.getLogger(AuthFilterHandleIntercepter.class);
    private static final String APP_ID = "X-App-Id";
    private static final String USER_ID = "X-User-Id";
    private static final String ORIGIN_FROM = "X-Origin-From";

    /**
     * 对于这个API权限的角色集合
     */
    private static final String ROLE_FLAG = "X-ROLES";

    @Autowired
    private SDKKernelConfig config;

    @Autowired
    private CSPKernelSDK sdk;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        final String requestUri = request.getRequestURI();
        final String requestMethod = request.getMethod();
        if (logger.isDebugEnabled()) {
            logger.debug("The current requestUri is {} and the method is {}", request.getRequestURI(), requestMethod);
        }
        logger.trace("Start Back List Validator");
        if (!sdk.auth.needAuthByList(requestMethod, requestUri)) {
            return true;
        }
        logger.trace("Start Validate Token");
        Resp<TokenDeliverDTO> resp = validateToken(request.getHeader(Dew.dewConfig.getSecurity().getTokenFlag()), requestUri,request);
        if (!resp.ok()) {
            logger.trace("Token Validate Was Failure.{}", request.getHeader(Dew.dewConfig.getSecurity().getTokenFlag()));
            ErrorController.error(request, response, 401, String.format("The current[%S][%s] request is not allowed", requestUri, requestMethod), AuthException.class.getName());
            return false;
        } else {
            TokenDeliverDTO tokenDeliver = resp.getBody();
            // client场景下，对URL的权限校验,partyId在client场景是返回空的
            logger.trace("tokenDeliver:{}", $.json.toJsonString(tokenDeliver));
            if (!tokenDeliver.getUserId().isEmpty()) {
                logger.trace("Start API Validate.{}", $.json.toJsonString(tokenDeliver));
                // API权限校验
                Resp<String> rolesR = validateAPIAuth(tokenDeliver, requestMethod, requestUri);
                if (!rolesR.ok()) {
                    logger.trace("Start API Failure");
                    ErrorController.error(request, response, 403, "No access request API", AuthException.class.getName());
                    return false;
                }
                logger.trace("End API Validate");
                if (logger.isDebugEnabled()) {
                    logger.debug("The current request role is {}", rolesR.getBody());
                }
                request.setAttribute(ROLE_FLAG, rolesR.getBody());
            }
        }
        logger.trace("End Validate");
        return true;
    }

    private Resp<TokenDeliverDTO> validateToken(String rawToken, String requestUri, HttpServletRequest request) {
        Resp<TokenDeliverDTO> result = sdk.user.tenantValidate(rawToken, requestUri, config);
        logger.trace("validate Token result:{}", $.json.toJsonString(result));
        if (!result.ok()) {
            logger.warn("The token validate failure");
            return Resp.error(result);
        } else {
            TokenDeliverDTO heads = result.getBody();
            request.setAttribute(APP_ID, heads.getAppId());
            request.setAttribute(USER_ID, heads.getUserId());
            request.setAttribute(ORIGIN_FROM, heads.getOriginFrom());
            if (logger.isDebugEnabled()) {
                logger.debug("appId:{}-userId:{}-originFrom:{}", heads.getAppId(), heads.getUserId(), heads.getOriginFrom());
            }
            // 非client Token 设为空
            if (!TokenType.CLIENT.name().equalsIgnoreCase(heads.getOriginFrom())) {
                result.getBody().setUserId("");
            }
            logger.trace("Validate Token End");
            return result;

        }
    }

    private Resp<String> validateAPIAuth(TokenDeliverDTO tokenDeliver, String method, String requestUri) {
        Resp<APIAuthResponse> authResult = sdk.auth.validateAPIAuth(tokenDeliver, requestUri, method);
        logger.trace("API Result:{}", $.json.toJsonString(authResult));
        if (authResult.ok() && authResult.getBody().isAuth()) {
            // 当前用户有对应API的权限
            return Resp.success(authResult.getBody().getRoleCode());
        } else {
            return Resp.forbidden(String.format("Account [%s] no access to %s:%s", tokenDeliver.getUserId(), method, requestUri));
        }
    }
}
