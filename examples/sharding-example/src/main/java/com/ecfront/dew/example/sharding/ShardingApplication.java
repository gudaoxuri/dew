package com.ecfront.dew.example.sharding;


import com.ecfront.dew.Dew;
import com.ecfront.dew.core.autoconfigure.DewBootApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.freemarker.FreeMarkerAutoConfiguration;
import org.springframework.boot.autoconfigure.gson.GsonAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.websocket.WebSocketAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;

@DewBootApplication(scanBasePackageClasses = {Dew.class, ShardingApplication.class}, exclude = {FreeMarkerAutoConfiguration.class, GsonAutoConfiguration.class, WebSocketAutoConfiguration.class, DataSourceAutoConfiguration.class})
public class ShardingApplication {

    public static void main(String[] args) {
        new SpringApplicationBuilder(ShardingApplication.class).run(args);
    }
}
