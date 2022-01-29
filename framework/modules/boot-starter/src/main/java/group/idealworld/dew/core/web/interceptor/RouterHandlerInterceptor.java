/*
 * Copyright 2022. the original author or authors
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

import com.ecfront.dew.common.StandardCode;
import group.idealworld.dew.Dew;
import group.idealworld.dew.core.DewContext;
import group.idealworld.dew.core.web.error.ErrorController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpMethod;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.ObjectUtils;
import org.springframework.web.servlet.AsyncHandlerInterceptor;

import javax.security.auth.message.AuthException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Token拦截器.
 *
 * @author gudaoxuri
 * @author gjason
 */
public class RouterHandlerInterceptor implements AsyncHandlerInterceptor {

    private static final Logger logger = LoggerFactory.getLogger(RouterHandlerInterceptor.class);

    private static final String URL_SPLIT = "@";

    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    // [method@uri]
    private static Set<String> BLOCK_URIS = new HashSet<>();
    // method@uri -> [roleIds]
    private static Map<String, Set<String>> ROLE_AUTH = new HashMap<>();

    RouterHandlerInterceptor() {
        fillAuthInfo(Dew.dewConfig.getSecurity().getRouter().getBlockUri(),
                Dew.dewConfig.getSecurity().getRouter().getRoleAuth());
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
            boolean pass = ROLE_AUTH.keySet().stream()
                    .filter(strings -> pathMatcher.matchStart(strings, reqUri))
                    .min(pathMatcher.getPatternComparator(reqUri))
                    .map(matchedUri -> {
                        // 找到需要鉴权的URI
                        if (ObjectUtils.isEmpty(DewContext.getContext().getToken())) {
                            // Token不存在
                            return false;
                        }
                        Set<String> needRoles = ROLE_AUTH.get(matchedUri);
                        return (Dew.dewConfig.getSecurity().isIdentInfoEnabled()
                                ? DewContext.getContext().optInfo()
                                : Dew.auth.getOptInfo(DewContext.getContext().getToken()))
                                .map(opt ->
                                        opt.getRoles() != null && Arrays.stream(opt.getRoles()).anyMatch(needRoles::contains))
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
        return true;
    }

}
