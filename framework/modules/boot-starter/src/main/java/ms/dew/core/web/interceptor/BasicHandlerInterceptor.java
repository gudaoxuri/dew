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
import ms.dew.Dew;
import ms.dew.core.DewContext;
import ms.dew.core.web.error.ErrorController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpMethod;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.security.auth.message.AuthException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;

/**
 * Dew Servlet拦截器.
 *
 * @author gudaoxuri
 * @author gjason
 */
public class BasicHandlerInterceptor extends HandlerInterceptorAdapter {

    private static final Logger logger = LoggerFactory.getLogger(BasicHandlerInterceptor.class);
    private AntPathMatcher pathMatcher = new AntPathMatcher();

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
        if (Dew.dewConfig.getSecurity().isTokenInHeader()) {
            token = request.getHeader(Dew.dewConfig.getSecurity().getTokenFlag());
        } else {
            token = request.getParameter(Dew.dewConfig.getSecurity().getTokenFlag());
        }
        if (token != null) {
            token = URLDecoder.decode(token, "UTF-8");
            if (Dew.dewConfig.getSecurity().isTokenHash()) {
                token = $.security.digest.digest(token, "MD5");
            }
        }
        // 请求黑名单拦截
        if (Dew.dewConfig.getSecurity().getRouter().isEnabled()
                && blackRequest(request.getMethod(), request.getRequestURI())) {
            ErrorController.error(request, response, 403,
                    String.format("The current[%S][%s] request is not allowed",
                            request.getRequestURI(), request.getMethod()), AuthException.class.getName());
            return false;
        }
        DewContext context = new DewContext();
        context.setId($.field.createUUID());
        context.setSourceIP(Dew.Util.getRealIP(request));
        context.setRequestUri(request.getRequestURI());
        context.setToken(token);
        DewContext.setContext(context);

        logger.trace("[{}] {}{} from {}",
                request.getMethod(),
                request.getRequestURI(),
                request.getQueryString() == null ? "" : "?" + request.getQueryString(), Dew.context().getSourceIP());
        return super.preHandle(request, response, handler);
    }

    /**
     * 黑名单.
     *
     * @param method     Request method
     * @param requestUri URL
     */
    public boolean blackRequest(String method, String requestUri) {

        /*
          兼容requestUri末尾包含/的情况
         */
        final String reqUri = requestUri.replaceAll("/+$", "");
        method = method.toLowerCase();
        if (method.equalsIgnoreCase(HttpMethod.OPTIONS.name())) {
            return false;
        }
        final List<String> blacks = Dew.dewConfig.getSecurity().getRouter().getBlackUri().getOrDefault(method, new ArrayList<>());
        if (logger.isDebugEnabled()) {
            logger.debug("the black apis are {}", $.json.toJsonString(blacks));
        }
        return blacks.stream().anyMatch(uri -> pathMatcher.match(uri, reqUri));
    }
}
