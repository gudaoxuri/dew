package com.ecfront.dew.example.hystrix;

import com.ecfront.dew.core.DewCloudApplication;
import com.ecfront.dew.core.DewCloudApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

/**
 * 工程启动类
 */
public class HystrixExampleApplication extends DewCloudApplication {

    public static void main(String[] args) {
        new SpringApplicationBuilder(HystrixExampleApplication.class).run(args);
    }

}
