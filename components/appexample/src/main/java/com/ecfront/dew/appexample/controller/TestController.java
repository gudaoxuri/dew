package com.ecfront.dew.appexample.controller;

import com.ecfront.dew.appexample.service.HystrixService;
import com.ecfront.dew.common.Resp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/test")
public class TestController {

    private static final Logger logger = LoggerFactory.getLogger(TestController.class);

    @Autowired
    private HystrixService hystrixService;

    @GetMapping(value = "")
    @ResponseBody
    public Resp<String> get() throws Exception {
        return hystrixService.doGet();
    }

    @GetMapping(value = "error")
    @ResponseBody
    public Resp<String> error() throws Exception {
        return hystrixService.errorGet();
    }

}
