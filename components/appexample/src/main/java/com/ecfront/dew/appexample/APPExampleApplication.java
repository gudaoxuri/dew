package com.ecfront.dew.appexample;

import com.ecfront.dew.core.DewApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

public class APPExampleApplication extends DewApplication {

    public static void main(String[] args) {
        new SpringApplicationBuilder(APPExampleApplication.class).web(true).run(args);
    }

}
