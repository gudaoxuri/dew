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

package group.idealworld.dew.idempotent;

import group.idealworld.dew.idempotent.strategy.StrategyEnum;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Dew idempotent config.
 *
 * @author gudaoxuri
 */
@Component
@ConfigurationProperties(prefix = "dew.idempotent")
public class DewIdempotentConfig {

    /**
     * 默认超时时间.
     */
    public static final int DEFAULT_EXPIRE_MS = 3600000;
    /**
     * 默认策略.
     */
    public static final StrategyEnum DEFAULT_STRATEGY = StrategyEnum.ITEM;
    /**
     * 策略幂等ID标识.
     */
    public static final String DEFAULT_OPT_ID_FLAG = "__IDEMPOTENT_OPT_ID__";

    // 设置默认过期时间，1小时
    private long defaultExpireMs = DEFAULT_EXPIRE_MS;
    // 设置默认策略，支持 bloom(Bloom Filter)和item(逐条记录)
    private StrategyEnum defaultStrategy = DEFAULT_STRATEGY;
    // 指定幂等操作ID标识，可以位于HTTP Header或请求参数中
    private String defaultOptIdFlag = DEFAULT_OPT_ID_FLAG;

    /**
     * Gets default expire ms.
     *
     * @return the default expire ms
     */
    public long getDefaultExpireMs() {
        return defaultExpireMs;
    }

    /**
     * Sets default expire ms.
     *
     * @param defaultExpireMs the default expire ms
     */
    public void setDefaultExpireMs(long defaultExpireMs) {
        this.defaultExpireMs = defaultExpireMs;
    }

    /**
     * Gets default strategy.
     *
     * @return the default strategy
     */
    public StrategyEnum getDefaultStrategy() {
        return defaultStrategy;
    }

    /**
     * Sets default strategy.
     *
     * @param defaultStrategy the default strategy
     */
    public void setDefaultStrategy(StrategyEnum defaultStrategy) {
        this.defaultStrategy = defaultStrategy;
    }

    /**
     * Gets default opt id flag.
     *
     * @return the default opt id flag
     */
    public String getDefaultOptIdFlag() {
        return defaultOptIdFlag;
    }

    /**
     * Sets default opt id flag.
     *
     * @param defaultOptIdFlag the default opt id flag
     */
    public void setDefaultOptIdFlag(String defaultOptIdFlag) {
        this.defaultOptIdFlag = defaultOptIdFlag;
    }
}
