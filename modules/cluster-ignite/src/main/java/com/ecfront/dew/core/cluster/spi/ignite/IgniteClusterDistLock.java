package com.ecfront.dew.core.cluster.spi.ignite;

import com.ecfront.dew.core.cluster.ClusterDistLock;
import com.ecfront.dew.core.cluster.VoidProcessFun;
import org.apache.ignite.Ignite;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;

public class IgniteClusterDistLock implements ClusterDistLock {

    private Lock lock;
    private Ignite ignite;
    private String key;

    IgniteClusterDistLock(String key, Ignite ignite) {
        this.ignite = ignite;
        this.key = "dew:dist:lock:" + key;
        lock = ignite.getOrCreateCache("dew:dist:lock:" + key).lock(key);
    }

    @Override
    public void lockWithFun(VoidProcessFun fun) throws Exception {
        try {
            lock();
            fun.exec();
        } finally {
            unLock();
        }
    }

    @Override
    public void tryLockWithFun(VoidProcessFun fun) throws Exception {
        tryLockWithFun(0, fun);
    }


    @Override
    public void tryLockWithFun(int waitSec, VoidProcessFun fun) throws Exception {
        if (tryLock(waitSec)) {
            try {
                fun.exec();
            } finally {
                unLock();
            }
        }
    }

    @Override
    public void lock() {
        lock.lock();
    }

    @Override
    public boolean tryLock() {
        return lock.tryLock();
    }

    @Override
    public boolean tryLock(int waitSec) throws InterruptedException {
        if (waitSec == 0) {
            return lock.tryLock();
        } else {
            return lock.tryLock(waitSec, TimeUnit.SECONDS);
        }
    }

    @Override
    public boolean unLock() {
        try{
            lock.unlock();
            return true;
        }catch (IllegalStateException e){
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public void delete() {
        ignite.destroyCache(key);
    }
}
