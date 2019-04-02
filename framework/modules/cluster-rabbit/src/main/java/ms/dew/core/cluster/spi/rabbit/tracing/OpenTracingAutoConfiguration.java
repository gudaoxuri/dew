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

package ms.dew.core.cluster.spi.rabbit.tracing;


import com.ecfront.dew.common.$;
import ms.dew.core.cluster.spi.rabbit.RabbitClusterMQ;
import io.opentracing.Scope;
import io.opentracing.Tracer;
import io.opentracing.propagation.Format;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Open tracing auto configuration.
 *
 * @author gudaoxuri
 */
@Configuration
@ConditionalOnClass(RabbitTemplate.class)
@ConditionalOnExpression("#{'${dew.cluster.cache}'=='rabbit' "
        + "|| '${dew.cluster.mq}'=='rabbit' "
        + "|| '${dew.cluster.lock}'=='rabbit' "
        + "|| '${dew.cluster.map}'=='rabbit' "
        + "|| '${dew.cluster.election}'=='rabbit'}")
public class OpenTracingAutoConfiguration {

    private static final Logger logger = LoggerFactory.getLogger(OpenTracingAutoConfiguration.class);

    @Autowired(required = false)
    private Tracer tracer;

    /**
     * Init.
     */
    @PostConstruct
    public void init() {
        if (tracer == null) {
            return;
        }
        logger.info("Load Auto Configuration : {}", this.getClass().getName());
        RabbitClusterMQ.setSendBeforeFun((exchange, routingKey, messageProperties) -> {
            try {
                // 修改Headers Collections.unmodifiableMap 为可修改
                $.bean.setValue(messageProperties, "headers", messageProperties.getHeaders().entrySet()
                        .stream().collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)));
            } catch (NoSuchFieldException ignore) {
                throw new RuntimeException(ignore);
            }
            Scope scope = RabbitMqTracingUtils.buildSendSpan(tracer, messageProperties);
            tracer.inject(
                    scope.span().context(),
                    Format.Builtin.TEXT_MAP,
                    new RabbitMqInjectAdapter(messageProperties));
            RabbitMqSpanDecorator.onSend(messageProperties, exchange, routingKey, scope.span());
            return scope;
        });
        RabbitClusterMQ.setSendErrorFun((ex, beforeResult) -> {
            if (beforeResult != null) {
                RabbitMqSpanDecorator.onError(ex, ((Scope) beforeResult).span());
            }
        });
        RabbitClusterMQ.setSendFinishFun(beforeResult -> {
            if (beforeResult != null) {
                ((Scope) beforeResult).close();
            }
        });
        RabbitClusterMQ.setReceiveBeforeFun((exchange, routingKey, queueName, messageProperties) -> {
            Optional<Scope> child = RabbitMqTracingUtils.buildReceiveSpan(messageProperties, tracer);
            child.ifPresent(scope -> RabbitMqSpanDecorator.onReceive(messageProperties, exchange, routingKey, queueName, scope.span()));
            return child;
        });
        RabbitClusterMQ.setReceiveErrorFun((ex, beforeResult) -> {
            if (beforeResult != null) {
                ((Optional<Scope>) beforeResult).ifPresent(scope -> RabbitMqSpanDecorator.onError(ex, scope.span()));
            }
        });
        RabbitClusterMQ.setReceiveFinishFun(beforeResult -> {
            if (beforeResult != null) {
                ((Optional<Scope>) beforeResult).ifPresent(Scope::close);
            }
        });


    }

}
