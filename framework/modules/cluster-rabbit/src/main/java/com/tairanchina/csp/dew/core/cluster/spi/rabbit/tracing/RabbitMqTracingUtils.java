package com.tairanchina.csp.dew.core.cluster.spi.rabbit.tracing;

import com.rabbitmq.client.AMQP;
import io.opentracing.*;
import io.opentracing.propagation.Format;
import io.opentracing.tag.Tags;

import java.util.Map;
import java.util.Optional;

class RabbitMqTracingUtils {

    static Optional<Scope> buildReceiveSpan(AMQP.BasicProperties messageProperties, Tracer tracer) {
        Optional<SpanContext> context = findParent(messageProperties, tracer);
        if (context.isPresent()) {
            Tracer.SpanBuilder spanBuilder =
                    tracer
                            .buildSpan(RabbitMqTracingTags.SPAN_KIND_CONSUMER)
                            .ignoreActiveSpan()
                            .withTag(Tags.SPAN_KIND.getKey(), RabbitMqTracingTags.SPAN_KIND_CONSUMER);
            spanBuilder.addReference(References.FOLLOWS_FROM, context.get());
            Scope scope = spanBuilder.startActive(true);
            return Optional.of(scope);
        }
        return Optional.empty();
    }

    static Scope buildSendSpan(Tracer tracer, AMQP.BasicProperties messageProperties) {
        Tracer.SpanBuilder spanBuilder =
                tracer
                        .buildSpan(RabbitMqTracingTags.SPAN_KIND_PRODUCER)
                        .ignoreActiveSpan()
                        .withTag(Tags.SPAN_KIND.getKey(), RabbitMqTracingTags.SPAN_KIND_PRODUCER);
        ScopeManager scopeManager = tracer.scopeManager();
        Optional<SpanContext> existingSpanContext = Optional.ofNullable(scopeManager)
                .map(ScopeManager::active)
                .map(Scope::span)
                .map(Span::context);
        existingSpanContext.ifPresent(spanBuilder::asChildOf);
        if (messageProperties.getHeaders() != null) {
            Optional<SpanContext> messageParentContext = findParent(messageProperties, tracer);
            messageParentContext.ifPresent(spanBuilder::asChildOf);
        }
        return spanBuilder.startActive(true);
    }

    private static Optional<SpanContext> findParent(
            AMQP.BasicProperties messageProperties, Tracer tracer) {
        final Map<String, Object> headers = messageProperties.getHeaders();
        SpanContext spanContext =
                tracer.extract(
                        Format.Builtin.TEXT_MAP, new RabbitMqMessagePropertiesExtractAdapter(headers));
        if (spanContext == null) {
            return Optional.ofNullable(tracer.activeSpan()).map(Span::context);
        } else {
            return Optional.of(spanContext);
        }
    }
}
