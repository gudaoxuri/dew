package com.ecfront.dew.gateway.filter;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.ecfront.dew.Dew;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;

public class LoggingFilter extends ZuulFilter {

    private static final Logger logger = LoggerFactory.getLogger(LoggingFilter.class);

    @Override
    public String filterType() {
        return "pre";
    }

    @Override
    public int filterOrder() {
        return 0;
    }

    @Override
    public boolean shouldFilter() {
        return true;
    }

    @Override
    public Object run() {
        RequestContext ctx = RequestContext.getCurrentContext();
        HttpServletRequest request = ctx.getRequest();
        String ip = Dew.Util.getRealIP(request);
        String requestPath = request.getRequestURI();
        logger.trace("[{}] {}{} from {}", request.getMethod(), requestPath, request.getQueryString() == null ? "" : "?" + request.getQueryString(), ip);
        return null;
    }

}
