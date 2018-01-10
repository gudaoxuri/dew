package com.ecfront.dew.example.web;

import com.ecfront.dew.core.autoconfigure.DewBootApplication;
import com.ecfront.dew.Dew;
import com.ecfront.dew.core.autoconfigure.DewBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

/**
 * 工程启动类
 */
@DewBootApplication(scanBasePackageClasses = {Dew.class,WebExampleApplication.class})
public class WebExampleApplication {

    public static void main(String[] args) {
        new SpringApplicationBuilder(WebExampleApplication.class).run(args);
    }

}
