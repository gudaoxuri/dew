package com.ecfront.dew.idempotent;

import com.ecfront.dew.core.loding.DewLoadImmediately;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

@Configuration
@DewLoadImmediately
public class DewIdempotentAutoConfiguration {

    private static final Logger logger = LoggerFactory.getLogger(DewIdempotentAutoConfiguration.class);

    @Autowired
    private DewIdempotentConfig dewIdempotentConfig;
    @Autowired
    private DewIdempotent dewIdempotent;

    @PostConstruct
    private void init() {
        logger.info("Enabled Dew Idempotent");
    }

}