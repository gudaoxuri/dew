package com.ecfront.dew.auth;

import com.ecfront.dew.core.DewApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

public class AuthApplication extends DewApplication {

    public static void main(String[] args) {
        new SpringApplicationBuilder(AuthApplication.class).web(true).run(args);
    }

}
