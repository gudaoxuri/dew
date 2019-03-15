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

package com.tairanchina.csp.dew.core.cluster.spi.redis;

import com.ecfront.dew.common.$;
import com.tairanchina.csp.dew.core.cluster.AbsClusterElection;
import com.tairanchina.csp.dew.core.cluster.Cluster;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;

public class RedisClusterElection extends AbsClusterElection {

    private static final String DEFAULT_KEY = "_";

    private String key;
    private int electionPeriodSec;
    private RedisTemplate<String, String> redisTemplate;

    public RedisClusterElection(int electionPeriodSec, RedisTemplate<String, String> redisTemplate) {
        this(DEFAULT_KEY, electionPeriodSec, redisTemplate);
    }

    public RedisClusterElection(String key, int electionPeriodSec, RedisTemplate<String, String> redisTemplate) {
        this.key = "dew:cluster:election:" + key;
        this.electionPeriodSec = electionPeriodSec;
        this.redisTemplate = redisTemplate;
        election();
    }

    @Override
    public void election() {
        $.timer.periodic(electionPeriodSec, false, this::doElection);
    }

    private void doElection() {
        logger.trace("[Election] electing...");
        byte[] rawKey = redisTemplate.getStringSerializer().serialize(key);
        byte[] rawValue = redisTemplate.getStringSerializer().serialize(Cluster.instanceId);
        boolean finish = redisTemplate.execute((RedisCallback<Boolean>) connection -> {
            if (connection.setNX(rawKey, rawValue)) {
                leader.set(FLAG_LEADER);
                connection.expire(rawKey, electionPeriodSec * 2 + 2);
                return true;
            }
            byte[] v = connection.get(rawKey);
            if (v == null) {
                return false;
            }
            if (redisTemplate.getStringSerializer().deserialize(v).equals(Cluster.instanceId)) {
                leader.set(FLAG_LEADER);
                // 默认2个选举周期过期
                connection.expire(rawKey, electionPeriodSec * 2 + 2);
            } else {
                leader.set(FLAG_FOLLOWER);
            }
            return true;
        });
        if (!finish) {
            doElection();
        }
    }

}