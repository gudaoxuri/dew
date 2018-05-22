package com.tairanchina.csp.dew.example.hystrix.client;

import com.tairanchina.csp.dew.example.hystrix.client.fallback.NullFallback;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * desription:
 * Created by ding on 2017/11/16.
 */
@FeignClient(value = "hystrix-feign-example2", fallback = NullFallback.class)
public interface NullClient {

    @GetMapping("example/get-exe/ori")
    ResponseEntity getExe();
}
