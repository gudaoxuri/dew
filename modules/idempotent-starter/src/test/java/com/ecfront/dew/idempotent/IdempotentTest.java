package com.ecfront.dew.idempotent;

import com.ecfront.dew.common.$;
import com.ecfront.dew.common.Resp;
import com.ecfront.dew.common.StandardCode;
import com.ecfront.dew.idempotent.strategy.StatusEnum;
import com.ecfront.dew.idempotent.strategy.StrategyEnum;
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
        HashMap<String, String> hashMap = new HashMap<String, String>() {{
            put(DewIdempotentConfig.DEFAULT_OPT_TYPE_FLAG, "manualConfirm");
            put(DewIdempotentConfig.DEFAULT_OPT_ID_FLAG, "0001");
        }};
        new Thread(new NeedTask(hashMap)).start();
        Thread.sleep(1000);
        // 上一次请求还在进行中
        Resp<String> result2 = Resp.generic($.http.get(urlPre + "manual-confirm?str=dew-test2", hashMap), String.class);
        Assert.assertEquals(StandardCode.CONFLICT.toString(), result2.getCode());
        Thread.sleep(1000);
        // 上一次请求已确认，不能重复请求
        result2 = Resp.generic($.http.get(urlPre + "manual-confirm?str=dew-test3", hashMap), String.class);
        Assert.assertEquals(StandardCode.LOCKED.toString(), result2.getCode());
        Thread.sleep(3500);
        // 幂等过期，可以再次提交
        result2 = Resp.generic($.http.get(urlPre + "manual-confirm?str=dew-test4", hashMap), String.class);
        Assert.assertTrue(result2.ok());
    }

    @Test
    public void testAutoConfirmed() throws IOException, InterruptedException {
        HashMap<String, String> hashMap = new HashMap<String, String>() {{
            put(DewIdempotentConfig.DEFAULT_OPT_TYPE_FLAG, "autoConfirm");
            put(DewIdempotentConfig.DEFAULT_OPT_ID_FLAG, "0001");
        }};
        // 第一次请求，正常
        Resp<String> result = Resp.generic($.http.get(urlPre + "auto-confirm?str=dew-test", hashMap), String.class);
        Assert.assertTrue(result.ok());
        // 上一次请求已确认，不能重复请求
        result = Resp.generic($.http.get(urlPre + "auto-confirm?str=dew-test", hashMap), String.class);
        Assert.assertEquals(StandardCode.LOCKED.toString(), result.getCode());
        Thread.sleep(5000);
        // 幂等过期，可以再次提交
        result = Resp.generic($.http.get(urlPre + "auto-confirm?str=dew-test", hashMap), String.class);
        Assert.assertTrue(result.ok());
    }

    @Test
    public void testCancel() throws InterruptedException, IOException {
        // needconfirm
        HashMap<String, String> needMap = new HashMap<String, String>() {{
            put(DewIdempotentConfig.DEFAULT_OPT_TYPE_FLAG, "manualConfirm");
            put(DewIdempotentConfig.DEFAULT_OPT_ID_FLAG, "0001");
        }};
        new Thread(new NeedTask(needMap)).start();
        Thread.sleep(2000);
        Resp<String> result = Resp.generic($.http.get(urlPre + "manual-confirm?str=dew-test1", needMap), String.class);
        Assert.assertEquals(StandardCode.LOCKED.toString(), result.getCode());
        // 删除缓存
        DewIdempotent.cancel("manualConfirm", "0001");
        result = Resp.generic($.http.get(urlPre + "manual-confirm?str=dew-test1", needMap), String.class);
        Assert.assertTrue(result.ok());


        // autoconfirm
        HashMap<String, String> autoMap = new HashMap<String, String>() {{
            put(DewIdempotentConfig.DEFAULT_OPT_TYPE_FLAG, "autoConfirm");
            put(DewIdempotentConfig.DEFAULT_OPT_ID_FLAG, "0001");
        }};
        // 第一次请求，正常
        result = Resp.generic($.http.get(urlPre + "auto-confirm?str=dew-test", autoMap), String.class);
        Assert.assertTrue(result.ok());
        // 上一次请求已确认，不能重复请求
        result = Resp.generic($.http.get(urlPre + "auto-confirm?str=dew-test", autoMap), String.class);
        Assert.assertEquals(StandardCode.LOCKED.toString(), result.getCode());
        // 删除缓存
        DewIdempotent.cancel("autoConfirm", "0001");
        result = Resp.generic($.http.get(urlPre + "auto-confirm?str=dew-test", autoMap), String.class);
        Assert.assertTrue(result.ok());


        // cancel
        HashMap<String, String> cancelMap = new HashMap<String, String>() {{
            put(DewIdempotentConfig.DEFAULT_OPT_TYPE_FLAG, "cancleConfirm");
            put(DewIdempotentConfig.DEFAULT_OPT_ID_FLAG, "0001");
        }};
        String s = $.http.get(urlPre + "cancel?str=dew-cancel", cancelMap);
        result = Resp.generic($.http.get(urlPre + "cancel?str=dew-cancel", cancelMap), String.class);
        Assert.assertTrue(!result.ok());
        result = Resp.generic($.http.get(urlPre + "cancel?str=dew-cancel", cancelMap), String.class);
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

    private class NeedTask implements Runnable {

        private Map<String, String> map;

        NeedTask(Map<String, String> map) {
            this.map = map;
        }

        @Override
        public void run() {
            Resp<String> result = null;
            try {
                result = Resp.generic($.http.get(urlPre + "manual-confirm?str=dew-test1", map), String.class);
            } catch (IOException e) {
                e.printStackTrace();
            }
            Assert.assertTrue(result.ok());
        }
    }

}
