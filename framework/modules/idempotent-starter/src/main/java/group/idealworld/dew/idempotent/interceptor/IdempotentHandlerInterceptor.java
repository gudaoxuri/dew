package group.idealworld.dew.idempotent.interceptor;

import group.idealworld.dew.Dew;
import group.idealworld.dew.core.web.error.ErrorController;
import group.idealworld.dew.idempotent.DewIdempotent;
import group.idealworld.dew.idempotent.DewIdempotentConfig;
import group.idealworld.dew.idempotent.annotations.Idempotent;
import group.idealworld.dew.idempotent.strategy.StrategyEnum;
import org.springframework.util.ObjectUtils;
import org.springframework.web.method.HandlerMethod;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * Idempotent handler interceptor.
 *
 * @author gudaoxuri
 */
public class IdempotentHandlerInterceptor implements HandlerInterceptor {

    private DewIdempotentConfig dewIdempotentConfig;

    /**
     * Instantiates a new Idempotent handler interceptor.
     *
     * @param dewIdempotentConfig the dew idempotent config
     */
    public IdempotentHandlerInterceptor(DewIdempotentConfig dewIdempotentConfig) {
        this.dewIdempotentConfig = dewIdempotentConfig;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        Idempotent idempotent = ((HandlerMethod) handler).getMethod().getAnnotation(Idempotent.class);
        if (idempotent == null) {
            return HandlerInterceptor.super.preHandle(request, response, handler);
        }
        // 参数设置
        String optType = "[" + request.getMethod() + "]" + Dew.Info.name + "/" + request.getRequestURI();
        String optIdFlag = ObjectUtils.isEmpty(idempotent.optIdFlag()) ? dewIdempotentConfig.getDefaultOptIdFlag()
                : idempotent.optIdFlag();
        String optId = request.getHeader(optIdFlag);
        if (ObjectUtils.isEmpty(optId)) {
            optId = request.getParameter(optIdFlag);
        }
        if (ObjectUtils.isEmpty(optId)) {
            // optId不存在，表示忽略幂等检查，强制执行
            return HandlerInterceptor.super.preHandle(request, response, handler);
        }
        if (!DewIdempotent.existOptTypeInfo(optType)) {
            long expireMs = idempotent.expireMs() == -1 ? dewIdempotentConfig.getDefaultExpireMs()
                    : idempotent.expireMs();
            boolean needConfirm = idempotent.needConfirm();
            StrategyEnum strategy = idempotent.strategy() == StrategyEnum.AUTO
                    ? dewIdempotentConfig.getDefaultStrategy()
                    : idempotent.strategy();
            DewIdempotent.initOptTypeInfo(optType, needConfirm, expireMs, strategy);
        }
        switch (DewIdempotent.process(optType, optId)) {
            case NOT_EXIST:
                return HandlerInterceptor.super.preHandle(request, response, handler);
            case UN_CONFIRM:
                ErrorController.error(request, response, 409,
                        "The last operation was still going on, please wait.", IdempotentException.class.getName());
                return false;
            case CONFIRMED:
                ErrorController.error(request, response, 423,
                        "Resources have been processed, can't repeat the request.",
                        IdempotentException.class.getName());
                return false;
            default:
                return false;
        }
    }

}
