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

import group.idealworld.dew.core.cluster.ClusterElection;
import group.idealworld.dew.core.cluster.ClusterElectionWrap;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.concurrent.ConcurrentHashMap;

/**
 * 领导者选举服务多实例封装 Redis 实现.
 *
 * @author gudaoxuri
 */
public class RedisClusterElectionWrap implements ClusterElectionWrap {

    private static final ConcurrentHashMap<String, ClusterElection> ELECTION_CONTAINER = new ConcurrentHashMap<>();

    private RedisTemplate<String, String> redisTemplate;
    private int electionPeriodSec;

    /**
     * Instantiates a new Redis cluster election wrap.
     *
     * @param redisTemplate     the redis template
     * @param electionPeriodSec the election period sec
     */
    public RedisClusterElectionWrap(RedisTemplate<String, String> redisTemplate, int electionPeriodSec) {
        this.redisTemplate = redisTemplate;
        this.electionPeriodSec = electionPeriodSec;
    }

    @Override
    public ClusterElection instance() {
        ELECTION_CONTAINER.putIfAbsent("", new RedisClusterElection(electionPeriodSec, redisTemplate));
        return ELECTION_CONTAINER.get("");
    }

    @Override
    public ClusterElection instance(String key) {
        ELECTION_CONTAINER.putIfAbsent(key, new RedisClusterElection(key, electionPeriodSec, redisTemplate));
        return ELECTION_CONTAINER.get(key);
    }

}
