package com.tairanchina.csp.dew.example.sleuth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping("/")
public class SleuthExampleController {

    @Autowired
    private RestTemplate restTemplate;

    @GetMapping("ping")
    public String ping(@RequestParam("code") String code) {
        SomeVO someVO = new SomeVO();
        someVO.setCode(code);
        return restTemplate.postForObject("http://sleuth-invoke3-example/ping", someVO, String.class);
    }
    public static class SomeVO {
        private String code;

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }
    }


}
