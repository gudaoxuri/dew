package com.tairanchina.csp.dew.core.logger;

import ch.qos.logback.classic.Level;
import com.tairanchina.csp.dew.core.DewCloudConfig;
import com.tairanchina.csp.dew.core.cluster.Cluster;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.sleuth.Span;
import org.springframework.cloud.sleuth.Tracer;
import org.springframework.cloud.sleuth.instrument.messaging.TraceMessageHeaders;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

@Configuration
@ConditionalOnProperty(prefix = "dew.cloud.traceLog", name = "enabled", havingValue = "true", matchIfMissing = true)
@ConditionalOnWebApplication
@EnableConfigurationProperties(DewCloudConfig.class)
@Aspect
public class TraceAutoConfiguration extends WebMvcConfigurerAdapter {

    private static final Logger logger = LoggerFactory.getLogger(TraceAutoConfiguration.class);

    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private org.springframework.cloud.sleuth.instrument.web.client.TraceRestTemplateInterceptor traceRestTemplateInterceptor;
    @Autowired
    private Tracer tracer;
    @Value("${dew.cluster.mq:}")
    private String mqConfig;

    @PostConstruct
    public void init() {
        logger.info("Load Auto Configuration : {}", this.getClass().getName());
        restTemplate.getInterceptors().add(traceRestTemplateInterceptor);
        restTemplate.getInterceptors().add(new TraceRestTemplateInterceptor());
        ch.qos.logback.classic.Logger root = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(TraceLogWrap.class);
        root.setLevel(Level.TRACE);
        processMQ();
    }

    private void processMQ() {
        if (!StringUtils.isEmpty(mqConfig)) {
            Cluster.initMQHeader(name -> {
                TraceLogWrap.request("MQ", name, "");
                Span span = tracer.createSpan(name);
                Long parentId = !span.getParents().isEmpty() ? span.getParents().get(0) : null;
                HashMap<String, Object> hashMap = new HashMap<String, Object>() {{
                    put(TraceMessageHeaders.TRACE_ID_NAME, Span.idToHex(span.getTraceId()));
                    put(TraceMessageHeaders.SPAN_ID_NAME, Span.idToHex(span.getSpanId()));
                    put(TraceMessageHeaders.SPAN_NAME_NAME, span.getName());
                    put(TraceMessageHeaders.PROCESS_ID_NAME, span.getProcessId());
                    if (parentId != null) {
                        put(Span.PARENT_ID_NAME, Span.idToHex(parentId));
                    }
                }};
                tracer.detach(span);
                return hashMap;
            }, headerWithName -> {
                String name = (String) headerWithName[0];
                Map<String, Object> header = (Map<String, Object>) headerWithName[1];
                if (header == null || header.isEmpty()) {
                    return;
                }
                String traceId = header.containsKey(TraceMessageHeaders.TRACE_ID_NAME) ? String.valueOf(header.get(TraceMessageHeaders.TRACE_ID_NAME)) : null;
                if (traceId == null) {
                    tracer.createSpan(name);
                } else {
                    Span.SpanBuilder spanBuilder = Span.builder();
                    String spanId = header.containsKey(TraceMessageHeaders.SPAN_ID_NAME) ? String.valueOf(header.get(TraceMessageHeaders.SPAN_ID_NAME)) : traceId;
                    spanBuilder
                            .traceIdHigh(traceId.length() == 32 ? Span.hexToId(traceId, 0) : 0)
                            .traceId(Span.hexToId(traceId))
                            .spanId(Span.hexToId(spanId));
                    String processId = header.containsKey(TraceMessageHeaders.PROCESS_ID_NAME) ? String.valueOf(header.get(TraceMessageHeaders.PROCESS_ID_NAME)) : null;
                    String spanName = header.containsKey(TraceMessageHeaders.SPAN_NAME_NAME) ? String.valueOf(header.get(TraceMessageHeaders.SPAN_NAME_NAME)) : null;
                    String parentId = header.containsKey(TraceMessageHeaders.PARENT_ID_NAME) ? String.valueOf(header.get(TraceMessageHeaders.PARENT_ID_NAME)) : null;
                    if (processId != null) {
                        spanBuilder.processId(processId);
                    }
                    if (spanName != null) {
                        spanBuilder.name(spanName);
                    }
                    if (parentId != null) {
                        spanBuilder.parent(Span.hexToId(parentId));
                    }
                    spanBuilder.remote(true);
                    Span span = spanBuilder.build();
                    tracer.createSpan(name, span);
                    TraceLogWrap.response("MQ", 200, name, "");
                }
            });
        }
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        LoggerHandlerInterceptor loggerHandlerInterceptor = new LoggerHandlerInterceptor();
        registry.addInterceptor(loggerHandlerInterceptor).excludePathPatterns("/error/**");
        super.addInterceptors(registry);
    }

    @Override
    public void configurePathMatch(PathMatchConfigurer configurer) {
        configurer.setUseTrailingSlashMatch(true);
    }


}