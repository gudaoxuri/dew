package com.ecfront.dew.example.hystrix;

import com.ecfront.dew.core.DewCloudApplication;
import com.ecfront.dew.core.DewCloudApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.cloud.netflix.feign.EnableFeignClients;

/**
 * 工程启动类
 */
@EnableFeignClients
public class HystrixExampleApplication extends DewCloudApplication {

    public static void main(String[] args) {
        new SpringApplicationBuilder(HystrixExampleApplication.class).run(args);
    }

}
