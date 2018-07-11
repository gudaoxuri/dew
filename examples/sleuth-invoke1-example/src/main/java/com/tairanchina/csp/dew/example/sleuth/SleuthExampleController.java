package com.tairanchina.csp.dew.example.sleuth;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.sleuth.Span;
import org.springframework.cloud.sleuth.instrument.web.TraceFilter;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@RestController
@RequestMapping("/")
public class SleuthExampleController {

    private Logger logger = LoggerFactory.getLogger(SleuthExampleController.class);

    @Autowired
    private RestTemplate restTemplate;

    @GetMapping("ping")
    public String ping(@RequestParam("code") String code) throws InterruptedException {
        logger.info("ssss");
        return restTemplate.getForObject("http://sleuth-invoke2-example/ping?code=" + code, String.class);
    }

    @GetMapping("pong")
    public String pong(HttpServletRequest httpServletRequest, @RequestParam("code") String code) throws InterruptedException {
        logger.info("进入方法");
        Map map = MDC.getCopyOfContextMap();
        new Thread(() -> {
            //异步日志追踪支持
            MDC.setContextMap(map);   //方法1
//            setMDC(httpServletRequest);   //方法2
            logger.info("正异步线程中");
        }).start();
        Thread.sleep(2000);
        return code;
    }

    // 也可以调用其他静态方法实现
    public static void setMDC(HttpServletRequest httpServletRequest) {
        String attributeName = TraceFilter.class.getName() + ".TRACE";
        Span span = (Span) httpServletRequest.getAttribute(attributeName);
        MDC.put(Span.SPAN_ID_NAME, Span.idToHex(span.getSpanId()));
        MDC.put(Span.SPAN_EXPORT_NAME, String.valueOf(span.isExportable()));
        MDC.put(Span.TRACE_ID_NAME, span.traceIdString());
    }

}
