package com.tairanchina.csp.dew.example.hystrixtwo.client;

import com.tairanchina.csp.dew.example.hystrixtwo.client.fallback.ExampleFallback;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * desription:
 * Created by ding on 2017/10/25.
 */
@FeignClient(value = "hystrix-feign-example",fallback = ExampleFallback.class)
public interface ExampleClient {

    @GetMapping("example/test")
    ResponseEntity testPressure();

    @GetMapping("example/test2")
    ResponseEntity testPressure2();

    @GetMapping("example/get-exe/ori")
    ResponseEntity getExe();

    @GetMapping("example/exe")
    String exe();

    @GetMapping("example/noMethod")
    String noMethod();


    @GetMapping("example/get-exe")
    ResponseEntity getExe(@RequestParam("i") int i, @RequestParam("str") String str);

    @PostMapping("example/post-exe")
    ResponseEntity postExe(@RequestParam("i") int i, @RequestParam("str") String str);

    @PutMapping("example/put-exe")
    ResponseEntity putExe(@RequestParam("i") int i, @RequestParam("str") String str);

    @DeleteMapping("example/delete-exe")
    ResponseEntity deleteExe(@RequestParam("i") int i, @RequestParam("str") String str);
}
