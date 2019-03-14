package com.tairanchina.csp.dew.example;

import com.ecfront.dew.common.$;
import org.junit.Test;

import java.io.IOException;

/**
 * desription:
 * Created by ding on 2018/2/1.
 */
public class WebTest {

    @Test
    public void testPre() throws IOException {
        for (int i = 0; i < 5000; i++) {
            $.http.get("http://127.0.0.1:8080/example");
        }
    }
}
