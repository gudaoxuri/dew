package com.tairanchina.csp.dew.example.hystrixtwo.controller;

import com.tairanchina.csp.dew.example.hystrixtwo.client.ExampleClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.atomic.AtomicInteger;


@RestController
@RequestMapping("example/")
public class HystrixExampleController {

    private static final Logger logger = LoggerFactory.getLogger(HystrixExampleController.class);

    private AtomicInteger atomicInteger = new AtomicInteger();

    @Autowired
    private ExampleClient exampleClient;


    @GetMapping("test")
    public ResponseEntity testPressure(){
        logger.info("count: "+atomicInteger.incrementAndGet());
        return exampleClient.testPressure();
    }

    @GetMapping("test2")
    public ResponseEntity testPressure2(){
        logger.info("count: "+atomicInteger.incrementAndGet());
        return exampleClient.testPressure2();
    }


    @GetMapping("get-exe/ori/{type}")
    public ResponseEntity getExe(@PathVariable("type") String type) throws InterruptedException {
        /*logger.info("post-exe   " );
        for (int m =0;m<80000;m++){
            exampleClient.postExe(m,"post");
//            nullClient.getExe();
            Thread.sleep(100);
        }*/
        if (type.equals("GET")) {
            String res = exampleClient.exe();
            return ResponseEntity.ok(res);
        }
        String res = exampleClient.noMethod();
        return ResponseEntity.ok(res);
        /*ResponseEntity responseEntity = exampleClient.postExe(1, "post");
//        String res = exampleClient.exe();
        return responseEntity;*/
    }

    @GetMapping("get-exe")
    public ResponseEntity getExe(@RequestParam("i") int i, @RequestParam("str") String str) throws InterruptedException {
        logger.info("get-exe   " + "i=" + i + "str=" + str);
        for (int m = 0; m < 80000; m++) {
            exampleClient.deleteExe(m, "delete");
            Thread.sleep(100);
        }
        return ResponseEntity.ok().build();
    }

    @PostMapping("post-exe")
    public ResponseEntity postExe(@RequestParam("i") int i, @RequestParam("str") String str) throws InterruptedException {
        logger.info("post-exe   " + "i=" + i + "str=" + str);
        Thread.sleep(100000000);
        if (i > 600) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.status(404).build();
    }

    @PutMapping("put-exe")
    public ResponseEntity putExe(@RequestParam("i") int i, @RequestParam("str") String str) {
        logger.info("put-exe   " + "i=" + i + "str=" + str);
        return ResponseEntity.ok().build();
    }
}
