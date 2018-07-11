package com.tairanchina.csp.dew.example.rest;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Api(description = "示例应用")
public class ExampleController {

    @GetMapping("/example")
    @ApiOperation(value = "示例方式")
    public String example() {
        return "{}";
    }

}
