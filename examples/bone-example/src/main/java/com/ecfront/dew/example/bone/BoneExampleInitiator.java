package com.ecfront.dew.example.bone;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * 在根路径创建用于初始化数据/行为的类
 * <p>
 * 减少滥用PostConstruct造成的不可控因素
 */
@Component
public class BoneExampleInitiator {

    private static final Logger logger = LoggerFactory.getLogger(BoneExampleInitiator.class);

    @Autowired
    private BoneExampleConfig boneExampleConfig;

    @PostConstruct
    public void init() {
        // 在这里初始化
        logger.info(">>>> " + boneExampleConfig.getSomeProp());

    }

}
