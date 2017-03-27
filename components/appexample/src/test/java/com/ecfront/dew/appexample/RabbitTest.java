package com.ecfront.dew.appexample;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.concurrent.CountDownLatch;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = APPExampleApplication.class, properties = {"spring.profiles.active=test"})
public class RabbitTest {

    @Autowired
    private AmqpTemplate rabbitTemplate;

    @Test
    public void testRabbit() throws Exception {
        for (int i = 0; i < 10; i++) {
            MQDTO dto = new MQDTO();
            dto.setA("haha");
            dto.setB(i);
            rabbitTemplate.convertAndSend("point", dto);
            rabbitTemplate.convertAndSend("pub", "sub", dto);
        }

        new CountDownLatch(1).await();
    }

}
