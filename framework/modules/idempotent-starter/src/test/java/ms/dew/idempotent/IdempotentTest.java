/*
 * Copyright 2019. the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ms.dew.idempotent;

import com.ecfront.dew.common.$;
import com.ecfront.dew.common.Resp;
import com.ecfront.dew.common.StandardCode;
import ms.dew.idempotent.strategy.StatusEnum;
import ms.dew.idempotent.strategy.StrategyEnum;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.util.HashMap;

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
        // 初始化类型为transfer_a的幂等操作，需要手工确认，过期时间为1秒
        DewIdempotent.initOptTypeInfo("transfer_a", true, 1000, StrategyEnum.ITEM);
        // 第一次请求transfer_a类型下的xxxxxxx这个ID，返回不存在，表示可以下一步操作
        Assert.assertEquals(StatusEnum.NOT_EXIST, DewIdempotent.process("transfer_a", "xxxxxxx"));
        Assert.assertEquals(StatusEnum.NOT_EXIST, DewIdempotent.process("transfer_a", "yyyyyyy"));
        // 第二次请求transfer_a类型下的xxxxxxx这个ID，返回未确认，表示上次操作还在进行中
        Assert.assertEquals(StatusEnum.UN_CONFIRM, DewIdempotent.process("transfer_a", "xxxxxxx"));
        // 确认操作完成
        DewIdempotent.confirm("transfer_a", "xxxxxxx");
        // 第三次请求transfer_a类型下的xxxxxxx这个ID，返回已确认，但未过期，仍不能操作
        Assert.assertEquals(StatusEnum.CONFIRMED, DewIdempotent.process("transfer_a", "xxxxxxx"));
        // 延时1秒
        Thread.sleep(1000);
        // 再次请求transfer_a类型下的xxxxxxx这个ID，返回不存在（上次请求已过期），表示可以下一步操作
        Assert.assertEquals(StatusEnum.NOT_EXIST, DewIdempotent.process("transfer_a", "xxxxxxx"));
    }

    @Test
    public void testNormal() throws IOException, InterruptedException {
        Resp<String> result = Resp.generic($.http.get(urlPre + "normal?str=dew-test"), String.class);
        Assert.assertTrue(result.ok());
    }

}
