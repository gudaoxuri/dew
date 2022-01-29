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

package group.idealworld.dew.idempotent.interceptor;

import group.idealworld.dew.Dew;
import group.idealworld.dew.core.web.error.ErrorController;
import group.idealworld.dew.idempotent.DewIdempotent;
import group.idealworld.dew.idempotent.DewIdempotentConfig;
import group.idealworld.dew.idempotent.annotations.Idempotent;
import group.idealworld.dew.idempotent.strategy.StrategyEnum;
import org.springframework.util.ObjectUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Idempotent handler interceptor.
 *
 * @author gudaoxuri
 */
public class IdempotentHandlerInterceptor extends HandlerInterceptorAdapter {

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
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        Idempotent idempotent = ((HandlerMethod) handler).getMethod().getAnnotation(Idempotent.class);
        if (idempotent == null) {
            return super.preHandle(request, response, handler);
        }
        // 参数设置
        String optType = "[" + request.getMethod() + "]" + Dew.Info.name + "/" + request.getRequestURI();
        String optIdFlag = ObjectUtils.isEmpty(idempotent.optIdFlag()) ? dewIdempotentConfig.getDefaultOptIdFlag() : idempotent.optIdFlag();
        String optId = request.getHeader(optIdFlag);
        if (ObjectUtils.isEmpty(optId)) {
            optId = request.getParameter(optIdFlag);
        }
        if (ObjectUtils.isEmpty(optId)) {
            // optId不存在，表示忽略幂等检查，强制执行
            return super.preHandle(request, response, handler);
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
                ErrorController.error(request, response, 409,
                        "The last operation was still going on, please wait.", IdempotentException.class.getName());
                return false;
            case CONFIRMED:
                ErrorController.error(request, response, 423,
                        "Resources have been processed, can't repeat the request.", IdempotentException.class.getName());
                return false;
            default:
                return false;
        }
    }

}
