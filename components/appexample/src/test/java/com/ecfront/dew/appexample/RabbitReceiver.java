package com.ecfront.dew.appexample;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

@Service
public class RabbitReceiver {

    private static final Logger logger = LoggerFactory.getLogger(RabbitReceiver.class);

    @Bean
    FanoutExchange fanoutExchange() {
        return new FanoutExchange("pub");
    }

    @Bean
    public Queue AMessage() {
        return new Queue("sub.A");
    }

    @Bean
    public Queue BMessage() {
        return new Queue("sub.B");
    }

    @Bean
    Binding bindingExchangeA(Queue AMessage, FanoutExchange fanoutExchange) {
        return BindingBuilder.bind(AMessage).to(fanoutExchange);
    }

    @Bean
    Binding bindingExchangeB(Queue BMessage, FanoutExchange fanoutExchange) {
        return BindingBuilder.bind(BMessage).to(fanoutExchange);
    }

    @RabbitListener(queues = "point")
    public void point1(MQDTO dto) {
        logger.info("point 1 receiver:" + dto.getA() + "" + dto.getB());
    }

    @RabbitListener(queues = "point")
    public void point2(MQDTO dto) {
        logger.info("point 2 receiver:" + dto.getA() + "" + dto.getB());
    }

    @RabbitListener(queues = "sub.A")
    public void sub1(MQDTO dto) {
        logger.info("sub 1 receiver:" + dto.getA() + "" + dto.getB());
    }

    @RabbitListener(queues = "sub.B")
    public void sub2(MQDTO dto) {
        logger.info("sub 2 receiver:" + dto.getA() + "" + dto.getB());
    }

}
