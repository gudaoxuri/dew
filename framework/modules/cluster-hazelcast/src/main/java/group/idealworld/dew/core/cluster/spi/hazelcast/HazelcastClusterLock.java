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

package group.idealworld.dew.core.cluster.spi.hazelcast;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.ILock;
import group.idealworld.dew.core.cluster.ClusterLock;
import group.idealworld.dew.core.cluster.VoidProcessFun;

import java.util.concurrent.TimeUnit;

/**
 * 分布式锁服务 Hazelcast 实现.
 *
 * @author gudaoxuri
 */
public class HazelcastClusterLock implements ClusterLock {

    private ILock lock;

    /**
     * Instantiates a new Hazelcast cluster lock.
     *
     * @param key               the key
     * @param hazelcastInstance the hazelcast instance
     */
    public HazelcastClusterLock(String key, HazelcastInstance hazelcastInstance) {
        lock = hazelcastInstance.getLock("dew:cluster:lock:" + key);
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
        return lock.tryLock();
    }

    @Override
    public boolean tryLock(long waitMillSec) throws InterruptedException {
        return tryLock(waitMillSec, 0);
    }

    @Override
    public boolean tryLock(long waitMillSec, long leaseMillSec) throws InterruptedException {
        if (waitMillSec == 0 && leaseMillSec == 0) {
            return lock.tryLock();
        } else if (leaseMillSec == 0) {
            return lock.tryLock(waitMillSec, TimeUnit.MILLISECONDS);
        } else if (waitMillSec == 0) {
            return lock.tryLock(0, TimeUnit.MILLISECONDS, leaseMillSec, TimeUnit.MILLISECONDS);
        } else {
            return lock.tryLock(waitMillSec, TimeUnit.MILLISECONDS, leaseMillSec, TimeUnit.MILLISECONDS);
        }
    }

    @Override
    public boolean unLock() {
        try {
            lock.unlock();
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    @Override
    public void delete() {
        lock.forceUnlock();
    }

    @Override
    public boolean isLocked() {
        return lock.isLocked();
    }
}
