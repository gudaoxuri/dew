package com.tairanchina.csp.dew.idempotent.interceptor;

import com.tairanchina.csp.dew.core.web.error.ErrorController;
import com.tairanchina.csp.dew.idempotent.DewIdempotent;
import com.tairanchina.csp.dew.idempotent.DewIdempotentConfig;
import com.tairanchina.csp.dew.idempotent.annotations.Idempotent;
import com.tairanchina.csp.dew.idempotent.strategy.StrategyEnum;
import org.springframework.util.StringUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class DewIdempotentHandlerInterceptor extends HandlerInterceptorAdapter {

    private DewIdempotentConfig dewIdempotentConfig;

    public DewIdempotentHandlerInterceptor(DewIdempotentConfig dewIdempotentConfig) {
        this.dewIdempotentConfig = dewIdempotentConfig;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        Idempotent idempotent = ((HandlerMethod) handler).getMethod().getAnnotation(Idempotent.class);
        if (idempotent == null) {
            return super.preHandle(request, response, handler);
        }
        // 参数设置
        String optTypeFlag = StringUtils.isEmpty(idempotent.optTypeFlag()) ? dewIdempotentConfig.getDefaultOptTypeFlag() : idempotent.optTypeFlag();
        String optType = request.getHeader(optTypeFlag);
        if (StringUtils.isEmpty(optType)) {
            optType = request.getParameter(optTypeFlag);
        }
        String optIdFlag = StringUtils.isEmpty(idempotent.optIdFlag()) ? dewIdempotentConfig.getDefaultOptIdFlag() : idempotent.optIdFlag();
        String optId = request.getHeader(optIdFlag);
        if (StringUtils.isEmpty(optId)) {
            optId = request.getParameter(optIdFlag);
        }
        if (!DewIdempotent.existOptTypeInfo(optType)) {
            long expireMs = idempotent.expireMs() == -1 ? dewIdempotentConfig.getDefaultExpireMs() : idempotent.expireMs();
            boolean needConfirm = idempotent.needConfirm();
            StrategyEnum strategy = idempotent.strategy() == StrategyEnum.AUTO ? dewIdempotentConfig.getDefaultStrategy() : idempotent.strategy();
            DewIdempotent.initOptTypeInfo(optType, needConfirm, expireMs, strategy);
        }
        switch (DewIdempotent.process(optType, optId)) {
            case NOT_EXIST:
                return super.preHandle(request, response, handler);
            case UN_CONFIRM:
                ErrorController.error(request, response, 409, "The last operation was still going on, please wait.", DewIdempotentException.class.getName());
                return false;
            case CONFIRMED:
                ErrorController.error(request, response, 423, "Resources have been processed, can't repeat the request.", DewIdempotentException.class.getName());
                return false;
            default:
                return false;
        }
    }

}
