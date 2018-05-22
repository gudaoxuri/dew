package com.tairanchina.csp.dew.example.hystrix.controller;

import com.tairanchina.csp.dew.Dew;
import com.tairanchina.csp.dew.example.hystrix.service.HystrixExampleService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;


@RestController
public class HystrixExampleController {

    private static final Logger logger = LoggerFactory.getLogger(HystrixExampleController.class);

    @Autowired
    private HystrixExampleService hystrixExampleService;

    @GetMapping("/exe")
    public String exe() {
        logger.info("Controller Token:" + Dew.context().getToken());
        return hystrixExampleService.getStores(new HashMap<String, Object>() {{
        }},Dew.context());
    }

}
