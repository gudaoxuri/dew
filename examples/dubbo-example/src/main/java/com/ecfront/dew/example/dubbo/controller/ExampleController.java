package com.ecfront.dew.example.dubbo.controller;


import com.alibaba.dubbo.config.annotation.Reference;
import com.ecfront.dew.example.dubbo.service.DubboAPI;
import com.ecfront.dew.example.dubbo.service.DubboWithTransactionAPI;
import org.springframework.context.annotation.Lazy;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Lazy // 让Dubbo服务先启动
public class ExampleController {

    @Reference(version = "1.0.0")
    private DubboAPI dubboAPI;

    @Reference(version = "1.0.0")
    private DubboWithTransactionAPI dubboWithTransactionAPI;

    @GetMapping("/example")
    public String example() {
        String id = dubboAPI.getRandomId();
        assert id != null;
        return dubboWithTransactionAPI.saveRandomId(id);
    }

}
