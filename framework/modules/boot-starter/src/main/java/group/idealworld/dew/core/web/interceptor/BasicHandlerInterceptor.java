package group.idealworld.dew.core.web.interceptor;

import group.idealworld.dew.Dew;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.AsyncHandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * 基础拦截器.
 *
 * @author gudaoxuri
 */
public class BasicHandlerInterceptor implements AsyncHandlerInterceptor {

    private static final Logger LOGGER = LoggerFactory.getLogger(BasicHandlerInterceptor.class);

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
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
            return true;
        }

        LOGGER.trace("[{}] {}{} from {}",
                request.getMethod(),
                request.getRequestURI(),
                request.getQueryString() == null ? "" : "?" + request.getQueryString(), Dew.Util.getRealIP(request));

        return true;
    }

}
