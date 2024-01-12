package group.idealworld.dew.example.idempotent;

import group.idealworld.dew.idempotent.DewIdempotent;
import group.idealworld.dew.idempotent.strategy.StrategyEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;

/**
 * 演示通过MQ等非HTTP调用的幂等处理.
 *
 * @author gudaoxuri
 */
@Service
public class IdempotentService {

    private static final Logger LOGGER = LoggerFactory.getLogger(IdempotentService.class);

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
        LOGGER.info("request 1 : {}", result);
        // 第二次调用
        result = transferAReceive("xxxx");
        LOGGER.info("request 2 : {}", result);
        // 第三次调用
        result = transferAReceive("xxxx");
        LOGGER.info("request 3 : {}", result);
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
