package com.ecfront.dew.example.sleuth;

import com.ecfront.dew.core.DewCloudApplication;
import com.ecfront.dew.core.DewCloudApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.cloud.netflix.feign.EnableFeignClients;

@EnableFeignClients
public class SleuthExampleApplication extends DewCloudApplication {

    public static void main(String[] args) {
        new SpringApplicationBuilder(SleuthExampleApplication.class).run(args);
    }

}
