package com.tairanchina.csp.dew.example.web;

import com.tairanchina.csp.dew.Dew;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

import javax.annotation.PostConstruct;

/**
 * 工程启动类
 */
@SpringBootApplication
public class WebExampleApplication {
    private static final Logger logger = LoggerFactory.getLogger(WebExampleApplication.class);

    @PostConstruct
    public void init() {
        Dew.cluster.mq.response("abc", msg -> {
            //todo 接收成功
            logger.info("msg={}", msg);
            //throw new RuntimeException("测试错误");
        });
    }

    public static void main(String[] args) {
        new SpringApplicationBuilder(WebExampleApplication.class).run(args);
    }

}
