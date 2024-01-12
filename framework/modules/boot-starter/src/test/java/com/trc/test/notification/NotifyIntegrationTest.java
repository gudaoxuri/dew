package com.trc.test.notification;

import com.ecfront.dew.common.Resp;
import group.idealworld.dew.Dew;
import org.junit.jupiter.api.Assertions;
import org.springframework.stereotype.Component;

/**
 * The type Notify integration test.
 *
 * @author gudaoxuri
 */
@Component
public class NotifyIntegrationTest {

    /**
     * Test all.
     */
    public void testAll() {
        Resp<Void> result = Dew.notify.send("flag1", "测试消息，默认通知人", "测试");
        Assertions.assertTrue(result.ok());
    }

}
