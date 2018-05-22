package com.tairanchina.csp.dew.example.moinitor;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Random;

@RestController
public class ExampleController {

    @GetMapping("/example")
    @HystrixCommand(fallbackMethod = "exampleFallback")
    public String example(@RequestParam("q") String q) throws InterruptedException {
        Thread.sleep(new Random().nextBoolean()?0:4000);
        return "enjoy!";
    }

    @GetMapping("/hello")
    @HystrixCommand(fallbackMethod = "helloFallback")
    public String hello() throws Exception {
        if(new Random().nextBoolean()){
            return "hello!";
        }else{
            throw new Exception("error");
        }
    }

    private String exampleFallback(String q) {
        return "enjoyFallback!";
    }

    private String helloFallback() {
        return "helloFallback!";
    }

}
