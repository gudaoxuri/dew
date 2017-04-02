package com.ecfront.dew.appexample.service;

import com.ecfront.dew.common.Resp;
import com.ecfront.dew.core.Dew;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class HystrixService {

    private static final Logger logger = LoggerFactory.getLogger(HystrixService.class);

    @HystrixCommand(fallbackMethod = "errorGet")
    public Resp<String> doGet() throws Exception {
        Dew.Service.cache.opsForValue().set("a", "b");
        logger.info("cache:" + Dew.Service.cache.opsForValue().get("a"));
        throw new Exception("。。。");
        //return Resp.success("ok");
    }

    public Resp<String> errorGet() throws Exception {
        logger.info("errorGet");
        throw new Exception("错误。。。");
    }

}
