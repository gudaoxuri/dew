package group.idealworld.dew.core.web.interceptor;

import com.ecfront.dew.common.$;
import com.ecfront.dew.common.StandardCode;
import group.idealworld.dew.Dew;
import group.idealworld.dew.core.DewContext;
import group.idealworld.dew.core.auth.dto.OptInfo;
import group.idealworld.dew.core.web.error.ErrorController;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.AsyncHandlerInterceptor;

import jakarta.security.auth.message.AuthException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Optional;

/**
 * IdentInfo拦截器.
 *
 * @author gudaoxuri
 * @author gjason
 */
public class IdentInfoHandlerInterceptor implements AsyncHandlerInterceptor {

    private static final Logger LOGGER = LoggerFactory.getLogger(IdentInfoHandlerInterceptor.class);

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws IOException {
        if (Dew.dewConfig.getSecurity().isIdentInfoEnabled()
                && StringUtils.isNotEmpty(Dew.dewConfig.getSecurity().getUnIdentUrls())) {
            var isUnIdentUrl = Arrays.stream(Dew.dewConfig.getSecurity().getUnIdentUrls().split(","))
                    .anyMatch(url -> url.equals(request.getRequestURI()));
            if (isUnIdentUrl) {
                DewContext context = new DewContext();
                context.setId($.field.createUUID());
                context.setSourceIP(Dew.Util.getRealIP(request));
                context.setRequestUri(request.getRequestURI());
                context.setInnerOptInfo(Optional.of(new OptInfo()));
                DewContext.setContext(context);
                return true;
            }
        }
        if (request.getHeader(Dew.dewConfig.getSecurity().getIdentInfoFlag()) == null) {
            ErrorController.error(request, response, Integer.parseInt(StandardCode.BAD_REQUEST.toString()),
                    "The request is missing [" + Dew.dewConfig.getSecurity().getIdentInfoFlag() + "] in header",
                    AuthException.class.getName());
            return false;
        }
        var optInfo = $.json.toObject(
                $.security.decodeBase64ToString(request.getHeader(Dew.dewConfig.getSecurity().getIdentInfoFlag()),
                        StandardCharsets.UTF_8),
                DewContext.getOptInfoClazz());
        var optInfoOpt = Optional.of(optInfo);
        var token = optInfo.getToken();
        var tokenKind = optInfo.getTokenKind();
        DewContext context = new DewContext();
        context.setId($.field.createUUID());
        context.setSourceIP(Dew.Util.getRealIP(request));
        context.setRequestUri(request.getRequestURI());
        context.setToken(token);
        context.setInnerOptInfo(optInfoOpt);
        context.setTokenKind(tokenKind);
        DewContext.setContext(context);
        return true;
    }

}
