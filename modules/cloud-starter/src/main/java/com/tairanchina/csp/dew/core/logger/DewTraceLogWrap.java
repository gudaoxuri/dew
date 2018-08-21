package com.tairanchina.csp.dew.core.logger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;

public class DewTraceLogWrap {

    private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    public static void request(String source, String method, String url) {
        logger.trace("[" + source + "] Request [" + method + "] " + url);
    }

    public static void response(String source, int status, String method, String url) {
        logger.trace("[" + source + "] Response " + status + " by [" + method + "] " + url);
    }

    public static void received(String source, String method, String url) {
        logger.trace("[" + source + "] Received [" + method + "] " + url);
    }

    public static void reply(String source, int status, String method, String url) {
        logger.trace("[" + source + "] Reply " + status + " by [" + method + "] " + url);
    }
}
