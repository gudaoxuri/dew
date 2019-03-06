package com.tairanchina.csp.dew.core.doc;


import com.tairanchina.csp.dew.Dew;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;

@Configuration
@ConditionalOnMissingClass("org.springframework.cloud.client.discovery.DiscoveryClient")
public class DocLocalAutoConfiguration {

    @Value("${server.ssl.key-store:}")
    private String localSSLKeyStore;
    @Value("${server.context-path:}")
    private String localContextPath;

    @Bean
    public DocController docController() {
        return new DocController(() ->
                new ArrayList<String>() {{
                    add((localSSLKeyStore == null || localSSLKeyStore.isEmpty() ? "http" : "https") + "://localhost:" + Dew.Info.webPort + localContextPath + "/v2/api-docs");
                }}
        );
    }

}
