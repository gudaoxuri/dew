/*
 * Copyright 2022. the original author or authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package group.idealworld.dew.example.idempotent;

import group.idealworld.dew.idempotent.DewIdempotent;
import group.idealworld.dew.idempotent.strategy.StrategyEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

/**
 * 演示通过MQ等非HTTP调用的幂等处理.
 *
 * @author gudaoxuri
 */
@Service
public class IdempotentService {

    private static final Logger logger = LoggerFactory.getLogger(IdempotentService.class);

    /**
     * Init.
     */
    @PostConstruct
    public void init() {
        // 初始化用到的操作类型
        DewIdempotent.initOptTypeInfo("transfer_a", true, 1000, StrategyEnum.ITEM);
        DewIdempotent.initOptTypeInfo("transfer_b", true, 1000, StrategyEnum.ITEM);
        // ……
        send();
    }

    /**
     * Send.
     */
    public void send() {
        // 第一次调用
        int result = transferAReceive("xxxx");
        logger.info("request 1 : {}", result);
        // 第二次调用
        result = transferAReceive("xxxx");
        logger.info("request 2 : {}", result);
        // 第三次调用
        result = transferAReceive("xxxx");
        logger.info("request 3 : {}", result);
    }

    /**
     * Transfer a receive int.
     *
     * @param id the id
     * @return the int
     */
    public int transferAReceive(String id) {
        // 收到请求后先调用process
        switch (DewIdempotent.process("transfer_a", id)) {
            case NOT_EXIST:
                // 业务操作
                // ...
                // 手工确认
                DewIdempotent.confirm();
                return 1;
            case UN_CONFIRM:
                // 已收到但操作还未确认
                // 对应的处理，一般返回请求等待，稍后重试
                return 2;
            case CONFIRMED:
                // 已确认操作
                // 对应的处理，一般直接返回失败
                return 3;
            default:
                return -1;
        }

    }

}
