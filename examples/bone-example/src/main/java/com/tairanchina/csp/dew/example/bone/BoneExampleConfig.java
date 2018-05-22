package com.tairanchina.csp.dew.example.bone;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 在根路径创建配置隐射类
 */
@Component
@ConfigurationProperties(prefix = "bone-example")
public class BoneExampleConfig {

    private String someProp;

    public String getSomeProp() {
        return someProp;
    }

    public void setSomeProp(String someProp) {
        this.someProp = someProp;
    }

}
