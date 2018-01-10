package com.ecfront.dew.idempotent;


import com.ecfront.dew.Dew;
import com.ecfront.dew.core.autoconfigure.DewBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

@DewBootApplication(scanBasePackageClasses = {Dew.class, IdempotentApplication.class})
public class IdempotentApplication {

    public static void main(String[] args) {
        new SpringApplicationBuilder(IdempotentApplication.class).run(args);
    }
}
