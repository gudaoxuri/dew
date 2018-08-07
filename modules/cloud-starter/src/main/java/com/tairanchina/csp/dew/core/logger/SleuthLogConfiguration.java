package com.tairanchina.csp.dew.core.logger;

import com.tairanchina.csp.dew.Dew;
import com.tairanchina.csp.dew.core.DewCloudConfig;
import com.tairanchina.csp.dew.core.cluster.Cluster;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.sleuth.Span;
import org.springframework.cloud.sleuth.Tracer;
import org.springframework.cloud.sleuth.instrument.messaging.TraceMessageHeaders;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

@Configuration
@ConditionalOnProperty(value = "spring.sleuth.enabled", matchIfMissing = true)
@EnableConfigurationProperties(DewCloudConfig.class)
public class SleuthLogConfiguration {

    @Autowired
    private Tracer tracer;

    @PostConstruct
    public void init() {
        if (Dew.cluster != null && Dew.cluster.mq != null) {
            Cluster.initMQHeader(name -> {
                DewTraceLogWrap.request("Cluster", "request", null);
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
                DewTraceLogWrap.response("Cluster", 200, "response", null);
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
                }
            });
        }
    }
}
