package com.trc.test;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

@SpringBootApplication
public class BootTestApplicationWithAnnotation {

    public static void main(String[] args) {
        new SpringApplicationBuilder(BootTestApplicationWithAnnotation.class).web(true).run(args);
    }

}