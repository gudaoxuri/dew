package com.ecfront.dew.example.sleuth;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/")
public class SleuthExampleController {

    private static final Logger logger = LoggerFactory.getLogger(SleuthExampleController.class);

    @Autowired
    private SleuthInvoke1Client sleuthInvoke1Client;

    @PostMapping("ping")
    public String ping(@RequestBody SomeVO vo) {
        logger.info("收到请求");
        return sleuthInvoke1Client.pong(vo.code);
    }

    public static class SomeVO {
        private String code;

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }
    }


}
