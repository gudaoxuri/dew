package com.tairanchina.csp.dew.core.logger;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class LoggerHandlerInterceptor extends HandlerInterceptorAdapter {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        TraceLogWrap.received("Web", request.getMethod(), request.getRequestURL().toString());
        return super.preHandle(request, response, handler);
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        TraceLogWrap.reply("Web", response.getStatus(), request.getMethod(), request.getRequestURL().toString());
        super.postHandle(request, response, handler, modelAndView);
    }
}
