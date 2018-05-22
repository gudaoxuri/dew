package com.trc.test.web;

import com.ecfront.dew.common.$;
import org.junit.Test;

import java.io.IOException;

/**
 * desription:
 * Created by ding on 2018/1/15.
 */
public class MetricsTest {

    @Test
    public void testMetric() throws IOException, InterruptedException {
        for (int i=0;i<10000;i++){
            $.http.get( "http://localhost:8080/test/valid-method-spring/2");
            Thread.sleep(5);
        }
    }
}