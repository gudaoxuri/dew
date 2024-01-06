/*
 * Copyright 2022. the original author or authors
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
