package com.ecfront.dew.example.moinitor;

import com.ecfront.dew.core.DewCloudApplication;
import com.ecfront.dew.core.DewCloudApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

/**
 * 工程启动类
 */
public class MonitorClientApplication extends DewCloudApplication {

    public static void main(String[] args) {
        new SpringApplicationBuilder(MonitorClientApplication.class).run(args);
    }

}
