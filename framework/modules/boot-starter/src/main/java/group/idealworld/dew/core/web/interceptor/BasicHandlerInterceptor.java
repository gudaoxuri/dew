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

package group.idealworld.dew.core.web.interceptor;

import com.ecfront.dew.common.$;
import com.ecfront.dew.common.StandardCode;
import group.idealworld.dew.Dew;
import group.idealworld.dew.core.DewContext;
import group.idealworld.dew.core.auth.dto.OptInfo;
import group.idealworld.dew.core.web.error.ErrorController;
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
import java.nio.charset.StandardCharsets;
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
    private static Set<String> BLOCK_URIS = new HashSet<>();
    // method@uri -> [roleIds]
    private static Map<String, Set<String>> ROLE_AUTH = new HashMap<>();

    BasicHandlerInterceptor() {
        if (Dew.dewConfig == null) {
            // 未启用web的情况下，Dew加载滞后，忽略
            return;
        }
        if (Dew.dewConfig.getSecurity().getRouter().isEnabled()) {
            fillAuthInfo(Dew.dewConfig.getSecurity().getRouter().getBlockUri(),
                    Dew.dewConfig.getSecurity().getRouter().getRoleAuth());
        }
    }

    /**
     * 填充认证信息.
     *
     * @param blockUris 接口访问阻止单 http method - uris
     * @param roleAuth  接口授权角色 roleName - http method - uris
     */
    public static void fillAuthInfo(Map<String, List<String>> blockUris,
                                    Map<String, Map<String, List<String>>> roleAuth) {
        if (blockUris != null) {
            BLOCK_URIS = formatUris(blockUris);
        }
        if (roleAuth != null) {
            var exchangeRoleAuth = new HashMap<String, Set<String>>();
            roleAuth
                    .forEach((role, uris) -> formatUris(uris).forEach(uri -> {
                        if (!exchangeRoleAuth.containsKey(uri)) {
                            exchangeRoleAuth.put(uri, new HashSet<>());
                        }
                        exchangeRoleAuth.get(uri).add(role);
                    }));
            ROLE_AUTH = exchangeRoleAuth;
        }
    }

    private static Set<String> formatUris(Map<String, List<String>> uris) {
        Set<String> formattedUris = uris.entrySet().stream()
                .filter(entry -> !entry.getKey().equalsIgnoreCase("all")
                        && !entry.getKey().equalsIgnoreCase("*"))
                .flatMap(entry -> entry.getValue().stream()
                        .map(uri -> entry.getKey().toLowerCase() + URL_SPLIT + uri))
                .collect(Collectors.toSet());
        uris.entrySet().stream()
                .filter(entry ->
                        entry.getKey().equalsIgnoreCase("all")
                                || entry.getKey().equalsIgnoreCase("*"))
                .flatMap(entry -> entry.getValue().stream())
                .forEach(uri -> {
                    formattedUris.add("get" + URL_SPLIT + uri);
                    formattedUris.add("post" + URL_SPLIT + uri);
                    formattedUris.add("put" + URL_SPLIT + uri);
                    formattedUris.add("delete" + URL_SPLIT + uri);
                    formattedUris.add("patch" + URL_SPLIT + uri);
                    formattedUris.add("head" + URL_SPLIT + uri);
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
            token = URLDecoder.decode(token, StandardCharsets.UTF_8);
            if (Dew.dewConfig.getSecurity().isTokenHash()) {
                token = $.security.digest.digest(token, "MD5");
            }
        }
        if (tokenKind == null) {
            tokenKind = OptInfo.DEFAULT_TOKEN_KIND_FLAG;
        }
        // 请求阻止名单拦截
        if (Dew.dewConfig.getSecurity().getRouter().isEnabled()) {
            // 兼容requestUri末尾包含/的情况
            String method = request.getMethod().toLowerCase();
            if (method.equalsIgnoreCase(HttpMethod.OPTIONS.name())) {
                return false;
            }
            // 兼容requestUri末尾包含/的情况
            final String reqUri = method + URL_SPLIT + request.getRequestURI().replaceAll("/+$", "");
            // 阻止名单处理
            if (BLOCK_URIS.stream().anyMatch(uri -> pathMatcher.match(uri, reqUri))) {
                ErrorController.error(request, response, Integer.parseInt(StandardCode.FORBIDDEN.toString()),
                        String.format("The current [%s][%s] request is not allowed",
                                request.getMethod(), request.getRequestURI()), AuthException.class.getName());
                return false;
            }
            // 角色权限处理
            if (!ROLE_AUTH.isEmpty()) {
                String finalToken = token;
                boolean pass = ROLE_AUTH.keySet().stream()
                        .filter(strings -> pathMatcher.match(strings, reqUri))
                        .min(pathMatcher.getPatternComparator(reqUri))
                        .map(matchedUri -> {
                            // 找到需要鉴权的URI
                            if (StringUtils.isEmpty(finalToken)) {
                                // Token不存在
                                return false;
                            }
                            Set<String> needRoles = ROLE_AUTH.get(matchedUri);
                            return Dew.auth.getOptInfo(finalToken)
                                    .map(opt ->
                                            opt.getRoleInfo().stream()
                                                    .map(OptInfo.RoleInfo::getCode)
                                                    // 是否找到匹配的角色
                                                    .anyMatch(needRoles::contains))
                                    // Token在缓存中不存在
                                    .orElse(false);
                        })
                        // 该请求URI不需要鉴权
                        .orElse(true);
                if (!pass) {
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
