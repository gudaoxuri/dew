package com.ecfront.dew.example.idempotent;


import com.ecfront.dew.Dew;
import com.ecfront.dew.core.autoconfigure.DewBootApplication;
import com.ecfront.dew.Dew;
import com.ecfront.dew.core.autoconfigure.DewBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

@DewBootApplication(scanBasePackageClasses = {Dew.class,IdempotentExampleApplication.class})
public class IdempotentExampleApplication {

    public static void main(String[] args) {
        new SpringApplicationBuilder(IdempotentExampleApplication.class).run(args);
    }
}
