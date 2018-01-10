package com.ecfront.dew.example.sleuth;

import com.ecfront.dew.common.$;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * desription:
 * Created by ding on 2017/11/1.
 */
public class TestEntry {
    Logger logger = LoggerFactory.getLogger(TestEntry.class);

    @Test
    public void testSleuth() throws IOException, InterruptedException {
        String result = $.http.get("http://localhost:8081/ping?code=sleuth-test");
        logger.info("result：   "+result);
        for (int i = 0;i<300;i++){
            $.http.get("http://localhost:8081/ping?code=sleuth-test");
            Thread.sleep(5000);
        }
    }
}
