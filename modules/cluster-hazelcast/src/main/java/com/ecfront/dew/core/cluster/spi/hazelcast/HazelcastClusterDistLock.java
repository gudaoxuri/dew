package com.ecfront.dew.core.cluster.spi.hazelcast;

import com.ecfront.dew.core.cluster.ClusterDistLock;
import com.ecfront.dew.core.cluster.VoidProcessFun;
import com.hazelcast.core.ILock;

import java.util.concurrent.TimeUnit;

public class HazelcastClusterDistLock implements ClusterDistLock {

    private ILock lock;

    HazelcastClusterDistLock(String key, HazelcastAdapter hazelcastAdapter) {
        lock = hazelcastAdapter.getHazelcastInstance().getLock("dew:dist:lock:" + key);
    }

    @Override
    public void lockWithFun(VoidProcessFun fun) throws Exception {
        lockWithFun(0, fun);
    }

    @Override
    public void lockWithFun(int leaseSec, VoidProcessFun fun) throws Exception {
        try {
            lock(leaseSec);
            fun.exec();
        } finally {
            unLock();
        }
    }

    @Override
    public void tryLockWithFun(VoidProcessFun fun) throws Exception {
        tryLockWithFun(0, 0, fun);
    }

    @Override
    public void tryLockWithFun(int waitSec, VoidProcessFun fun) throws Exception {
        tryLockWithFun(waitSec, 0, fun);
    }

    @Override
    public void tryLockWithFun(int waitSec, int leaseSec, VoidProcessFun fun) throws Exception {
        if (tryLock(waitSec, leaseSec)) {
            try {
                fun.exec();
            } finally {
                unLock();
            }
        }
    }

    @Override
    public void lock() {
        lock(0);
    }

    @Override
    public void lock(int leaseSec) {
        if (leaseSec == 0) {
            lock.lock();
        } else {
            lock.lock(leaseSec, TimeUnit.SECONDS);
        }
    }

    @Override
    public boolean tryLock() {
        return lock.tryLock();
    }

    @Override
    public boolean tryLock(int waitSec) throws InterruptedException {
        return lock.tryLock(waitSec, TimeUnit.SECONDS);
    }

    @Override
    public boolean tryLock(int waitSec, int leaseSec) throws InterruptedException {
        if (waitSec == 0 && leaseSec == 0) {
            return lock.tryLock();
        } else if (leaseSec == 0) {
            return lock.tryLock(waitSec, TimeUnit.SECONDS);
        } else {
            return lock.tryLock(waitSec, TimeUnit.SECONDS, leaseSec, TimeUnit.SECONDS);
        }
    }

    @Override
    public boolean unLock() {
        lock.unlock();
        return true;
    }

    @Override
    public boolean isLock() {
        return lock.isLocked();
    }

    @Override
    public void delete() {
        lock.forceUnlock();
    }
}
