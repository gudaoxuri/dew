package com.tairanchina.csp.dew.example.sleuth;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient("sleuth-invoke1-example")
public interface SleuthInvoke1Client {

    @GetMapping("pong")
    String pong(@RequestParam("code") String code);

}
