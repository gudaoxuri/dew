package com.tairanchina.csp.dew.idempotent;

import com.tairanchina.csp.dew.idempotent.strategy.StrategyEnum;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "dew.idempotent")
public class DewIdempotentConfig {

    public static final int DEFAULT_EXPIRE_MS = 3600000;
    public static final StrategyEnum DEFAULT_STRATEGY = StrategyEnum.ITEM;
    public static final String DEFAULT_OPT_TYPE_FLAG = "__IDEMPOTENT_OPT_TYPE__";
    public static final String DEFAULT_OPT_ID_FLAG = "__IDEMPOTENT_OPT_ID__";

    // 设置默认过期时间，1小时
    private long defaultExpireMs = DEFAULT_EXPIRE_MS;
    // 设置默认策略，支持 bloom(Bloom Filter)和item(逐条记录)
    private StrategyEnum defaultStrategy = DEFAULT_STRATEGY;
    // 指定幂等操作类型标识，可以位于HTTP Header或请求参数中
    private String defaultOptTypeFlag = DEFAULT_OPT_TYPE_FLAG;
    // 指定幂等操作ID标识，可以位于HTTP Header或请求参数中
    private String defaultOptIdFlag = DEFAULT_OPT_ID_FLAG;

    public long getDefaultExpireMs() {
        return defaultExpireMs;
    }

    public void setDefaultExpireMs(long defaultExpireMs) {
        this.defaultExpireMs = defaultExpireMs;
    }

    public StrategyEnum getDefaultStrategy() {
        return defaultStrategy;
    }

    public void setDefaultStrategy(StrategyEnum defaultStrategy) {
        this.defaultStrategy = defaultStrategy;
    }

    public String getDefaultOptTypeFlag() {
        return defaultOptTypeFlag;
    }

    public void setDefaultOptTypeFlag(String defaultOptTypeFlag) {
        this.defaultOptTypeFlag = defaultOptTypeFlag;
    }

    public String getDefaultOptIdFlag() {
        return defaultOptIdFlag;
    }

    public void setDefaultOptIdFlag(String defaultOptIdFlag) {
        this.defaultOptIdFlag = defaultOptIdFlag;
    }
}
