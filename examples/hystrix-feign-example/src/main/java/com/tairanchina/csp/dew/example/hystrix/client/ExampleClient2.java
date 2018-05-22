package com.tairanchina.csp.dew.example.hystrix.client;

import com.tairanchina.csp.dew.example.hystrix.client.fallback.ExampleFallback2;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * desription:
 * Created by ding on 2017/11/17.
 */
@FeignClient(value = "hystrix-feign-example2", fallback = ExampleFallback2.class)
public interface ExampleClient2 {

    @GetMapping("example/get-exe/ori")
    ResponseEntity getExe();

    @GetMapping("example/get-exe")
    ResponseEntity getExe(@RequestParam("i") int i, @RequestParam("str") String str);

    @PostMapping("example/post-exe")
    ResponseEntity postExe(@RequestParam("i") int i, @RequestParam("str") String str);

    @PutMapping("example/put-exe")
    ResponseEntity putExe(@RequestParam("i") int i, @RequestParam("str") String str);

    @DeleteMapping("example/delete-exe")
    ResponseEntity deleteExe(@RequestParam("i") int i, @RequestParam("str") String str);
}
