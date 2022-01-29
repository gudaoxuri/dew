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

import group.idealworld.dew.core.cluster.Cluster;
import group.idealworld.dew.core.cluster.ClusterLock;
import group.idealworld.dew.core.cluster.VoidProcessFun;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.concurrent.TimeUnit;

/**
 * 分布式锁服务 Redis 实现.
 * <p>
 * 存在一定几率的错误，见各方法说明
 *
 * @author gudaoxuri
 */
public class RedisClusterLock implements ClusterLock {

    private String key;
    private RedisTemplate<String, String> redisTemplate;

    /**
     * Instantiates a new Redis cluster lock.
     *
     * @param key           the key
     * @param redisTemplate the redis template
     */
    RedisClusterLock(String key, RedisTemplate<String, String> redisTemplate) {
        this.key = "dew:cluster:lock:" + key;
        this.redisTemplate = redisTemplate;
    }

    @Override
    public void tryLockWithFun(VoidProcessFun fun) throws Exception {
        tryLockWithFun(0, fun);
    }

    @Override
    public void tryLockWithFun(long waitMillSec, VoidProcessFun fun) throws Exception {
        if (tryLock(waitMillSec)) {
            try {
                fun.exec();
            } finally {
                unLock();
            }
        }
    }

    @Override
    public void tryLockWithFun(long waitMillSec, long leaseMillSec, VoidProcessFun fun) throws Exception {
        if (tryLock(waitMillSec, leaseMillSec)) {
            try {
                fun.exec();
            } finally {
                unLock();
            }
        }
    }

    @Override
    public boolean tryLock() {
        return redisTemplate.opsForValue().setIfAbsent(key, getCurrThreadId());
    }

    @Override
    public boolean tryLock(long waitMillSec) throws InterruptedException {
        long now = System.currentTimeMillis();
        while (System.currentTimeMillis() - now < waitMillSec) {
            if (isLocked()) {
                Thread.sleep(100);
            } else if (tryLock()) {
                return true;
            }
        }
        return tryLock();
    }

    @Override
    public boolean tryLock(long waitMillSec, long leaseMillSec) throws InterruptedException {
        if (waitMillSec == 0 && leaseMillSec == 0) {
            return tryLock();
        } else if (leaseMillSec == 0) {
            return tryLock(waitMillSec);
        } else if (waitMillSec == 0) {
            return putLockKey(leaseMillSec);
        } else {
            long now = System.currentTimeMillis();
            while (System.currentTimeMillis() - now < waitMillSec) {
                if (isLocked()) {
                    Thread.sleep(100);
                } else if (putLockKey(leaseMillSec)) {
                    return true;
                }
            }
            return putLockKey(leaseMillSec);
        }
    }

    /**
     * 解锁.
     * <p>
     * 存在非原子操作，有误解锁可能!
     */
    @Override
    public boolean unLock() {
        if (getCurrThreadId().equals(redisTemplate.opsForValue().get(key))) {
            redisTemplate.delete(key);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean isLocked() {
        return redisTemplate.hasKey(key);
    }

    @Override
    public void delete() {
        redisTemplate.delete(key);
    }

    private boolean putLockKey(long leaseMillSec) {
        return redisTemplate.opsForValue()
                .setIfAbsent(key, getCurrThreadId(), leaseMillSec, TimeUnit.MILLISECONDS);
    }

    private String getCurrThreadId() {
        return Cluster.instanceId + "-" + Thread.currentThread().getId();
    }
}
