package com.ecfront.dew.appexample.controller;

import com.ecfront.dew.appexample.entity.TestEntity;
import com.ecfront.dew.appexample.repository.TestRepository;
import com.ecfront.dew.common.Resp;
import com.ecfront.dew.core.Dew;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping(value = "/ws")
public class WSController {

    private static final Logger logger = LoggerFactory.getLogger(WSController.class);

    @Autowired
    RestTemplate restTemplate;

    @Autowired
    TestRepository testRepository;

    @PostMapping(value = "")
    @ResponseBody
    public Resp<String> ws(@RequestBody String message) throws Exception {
        if (Dew.context().getToken() == null || Dew.context().getToken().isEmpty()) {
            throw new Exception("");
        }
        TestEntity entity = new TestEntity();
        entity.setToken(Dew.context().getToken());
        entity.setMessage(message);
        logger.info("info test");
        logger.trace("trace test");
        logger.debug("debug test");
        testRepository.save(entity);
        Dew.Timer.periodic(10L, () ->
                testRepository.findAll().forEach(
                        i ->
                                restTemplate.postForEntity(Dew.EB.buildUrl("wsgateway", "push", i.getToken()), "push>>" + i.getMessage(), String.class)
                )
        );
        return Resp.success(message);
    }


}
