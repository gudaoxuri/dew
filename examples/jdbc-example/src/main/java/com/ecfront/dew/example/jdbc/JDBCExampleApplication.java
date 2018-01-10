package com.ecfront.dew.example.jdbc;

import com.ecfront.dew.Dew;
import com.ecfront.dew.core.autoconfigure.DewBootApplication;
import com.ecfront.dew.Dew;
import com.ecfront.dew.core.autoconfigure.DewBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

/**
 * 工程启动类
 */
@DewBootApplication(scanBasePackageClasses = {Dew.class, JDBCExampleApplication.class})
public class JDBCExampleApplication {

    public static void main(String[] args) throws InterruptedException {
        new SpringApplicationBuilder(JDBCExampleApplication.class).run(args);
    }

}
