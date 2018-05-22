package com.tairanchina.csp.dew.example.hystrix.client.fallback;

import com.tairanchina.csp.dew.example.hystrix.client.ExampleClient2;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

/**
 * desription:
 * Created by ding on 2017/11/17.
 */
@Component
public class ExampleFallback2 implements ExampleClient2 {

    @Override
    public ResponseEntity getExe() {
        return null;
    }

    @Override
    public ResponseEntity getExe(int i, String str) {
        return ResponseEntity.ok("get fallback");
    }

    @Override
    public ResponseEntity postExe(int i, String str) {
        return ResponseEntity.ok("post fallback");
    }

    @Override
    public ResponseEntity putExe(int i, String str) {
        return ResponseEntity.ok("put fallback");
    }

    @Override
    public ResponseEntity deleteExe(int i, String str) {
        return ResponseEntity.ok("delete fallback");
    }
}
