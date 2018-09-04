package com.tairanchina.csp.dew.core.cluster.spi.hazelcast;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.ILock;
import com.tairanchina.csp.dew.core.cluster.ClusterLock;
import com.tairanchina.csp.dew.core.cluster.VoidProcessFun;

import java.util.concurrent.TimeUnit;

public class HazelcastClusterLock implements ClusterLock {

    private ILock lock;

    public HazelcastClusterLock(String key, HazelcastInstance hazelcastInstance) {
        lock = hazelcastInstance.getLock("dew:dist:lock:" + key);
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
