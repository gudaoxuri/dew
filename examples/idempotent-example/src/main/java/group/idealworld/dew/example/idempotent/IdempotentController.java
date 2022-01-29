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

package group.idealworld.dew.example.idempotent;

import com.ecfront.dew.common.Resp;
import group.idealworld.dew.idempotent.DewIdempotent;
import group.idealworld.dew.idempotent.annotations.Idempotent;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 演示通过HTTP调用的幂等处理.
 *
 * @author gudaoxuri
 */
@RestController
@RequestMapping("/idempotent/")
public class IdempotentController {

    /**
     * Manual confirm.
     *
     * @param str the str
     * @return the resp
     */
    @GetMapping(value = "manual-confirm")
    // 启用幂等支持
    // 请求头部或参数加上 __IDEMPOTENT_OPT_ID__ = yy
    @Idempotent(expireMs = 5000)
    public Resp<String> manualConfirm(@RequestParam("str") String str) {
        try {
            Thread.sleep(1000);
            // 手工确认
            DewIdempotent.confirm();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return Resp.success(str);
    }

    /**
     * Auto confirm.
     *
     * @param str the str
     * @return the resp
     */
    @GetMapping(value = "auto-confirm")
    // 启用幂等支持，自动确认
    // 请求头部或参数加上 __IDEMPOTENT_OPT_ID__ = yy
    @Idempotent(needConfirm = false, expireMs = 5000)
    public Resp<String> autoConfirm(@RequestParam("str") String str) {
        return Resp.success(str);
    }
}
