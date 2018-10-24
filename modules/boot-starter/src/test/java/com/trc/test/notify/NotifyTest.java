package com.trc.test.notify;


import com.ecfront.dew.common.Resp;
import com.tairanchina.csp.dew.Dew;
import org.junit.Assert;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.HashSet;
import java.util.stream.Collectors;

@Component
public class NotifyTest {

    public void testAll() throws Exception {
        Resp<Void> result = Dew.notify.send("flag1", "测试消息，默认通知人", "测试");
        Assert.assertTrue(result.ok());
        result = Dew.notify.send("flag1", "测试消息，加上指定通知人", "测试", new HashSet<String>() {{
            add("15658132456");
        }});
        Assert.assertTrue(result.ok());
        result = Dew.notify.send("flag1", "测试消息，带符号\r\n\"#sf@!&^$(Q@^Q)*UR#", "测试");
        Assert.assertTrue(result.ok());
        Dew.notify.sendAsync("flag1", "测试消息，异步发送", "测试");

        try {
            int i = 1 / 0;
        } catch (Exception e) {
            result = Dew.notify.send("flag1", "异常" + Arrays.stream(e.fillInStackTrace().getStackTrace()).map(StackTraceElement::toString).collect(Collectors.joining("\r\n")), "测试");
            Assert.assertTrue(result.ok());
        }

        result = Dew.notify.send(Dew.dewConfig.getBasic().getFormat().getErrorFlag(), "测试消息，带时间限制，5s只会发一次", "测试");
        Assert.assertTrue(result.ok());
        result = Dew.notify.send(Dew.dewConfig.getBasic().getFormat().getErrorFlag(), "测试消息，带时间限制，5s只会发一次", "测试");
        Assert.assertFalse(result.ok());
        Thread.sleep(5000);
        result = Dew.notify.send(Dew.dewConfig.getBasic().getFormat().getErrorFlag(), "测试消息，带时间限制，5s只会发一次", "测试");
        Assert.assertTrue(result.ok());

        result = Dew.notify.send("flag2", "测试消息，有免扰时间，超过2次才发送", "测试");
        Assert.assertFalse(result.ok());
        result = Dew.notify.send("flag2", "测试消息，有免扰时间，超过2次才发送", "测试");
        Assert.assertFalse(result.ok());
        result = Dew.notify.send("flag2", "测试消息，有免扰时间，超过2次才发送", "测试");
        Assert.assertTrue(result.ok());


        /*result = Dew.notify.send("sendMail", "测试消息", "测试");
        Assert.assertTrue(result.ok());*/
    }

}
