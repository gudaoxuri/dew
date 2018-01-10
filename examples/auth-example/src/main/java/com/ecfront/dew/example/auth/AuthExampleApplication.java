package com.ecfront.dew.example.auth;

import com.ecfront.dew.core.autoconfigure.DewBootApplication;
import com.ecfront.dew.Dew;
import com.ecfront.dew.core.autoconfigure.DewBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

/**
 * 工程启动类
 */
@DewBootApplication(scanBasePackageClasses = {Dew.class,AuthExampleApplication.class})
public class AuthExampleApplication{

    public static void main(String[] args) {
        new SpringApplicationBuilder(AuthExampleApplication.class).run(args);
    }

}
