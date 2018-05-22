package com.tairanchina.csp.dew.example.hystrix.client.fallback;

import com.tairanchina.csp.dew.example.hystrix.client.NullClient;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

/**
 * desription:
 * Created by ding on 2017/11/16.
 */
@Component
public class NullFallback implements NullClient {

    @Override
    public ResponseEntity getExe() {
        System.out.println("进入降级");
        return null;
    }
}
