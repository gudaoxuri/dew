package com.ecfront.dew.core.logger;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class DewLoggerHandlerInterceptor extends HandlerInterceptorAdapter {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        DewTraceLogWrap.received("WebMVC",request.getMethod(), request.getRequestURL().toString());
        return super.preHandle(request, response, handler);
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        DewTraceLogWrap.reply("WebMVC",response.getStatus(), request.getMethod(), request.getRequestURL().toString());
        super.postHandle(request, response, handler, modelAndView);
    }
}
