package com.tairanchina.csp.dew.auth.sdk;

import com.tairanchina.csp.dew.Dew;
import com.tairanchina.csp.dew.core.DewContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

@Configuration
@AutoConfigureAfter(Dew.class)
public class AuthSDKAutoConfiguration {

    private static final Logger logger = LoggerFactory.getLogger(AuthSDKAutoConfiguration.class);

    @PostConstruct
    private void init() {
        logger.info("Load Auto Configuration : {}", this.getClass().getName());
        logger.info("Enabled Dew Auth SDK");
        Dew.auth = new AuthSDKAdapter();
        DewContext.setOptInfoClazz(TokenInfo.class);
    }

}