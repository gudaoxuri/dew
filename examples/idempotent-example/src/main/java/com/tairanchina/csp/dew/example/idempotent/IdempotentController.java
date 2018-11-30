package com.tairanchina.csp.dew.example.idempotent;

import com.ecfront.dew.common.Resp;
import com.tairanchina.csp.dew.idempotent.DewIdempotent;
import com.tairanchina.csp.dew.idempotent.annotations.Idempotent;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 演示通过HTTP调用的幂等处理
 */
@RestController
@RequestMapping("/idempotent/")
public class IdempotentController {

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

    @GetMapping(value = "auto-confirm")
    // 启用幂等支持，自动确认
    // 请求头部或参数加上 __IDEMPOTENT_OPT_ID__ = yy
    @Idempotent(needConfirm = false, expireMs = 5000)
    public Resp<String> autoConfirm(@RequestParam("str") String str) {
        return Resp.success(str);
    }
}
