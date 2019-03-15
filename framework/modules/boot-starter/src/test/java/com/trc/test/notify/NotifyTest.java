/*
 * Copyright 2019. the original author or authors.
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

package com.trc.test.notify;


import com.ecfront.dew.common.Resp;
import com.tairanchina.csp.dew.Dew;
import org.junit.Assert;
import org.springframework.stereotype.Component;

import java.util.HashSet;

@Component
public class NotifyTest {

    public void testAll() throws Exception {
        Resp<Void> result = Dew.notify.send("flag1", "测试消息，默认通知人", "测试");
        Assert.assertTrue(result.ok());
        result = Dew.notify.send("flag1", "测试消息，加上指定通知人", "测试", new HashSet<String>() {{
            add("15658132456");
        }});
        Assert.assertTrue(result.ok());
        result = Dew.notify.send("flag1", "测试消息，带符号$$ () <s>\r\n\"#sf@!&^$(Q@^Q)*UR#", "测试");
        Assert.assertTrue(result.ok());
        result = Dew.notify.send("flag1", "测试消息，带符号$$ com.trc.test.web.WebController.customHttpState(WebController.java:73)\n" +
                "com.trc.test.web.WebController$$FastClassBySpringCGLIB$$2ae0c170.invoke(<generated>)\n" +
                "org.springframework.cglib.proxy.MethodProxy.invoke(MethodProxy.java:204)\n" +
                "org.springframework.aop.framework.CglibAopProxy$CglibMethodInvocation.invokeJoinpoint(CglibAopProxy.java:736)\n" +
                "org.springframework.aop.framework.ReflectiveMethodInvocation.proceed(ReflectiveMethodInvocation.java:157)\n" +
                "org.springframework.validation.beanvalidation.MethodValidationInterceptor.invoke(MethodValidationInterceptor.java:150)\n" +
                "org.springframework.aop.framework.ReflectiveMethodInvocation.proceed(ReflectiveMethodInvocation.java:179)\n" +
                "org.springframework.aop.framework.CglibAopProxy$DynamicAdvisedInterceptor.intercept(CglibAopProxy.java:671)\n" +
                "com.trc.test.web.WebController$$EnhancerBySpringCGLIB$$34293e67.customHttpState(<generated>)\n" +
                "sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method)\n" +
                "sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:62)\n" +
                "sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)\n" +
                "java.lang.reflect.Method.invoke(Method.java:497)\n" +
                "org.springframework.web.method.support.InvocableHandlerMethod.doInvoke(InvocableHandlerMethod.java:205)\n" +
                "org.springframework.web.method.support.InvocableHandlerMethod.invokeForRequest(InvocableHandlerMethod.java:133)\n" +
                "org.springframework.web.servlet.mvc.method.annotation.ServletInvocableHandlerMethod.invokeAndHandle(ServletInvocableHandlerMethod.java:97)\n" +
                "org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter.invokeHandlerMethod(RequestMappingHandlerAdapter.java:827)\n" +
                "org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter.handleInternal(RequestMappingHandlerAdapter.java:738)\n" +
                "org.springframework.web.servlet.mvc.method.AbstractHandlerMethodAdapter.handle(AbstractHandlerMethodAdapter.java:85)\n" +
                "org.springframework.web.servlet.DispatcherServlet.doDispatch(DispatcherServlet.java:967)\n" +
                "org.springframework.web.servlet.DispatcherServlet.doService(DispatcherServlet.java:901)\n" +
                "org.springframework.web.servlet.FrameworkServlet.processRequest(FrameworkServlet.java:970)\n" +
                "org.springframework.web.servlet.FrameworkServlet.doGet(FrameworkServlet.java:861)\n" +
                "javax.servlet.http.HttpServlet.service(HttpServlet.java:635)\n" +
                "org.springframework.web.servlet.FrameworkServlet.service(FrameworkServlet.java:846)\n" +
                "javax.servlet.http.HttpServlet.service(HttpServlet.java:742)\n" +
                "org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:231)\n" +
                "org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:166)\n" +
                "org.apache.tomcat.websocket.server.WsFilter.doFilter(WsFilter.java:52)\n" +
                "org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:193)\n" +
                "org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:166)\n" +
                "org.springframework.boot.web.filter.ApplicationContextHeaderFilter.doFilterInternal(ApplicationContextHeaderFilter.java:55)\n" +
                "org.springframework.web.filter.OncePerRequestFilter.doFilter(OncePerRequestFilter.java:107)\n" +
                "org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:193)\n" +
                "org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:166)\n" +
                "org.springframework.boot.actuate.trace.WebRequestTraceFilter.doFilterInternal(WebRequestTraceFilter.java:111)\n" +
                "org.springframework.web.filter.OncePerRequestFilter.doFilter(OncePerRequestFilter.java:107)\n" +
                "org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:193)\n" +
                "org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:166)\n" +
                "org.springframework.web.filter.RequestContextFilter.doFilterInternal(RequestContextFilter.java:99)\n" +
                "org.springframework.web.filter.OncePerRequestFilter.doFilter(OncePerRequestFilter.java:107)\n" +
                "org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:193)\n" +
                "org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:166)\n" +
                "org.springframework.web.filter.HttpPutFormContentFilter.doFilterInternal(HttpPutFormContentFilter.java:109)\n" +
                "org.springframework.web.filter.OncePerRequestFilter.doFilter(OncePerRequestFilter.java:107)\n" +
                "org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:193)\n" +
                "org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:166)\n" +
                "org.springframework.web.filter.HiddenHttpMethodFilter.doFilterInternal(HiddenHttpMethodFilter.java:81)\n" +
                "org.springframework.web.filter.OncePerRequestFilter.doFilter(OncePerRequestFilter.java:107)\n" +
                "org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:193)\n" +
                "org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:166)\n" +
                "org.springframework.web.filter.CharacterEncodingFilter.doFilterInternal(CharacterEncodingFilter.java:197)\n" +
                "org.springframework.web.filter.OncePerRequestFilter.doFilter(OncePerRequestFilter.java:107)\n" +
                "org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:193)\n" +
                "org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:166)\n" +
                "org.springframework.boot.actuate.autoconfigure.MetricsFilter.doFilterInternal(MetricsFilter.java:106)\n" +
                "org.springframework.web.filter.OncePerRequestFilter.doFilter(OncePerRequestFilter.java:107)\n" +
                "org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:193)\n" +
                "org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:166)\n" +
                "io.micrometer.spring.web.servlet.WebMvcMetricsFilter.doFilterInternal(WebMvcMetricsFilter.java:106)\n" +
                "org.springframework.web.filter.OncePerRequestFilter.doFilter(OncePerRequestFilter.java:107)\n" +
                "org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:193)\n" +
                "org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:166)\n" +
                "com.tairanchina.csp.dew.DewStartup$DewStartupFilter.doFilter(DewStartup.java:42)\n" +
                "org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:193)\n" +
                "org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:166)\n" +
                "org.apache.catalina.core.StandardWrapperValve.invoke(StandardWrapperValve.java:198)\n" +
                "org.apache.catalina.core.StandardContextValve.invoke(StandardContextValve.java:96)\n" +
                "org.apache.catalina.authenticator.AuthenticatorBase.invoke(AuthenticatorBase.java:496)\n" +
                "org.apache.catalina.core.StandardHostValve.invoke(StandardHostValve.java:140)\n" +
                "org.apache.catalina.valves.ErrorReportValve.invoke(ErrorReportValve.java:81)\n" +
                "org.apache.catalina.core.StandardEngineValve.invoke(StandardEngineValve.java:87)\n" +
                "org.apache.catalina.connector.CoyoteAdapter.service(CoyoteAdapter.java:342)\n" +
                "org.apache.coyote.http11.Http11Processor.service(Http11Processor.java:803)\n" +
                "org.apache.coyote.AbstractProcessorLight.process(AbstractProcessorLight.java:66)\n" +
                "org.apache.coyote.AbstractProtocol$ConnectionHandler.process(AbstractProtocol.java:790)\n" +
                "org.apache.tomcat.util.net.NioEndpoint$SocketProcessor.doRun(NioEndpoint.java:1468)\n" +
                "org.apache.tomcat.util.net.SocketProcessorBase.run(SocketProcessorBase.java:49)\n" +
                "java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1142)\n" +
                "java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:617)\n" +
                "org.apache.tomcat.util.threads.TaskThread$WrappingRunnable.run(TaskThread.java:61)\n" +
                "java.lang.Thread.run(Thread.java:745)\n", "测试");
        Assert.assertTrue(result.ok());
        Dew.notify.sendAsync("flag1", "测试消息，异步发送", "测试");

        try {
            int i = 1 / 0;
        } catch (Exception e) {
            result = Dew.notify.send("flag1", e, "测试");
            Assert.assertTrue(result.ok());
        }

        Thread.sleep(5000);
        result = Dew.notify.send(Dew.dewConfig.getBasic().getFormat().getErrorFlag(), "测试消息，带时间限制，5s只会发一次", "测试");
        Assert.assertTrue(result.ok());
        result = Dew.notify.send(Dew.dewConfig.getBasic().getFormat().getErrorFlag(), "测试消息，带时间限制，5s只会发一次，此条消息不应被收到", "测试");
        Assert.assertFalse(result.ok());
        result = Dew.notify.send(Dew.dewConfig.getBasic().getFormat().getErrorFlag(), "测试消息，带时间限制，5s只会发一次，此条消息不应被收到", "测试");
        Assert.assertFalse(result.ok());
        Thread.sleep(1000);
        result = Dew.notify.send(Dew.dewConfig.getBasic().getFormat().getErrorFlag(), "测试消息，带时间限制，5s只会发一次，此条消息不应被收到", "测试");
        Assert.assertFalse(result.ok());
        Thread.sleep(5000);
        result = Dew.notify.send(Dew.dewConfig.getBasic().getFormat().getErrorFlag(), "测试消息，带时间限制，5s只会发一次，这是5s后的消息", "测试");
        Assert.assertTrue(result.ok());
        result = Dew.notify.send(Dew.dewConfig.getBasic().getFormat().getErrorFlag(), "测试消息，带时间限制，5s只会发一次，此条消息不应被收到", "测试");
        Assert.assertFalse(result.ok());

        result = Dew.notify.send("flag2", "测试消息，有免扰时间，超过2次才发送，此条消息不应被收到", "测试");
        Assert.assertFalse(result.ok());
        result = Dew.notify.send("flag2", "测试消息，有免扰时间，超过2次才发送，此条消息不应被收到", "测试");
        Assert.assertFalse(result.ok());
        result = Dew.notify.send("flag2", "测试消息，有免扰时间，超过2次才发送", "测试");
        Assert.assertTrue(result.ok());

        result = Dew.notify.send("custom", "测试消息，有免扰时间，超过2次才发送", "测试");
        Assert.assertTrue(result.ok());

        Thread.sleep(10000);
        /*result = Dew.notify.send("sendMail", "测试消息", "测试");
        Assert.assertTrue(result.ok());*/
    }

}
