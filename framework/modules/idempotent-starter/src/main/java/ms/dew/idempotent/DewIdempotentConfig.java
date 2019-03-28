/*
 * Copyright 2019. the original author or authors.
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

package ms.dew.idempotent;

import ms.dew.idempotent.strategy.StrategyEnum;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "dew.idempotent")
public class DewIdempotentConfig {

    public static final int DEFAULT_EXPIRE_MS = 3600000;
    public static final StrategyEnum DEFAULT_STRATEGY = StrategyEnum.ITEM;
    public static final String DEFAULT_OPT_ID_FLAG = "__IDEMPOTENT_OPT_ID__";

    // 设置默认过期时间，1小时
    private long defaultExpireMs = DEFAULT_EXPIRE_MS;
    // 设置默认策略，支持 bloom(Bloom Filter)和item(逐条记录)
    private StrategyEnum defaultStrategy = DEFAULT_STRATEGY;
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

    public String getDefaultOptIdFlag() {
        return defaultOptIdFlag;
    }

    public void setDefaultOptIdFlag(String defaultOptIdFlag) {
        this.defaultOptIdFlag = defaultOptIdFlag;
    }
}
