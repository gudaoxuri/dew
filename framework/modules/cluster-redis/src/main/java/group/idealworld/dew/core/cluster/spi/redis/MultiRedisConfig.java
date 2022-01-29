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

package group.idealworld.dew.core.cluster.spi.redis;


import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.HashMap;
import java.util.Map;

/**
 * Multi redis config.
 *
 * @author gudaoxuri
 */
@ConfigurationProperties(prefix = "spring.redis")
public class MultiRedisConfig {

    private Map<String, RedisProperties> multi = new HashMap<>();

    /**
     * Gets multi.
     *
     * @return the multi
     */
    public Map<String, RedisProperties> getMulti() {
        return multi;
    }

    /**
     * Sets multi.
     *
     * @param multi the multi
     */
    public void setMulti(Map<String, RedisProperties> multi) {
        this.multi = multi;
    }
}
