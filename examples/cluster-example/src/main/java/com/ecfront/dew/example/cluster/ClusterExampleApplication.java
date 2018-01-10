package com.ecfront.dew.example.cluster;

import com.ecfront.dew.Dew;
import com.ecfront.dew.core.autoconfigure.DewBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

/**
 * 工程启动类
 */
@DewBootApplication(scanBasePackageClasses = {Dew.class,ClusterExampleApplication.class})
public class ClusterExampleApplication{

    public static void main(String[] args) {
        new SpringApplicationBuilder(ClusterExampleApplication.class).run(args);
    }

}
