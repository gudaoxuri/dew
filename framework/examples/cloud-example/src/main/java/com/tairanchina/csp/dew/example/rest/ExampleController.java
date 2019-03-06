package com.tairanchina.csp.dew.example.rest;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Api(description = "示例应用")
@RequestMapping("/")
public class ExampleController {

    @GetMapping("/example/100")
    @ApiOperation(value = "示例，延时100")
    public String example100() throws InterruptedException {
        Thread.sleep(100);
        return "ok";
    }

    @GetMapping("/example/1000")
    @ApiOperation(value = "示例，延时1000")
    public String example1000() throws InterruptedException {
        Thread.sleep(1000);
        return "ok";
    }

    @GetMapping("/example/10000")
    @ApiOperation(value = "示例，延时10000")
    public String example10000() throws InterruptedException {
        Thread.sleep(10000);
        return "ok";
    }

}
