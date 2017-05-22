package com.ecfront.dew.auth;

import com.ecfront.dew.core.Dew;
import com.ecfront.dew.core.DewCloudApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.ComponentScan;

@ComponentScan(basePackageClasses = {Dew.class,AuthApplication.class})
public class AuthApplication extends DewCloudApplication {

    public static void main(String[] args) {
        new SpringApplicationBuilder(AuthApplication.class).web(true).run(args);
    }

}
