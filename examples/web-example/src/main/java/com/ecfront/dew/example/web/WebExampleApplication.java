package com.ecfront.dew.example.web;

import com.ecfront.dew.core.Dew;
import com.ecfront.dew.core.DewBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.ComponentScan;

/**
 * 工程启动类
 */
@ComponentScan(basePackageClasses = {Dew.class,WebExampleApplication.class})
public class WebExampleApplication extends DewBootApplication {

    public static void main(String[] args) {
        new SpringApplicationBuilder(WebExampleApplication.class).run(args);
    }

}
