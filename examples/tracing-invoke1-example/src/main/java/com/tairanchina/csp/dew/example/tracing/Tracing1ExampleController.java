package com.tairanchina.csp.dew.example.tracing;

import com.tairanchina.csp.dew.Dew;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping("/")
public class Tracing1ExampleController {

    @Autowired
    private RestTemplate restTemplate;

    @GetMapping("ping")
    public String ping(@RequestParam("code") String code) {
        Dew.cluster.mq.publish("test", code);
        return restTemplate.getForObject("http://tracing-invoke2-example/ping?code=" + code, String.class);
    }

}
