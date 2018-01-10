package com.ecfront.dew.example.dubbo;

import com.ecfront.dew.Dew;
import com.ecfront.dew.core.autoconfigure.DewBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

@DewBootApplication(scanBasePackageClasses = {Dew.class,DubboExampleApplication.class})
public class DubboExampleApplication{

    public static void main(String[] args) {
        new SpringApplicationBuilder(DubboExampleApplication.class).run(args);
    }

}
