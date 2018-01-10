package com.ecfront.dew.example.sleuth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping("/")
public class SleuthExampleController {

    @Autowired
    private RestTemplate restTemplate;

    @GetMapping("ping")
    public String ping(@RequestParam("code") String code) {
        return restTemplate.getForObject("http://sleuth-invoke2-example/ping?code=" + code, String.class);
    }

    @GetMapping("pong")
    public String pong(@RequestParam("code") String code) {
        return code;
    }

}
