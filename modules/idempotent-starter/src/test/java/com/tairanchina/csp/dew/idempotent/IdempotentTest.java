package com.tairanchina.csp.dew.idempotent;

import com.ecfront.dew.common.$;
import com.ecfront.dew.common.Resp;
import com.ecfront.dew.common.StandardCode;
import com.tairanchina.csp.dew.idempotent.strategy.StatusEnum;
import com.tairanchina.csp.dew.idempotent.strategy.StrategyEnum;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = IdempotentApplication.class, webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class IdempotentTest {

    private String urlPre = "http://localhost:8080/idempotent/";

    @Test
    public void testManualConfirm() throws IOException, InterruptedException {
        HashMap<String, String> header = new HashMap<String, String>() {{
            put(DewIdempotentConfig.DEFAULT_OPT_ID_FLAG, "0001");
        }};
        Thread thread = new Thread(() -> {
            // 第一次请求，正常
            Resp<String> result = null;
            try {
                result = Resp.generic($.http.get(urlPre + "manual-confirm?str=dew-test1", header), String.class);
            } catch (IOException e) {
                e.printStackTrace();
            }
            Assert.assertTrue(result.ok());
        });
        thread.start();
        Thread.sleep(500);
        // 上一次请求还在进行中
        Resp<String> result = Resp.generic($.http.get(urlPre + "manual-confirm?str=dew-test2", header), String.class);
        Assert.assertEquals(StandardCode.CONFLICT.toString(), result.getCode());
        Thread.sleep(1000);
        // 上一次请求已确认，不能重复请求
        result = Resp.generic($.http.get(urlPre + "manual-confirm?str=dew-test3", header), String.class);
        Assert.assertEquals(StandardCode.LOCKED.toString(), result.getCode());
        Thread.sleep(4000);
        // 幂等过期，可以再次提交
        result = Resp.generic($.http.get(urlPre + "manual-confirm?str=dew-test4", header), String.class);
        Assert.assertTrue(result.ok());
        // 忽略幂等检查，强制提交
        result = Resp.generic($.http.get(urlPre + "manual-confirm?str=dew-test5"), String.class);
        Assert.assertTrue(result.ok());
    }

    @Test
    public void testAutoConfirmed() throws IOException, InterruptedException {
        HashMap<String, String> header = new HashMap<String, String>() {{
            put(DewIdempotentConfig.DEFAULT_OPT_ID_FLAG, "0001");
        }};
        // 第一次请求，正常
        Resp<String> result = Resp.generic($.http.get(urlPre + "auto-confirm?str=dew-test1", header), String.class);
        Assert.assertTrue(result.ok());
        // 上一次请求已确认，不能重复请求
        result = Resp.generic($.http.get(urlPre + "auto-confirm?str=dew-test2", header), String.class);
        Assert.assertEquals(StandardCode.LOCKED.toString(), result.getCode());
        Thread.sleep(5000);
        // 幂等过期，可以再次提交
        result = Resp.generic($.http.get(urlPre + "auto-confirm?str=dew-test3", header), String.class);
        Assert.assertTrue(result.ok());
        // 忽略幂等检查，强制提交
        result = Resp.generic($.http.get(urlPre + "auto-confirm?str=dew-test4"), String.class);
        Assert.assertTrue(result.ok());
    }

    @Test
    public void testWithoutHttp() throws IOException, InterruptedException {
        DewIdempotent.initOptTypeInfo("transfer_a", true, 1000, StrategyEnum.ITEM);
        Assert.assertEquals(StatusEnum.NOT_EXIST, DewIdempotent.process("transfer_a", "xxxxxxx"));
        Assert.assertEquals(StatusEnum.NOT_EXIST, DewIdempotent.process("transfer_a", "yyyyyyy"));
        Assert.assertEquals(StatusEnum.UN_CONFIRM, DewIdempotent.process("transfer_a", "xxxxxxx"));
        DewIdempotent.confirm("transfer_a", "xxxxxxx");
        Assert.assertEquals(StatusEnum.CONFIRMED, DewIdempotent.process("transfer_a", "xxxxxxx"));
        Thread.sleep(1000);
        Assert.assertEquals(StatusEnum.NOT_EXIST, DewIdempotent.process("transfer_a", "xxxxxxx"));
    }

    @Test
    public void testNormal() throws IOException, InterruptedException {
        Resp<String> result = Resp.generic($.http.get(urlPre + "normal?str=dew-test"), String.class);
        Assert.assertTrue(result.ok());
    }

}
