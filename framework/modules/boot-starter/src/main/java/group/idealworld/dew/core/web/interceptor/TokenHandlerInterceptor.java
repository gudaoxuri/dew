package group.idealworld.dew.core.web.interceptor;

import com.ecfront.dew.common.$;
import group.idealworld.dew.Dew;
import group.idealworld.dew.core.DewContext;
import group.idealworld.dew.core.auth.dto.OptInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.AsyncHandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

/**
 * Token拦截器.
 *
 * @author gudaoxuri
 */
public class TokenHandlerInterceptor implements AsyncHandlerInterceptor {

    private static final Logger LOGGER = LoggerFactory.getLogger(TokenHandlerInterceptor.class);

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
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
        DewContext context = new DewContext();
        context.setId($.field.createUUID());
        context.setSourceIP(Dew.Util.getRealIP(request));
        context.setRequestUri(request.getRequestURI());
        context.setToken(token);
        context.setTokenKind(tokenKind);
        DewContext.setContext(context);
        return true;
    }

}
