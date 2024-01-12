package com.trc.test;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

/**
 * Boot test application.
 *
 * @author gudaoxuri
 */
@SpringBootApplication
public class BootTestApplication {

    /**
     * The entry point of application.
     *
     * @param args the input arguments
     */
    public static void main(String[] args) {
        new SpringApplicationBuilder(BootTestApplication.class).run(args);
    }

}
