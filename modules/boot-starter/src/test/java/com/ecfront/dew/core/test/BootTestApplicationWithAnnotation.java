package com.ecfront.dew.core.test;

import com.ecfront.dew.Dew;
import com.ecfront.dew.core.autoconfigure.DewBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

@DewBootApplication(scanBasePackageClasses = {Dew.class,BootTestApplicationWithAnnotation.class})
public class BootTestApplicationWithAnnotation {

    public static void main(String[] args) {
        new SpringApplicationBuilder(BootTestApplicationWithAnnotation.class).web(true).run(args);
    }

}