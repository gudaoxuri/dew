package com.tairanchina.csp.dew.example.config;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

@RefreshScope
@RestController
public class TestController {

    @Autowired
    private ConfigExampleConfig configExampleConfig;

    @Value("${config-example.version}")
    private String version;

    @Scheduled(cron = "0/5 * * * * ?")
    public void executeFileDownLoadTask() throws BadPaddingException, NoSuchAlgorithmException, IllegalBlockSizeException, IOException, NoSuchPaddingException, InvalidKeyException, InvalidKeySpecException {
        System.out.println("Ctl @Value:" + version);
        System.out.println("Ctl @ConfigurationProperties:" + configExampleConfig.getVersion());
    }

    @RequestMapping("/value")
    public String value() throws BadPaddingException, InvalidKeySpecException, NoSuchAlgorithmException, IllegalBlockSizeException, NoSuchPaddingException, InvalidKeyException, IOException {
        executeFileDownLoadTask();
        return version;
    }

}
