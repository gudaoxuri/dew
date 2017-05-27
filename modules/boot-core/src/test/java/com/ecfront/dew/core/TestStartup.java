package com.ecfront.dew.core;

import org.springframework.boot.builder.SpringApplicationBuilder;

public class TestStartup extends DewBootApplication {

    public static void main(String[] args) {
        new SpringApplicationBuilder(TestStartup.class).web(true).run(args);
    }

}
