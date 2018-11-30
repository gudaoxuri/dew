package com.tairanchina.csp.dew.example.rest;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class CloudExampleInitiator {

    @Value("${conf.load}")
    private String confLoadSort;

    @PostConstruct
    public void init() {
        System.out.println("conf.load:" + confLoadSort);
    }

}
