package com.ecfront.dew.wsgateway.auth;

import com.ecfront.dew.common.JsonHelper;
import com.ecfront.dew.common.Resp;
import com.ecfront.dew.core.Dew;
import com.ecfront.dew.core.dto.OptInfo;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;

public class AuthFilter extends ZuulFilter {

    private static final Logger logger = LoggerFactory.getLogger(AuthFilter.class);

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

    private Void filterHit(RequestContext ctx, Resp<?> resp) {
        ctx.setSendZuulResponse(false);
        ctx.setResponseBody(JsonHelper.toJsonString(resp));
        logger.warn("Auth Filter Hit [" + resp.getCode() + "] " + resp.getMessage());
        return null;
    }

    @Override
    public Object run() {
        RequestContext ctx = RequestContext.getCurrentContext();
        HttpServletRequest request = ctx.getRequest();
        ctx.getResponse().setCharacterEncoding("UTF-8");
        String ip = Dew.Util.getRealIP(request);
        logger.info("[" + request.getMethod() + "] " + request.getRequestURL().toString() + " from " + ip);

        return null;
        /*Resp<LocalCacheContainer.MatchInfo> routerR = LocalCacheContainer.getRouter(request.getMethod(), request.getRequestURI(), ip);
        if (!routerR.ok()) {
            return filterHit(ctx, routerR);
        }

        if (routerR.getBody() == null) {
            // 可匿名访问
            return null;
        }
        try {
            String token = request.getParameter(Dew.VIEW_TOKEN_FLAG);
            if (token == null) {
                return filterHit(ctx, Resp.unAuthorized("【token】not exist，Request parameter must include【" + Dew.VIEW_TOKEN_FLAG + "】"));
            }
            // 根据token获取EZ_Token_Info
            String optInfoStr = Dew.redis.opsForValue().get(Dew.TOKEN_INFO_FLAG + token);
            if (optInfoStr == null) {
                return filterHit(ctx, Resp.unAuthorized("Token NOT exist"));
            }
            OptInfo optInfo = JsonHelper.toObject(optInfoStr, OptInfo.class);
            // 此资源需要认证
            if (!LocalCacheContainer.existOrganization(optInfo.getOrganizationCode())) {
                return filterHit(ctx, Resp.unAuthorized("Organization【" + optInfo.getOrganizationCode() + "】 not found"));
            }
            // 用户所属组织状态正常
            if (!routerR.getBody().getRoleCode().isEmpty() && routerR.getBody().getRoleCode().stream().noneMatch(i -> optInfo.getRoleCodes().contains(i))) {
                // 登录用户所属角色列表中不存在此资源
                return filterHit(ctx, Resp.unAuthorized("Account【" + optInfo.getName() + "】in【" + optInfo.getOrganizationCode() + "】no access to " + request.getMethod() + ":" + request.getRequestURI()));
            }
            return null;
        } catch (Exception e) {
            return filterHit(ctx, Resp.unAuthorized("Service error:" + e.getMessage()));
        }*/
    }

}
