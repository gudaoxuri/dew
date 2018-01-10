package com.ecfront.dew.example.cache;

import com.ecfront.dew.Dew;
import com.ecfront.dew.core.autoconfigure.DewBootApplication;
import com.ecfront.dew.Dew;
import com.ecfront.dew.core.autoconfigure.DewBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

/**
 * 工程启动类
 */
@DewBootApplication(scanBasePackageClasses = {Dew.class,CacheExampleApplication.class})
public class CacheExampleApplication {

    public static void main(String[] args) {
        new SpringApplicationBuilder(CacheExampleApplication.class).run(args);
    }

}
