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

package group.idealworld.dew.idempotent;

import com.ecfront.dew.common.Resp;
import group.idealworld.dew.idempotent.annotations.Idempotent;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Idempotent controller.
 *
 * @author gudaoxuri
 */
@RestController
@RequestMapping("/idempotent/")
public class IdempotentController {

    /**
     * Test manual confirm.
     *
     * @param str the str
     * @return the result
     */
    @GetMapping(value = "manual-confirm")
    @Idempotent(expireMs = 5000)
    public Resp<String> testManualConfirm(@RequestParam("str") String str) {
        try {
            Thread.sleep(1000);
            DewIdempotent.confirm();
        } catch (InterruptedException e) {
            e.printStackTrace();
            Thread.currentThread().interrupt();
        }
        return Resp.success(str);
    }

    /**
     * Test auto confirm.
     *
     * @param str the str
     * @return the result
     */
    @GetMapping(value = "auto-confirm")
    @Idempotent(needConfirm = false, expireMs = 5000)
    public Resp<String> testAutoConfirm(@RequestParam("str") String str) {
        return Resp.success(str);
    }

    /**
     * Normal.
     *
     * @param str the str
     * @return the result
     */
    @GetMapping(value = "normal")
    public Resp<String> normal(@RequestParam("str") String str) {
        return Resp.success(str);
    }


}
