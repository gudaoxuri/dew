package com.ecfront.dew.core.test;

import com.ecfront.dew.core.DewBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

public class Application extends DewBootApplication {

    public static void main(String[] args) {
        new SpringApplicationBuilder(Application.class).run(args);
    }

}