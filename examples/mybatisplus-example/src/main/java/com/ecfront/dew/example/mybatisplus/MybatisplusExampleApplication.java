package com.ecfront.dew.example.mybatisplus;

import com.ecfront.dew.Dew;
import com.ecfront.dew.core.autoconfigure.DewBootApplication;
import com.ecfront.dew.Dew;
import com.ecfront.dew.core.autoconfigure.DewBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

@DewBootApplication(scanBasePackageClasses = {Dew.class, MybatisplusExampleApplication.class})
public class MybatisplusExampleApplication {

    public static void main(String[] args) throws InterruptedException {
        new SpringApplicationBuilder(MybatisplusExampleApplication.class).run(args);
    }
}
