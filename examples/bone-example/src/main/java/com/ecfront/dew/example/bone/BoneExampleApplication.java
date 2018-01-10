package com.ecfront.dew.example.bone;

import com.ecfront.dew.Dew;
import com.ecfront.dew.core.autoconfigure.DewBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

/**
 * 工程启动类
 */
@DewBootApplication(scanBasePackageClasses = {Dew.class,BoneExampleApplication.class})
public class BoneExampleApplication {

    public static void main(String[] args) {
        new SpringApplicationBuilder(BoneExampleApplication.class).run(args);
    }

}
