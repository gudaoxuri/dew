package com.tairanchina.csp.dew.example.tracing;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/")
public class Tracing2ExampleController {

    @GetMapping("ping")
    public String ping(@RequestParam("code") String code) {
        return code + "_reply";
    }

}
