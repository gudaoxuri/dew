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

package ms.dew.core.web.interceptor;

import com.ecfront.dew.common.$;
import com.ecfront.dew.common.StandardCode;
import ms.dew.Dew;
import ms.dew.core.DewContext;
import ms.dew.core.auth.dto.OptInfo;
import ms.dew.core.web.error.ErrorController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpMethod;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.security.auth.message.AuthException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.URLDecoder;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Dew Servlet拦截器.
 *
 * @author gudaoxuri
 * @author gjason
 */
public class BasicHandlerInterceptor extends HandlerInterceptorAdapter {

    private static final Logger logger = LoggerFactory.getLogger(BasicHandlerInterceptor.class);

    private static final String URL_SPLIT = "@";

    private AntPathMatcher pathMatcher = new AntPathMatcher();

    // [method@uri]
    private Set<String> blackUris = new HashSet<>();
    // method@uri -> [roleIds]
    private Map<String, Set<String>> roleAuth = new HashMap<>();

    BasicHandlerInterceptor() {
        if (Dew.dewConfig.getSecurity().getRouter().isEnabled()) {
            blackUris = formatUris(Dew.dewConfig.getSecurity().getRouter().getBlackUri());
            Dew.dewConfig.getSecurity().getRouter().getRoleAuth()
                    .forEach((role, uris) -> formatUris(uris).forEach(uri -> {
                        if (!roleAuth.containsKey(uri)) {
                            roleAuth.put(uri, new HashSet<>());
                        }
                        roleAuth.get(uri).add(role);
                    }));
        }
    }

    private Set<String> formatUris(Map<String, List<String>> uris) {
        Set<String> formattedUris = uris.entrySet().stream().filter(entry -> !entry.getKey().equalsIgnoreCase("all"))
                .flatMap(entry -> entry.getValue().stream().map(uri -> entry.getKey().toLowerCase() + URL_SPLIT + uri))
                .collect(Collectors.toSet());
        uris.entrySet().stream().filter(entry -> entry.getKey().equalsIgnoreCase("all"))
                .flatMap(entry -> entry.getValue().stream())
                .forEach(uri -> {
                    formattedUris.add("get" + URL_SPLIT + uri);
                    formattedUris.add("post" + URL_SPLIT + uri);
                    formattedUris.add("put" + URL_SPLIT + uri);
                    formattedUris.add("delete" + URL_SPLIT + uri);
                });
        return formattedUris;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 配置跨域参数
        response.addHeader("Access-Control-Allow-Origin", Dew.dewConfig.getSecurity().getCors().getAllowOrigin());
        response.addHeader("Access-Control-Allow-Methods", Dew.dewConfig.getSecurity().getCors().getAllowMethods());
        response.addHeader("Access-Control-Allow-Headers", Dew.dewConfig.getSecurity().getCors().getAllowHeaders());
        response.addHeader("Access-Control-Max-Age", "3600000");
        response.addHeader("Access-Control-Allow-Credentials", "true");
        response.setHeader("Cache-Control", "no-cache");
        response.setHeader("Cache-Control", "no-store");
        response.setHeader("Pragma", "no-cache");
        response.setDateHeader("Expires", 0);

        if (request.getMethod().equalsIgnoreCase("OPTIONS") || request.getMethod().equalsIgnoreCase("HEAD")) {
            return super.preHandle(request, response, handler);
        }

        String token;
        String tokenKind;
        if (Dew.dewConfig.getSecurity().isTokenInHeader()) {
            token = request.getHeader(Dew.dewConfig.getSecurity().getTokenFlag());
            tokenKind = request.getHeader(Dew.dewConfig.getSecurity().getTokenKindFlag());
        } else {
            token = request.getParameter(Dew.dewConfig.getSecurity().getTokenFlag());
            tokenKind = request.getParameter(Dew.dewConfig.getSecurity().getTokenKindFlag());
        }
        if (token != null) {
            token = URLDecoder.decode(token, "UTF-8");
            if (Dew.dewConfig.getSecurity().isTokenHash()) {
                token = $.security.digest.digest(token, "MD5");
            }
        }
        if (tokenKind == null) {
            tokenKind = OptInfo.DEFAULT_TOKEN_KIND_FLAG;
        }
        // 请求黑名单拦截
        if (Dew.dewConfig.getSecurity().getRouter().isEnabled()) {
            // 兼容requestUri末尾包含/的情况
            String method = request.getMethod().toLowerCase();
            if (method.equalsIgnoreCase(HttpMethod.OPTIONS.name())) {
                return false;
            }
            // 兼容requestUri末尾包含/的情况
            final String reqUri = method + URL_SPLIT + request.getRequestURI().replaceAll("/+$", "");
            // 黑名单处理
            if (blackUris.stream().anyMatch(uri -> pathMatcher.match(uri, reqUri))) {
                ErrorController.error(request, response, Integer.parseInt(StandardCode.FORBIDDEN.toString()),
                        String.format("The current [%s][%s] request is not allowed",
                                request.getMethod(), request.getRequestURI()), AuthException.class.getName());
                return false;
            }

            if (!roleAuth.isEmpty()) {
                Set<String> needRoles = roleAuth.entrySet().stream()
                        .filter(entry -> pathMatcher.match(entry.getKey(), reqUri))
                        .flatMap(entry -> entry.getValue().stream())
                        .collect(Collectors.toSet());
                if (!needRoles.isEmpty() && (StringUtils.isEmpty(token) || Dew.auth.getOptInfo(token)
                        .map(opt ->
                                ((Set<OptInfo.RoleInfo>) opt.getRoleInfo()).stream()
                                        .map(role -> {
                                            if (StringUtils.isEmpty(role.getTenantCode())) {
                                                return role.getCode();
                                            } else {
                                                return role.getTenantCode() + "." + role.getCode();
                                            }
                                        })
                                        .noneMatch(needRoles::contains))
                        .orElse(true))) {
                    ErrorController.error(request, response, Integer.parseInt(StandardCode.UNAUTHORIZED.toString()),
                            String.format("The current[%s][%s] request role is not allowed",
                                    request.getMethod(), request.getRequestURI()), AuthException.class.getName());
                    return false;
                }
            }
        }
        DewContext context = new DewContext();
        context.setId($.field.createUUID());
        context.setSourceIP(Dew.Util.getRealIP(request));
        context.setRequestUri(request.getRequestURI());
        context.setToken(token);
        context.setTokenKind(tokenKind);
        DewContext.setContext(context);

        logger.trace("[{}] {}{} from {}",
                request.getMethod(),
                request.getRequestURI(),
                request.getQueryString() == null ? "" : "?" + request.getQueryString(), Dew.context().getSourceIP());
        return super.preHandle(request, response, handler);
    }

}
