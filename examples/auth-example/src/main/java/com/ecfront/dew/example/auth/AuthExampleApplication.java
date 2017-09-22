package com.ecfront.dew.example.auth;

import com.ecfront.dew.core.Dew;
import com.ecfront.dew.core.DewBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.ComponentScan;

/**
 * 工程启动类
 */
@ComponentScan(basePackageClasses = {Dew.class,DewBootApplication.class})
public class AuthExampleApplication extends DewBootApplication {

    public static void main(String[] args) {
        new SpringApplicationBuilder(AuthExampleApplication.class).run(args);
    }

}
