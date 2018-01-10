package com.ecfront.dew.jdbc;


import com.ecfront.dew.core.autoconfigure.DewBootApplication;
import com.ecfront.dew.Dew;
import com.ecfront.dew.core.autoconfigure.DewBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

@DewBootApplication(scanBasePackageClasses = {Dew.class,JDBCApplication.class})
public class JDBCApplication {

    public static void main(String[] args) {
        new SpringApplicationBuilder(JDBCApplication.class).run(args);
    }
}
