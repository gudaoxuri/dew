package com.ecfront.dew.example.jdbc;

import com.ecfront.dew.core.Dew;
import com.ecfront.dew.core.DewBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.ComponentScan;

/**
 * 工程启动类
 */
@ComponentScan(basePackageClasses = {Dew.class, JDBCExampleApplication.class})
public class JDBCExampleApplication extends DewBootApplication {

    public static void main(String[] args) throws InterruptedException {
        new SpringApplicationBuilder(JDBCExampleApplication.class).run(args);
    }

}
