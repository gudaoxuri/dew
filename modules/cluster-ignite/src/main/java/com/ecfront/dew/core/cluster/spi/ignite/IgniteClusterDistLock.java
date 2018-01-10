package com.ecfront.dew.core.cluster.spi.ignite;

import com.ecfront.dew.core.cluster.ClusterDistLock;
import com.ecfront.dew.core.cluster.VoidProcessFun;
import com.ecfront.dew.core.cluster.ClusterDistLock;
import com.ecfront.dew.core.cluster.VoidProcessFun;
import org.apache.ignite.Ignite;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;

public class IgniteClusterDistLock implements ClusterDistLock {

    private Logger logger = LoggerFactory.getLogger(IgniteClusterDistLock.class);

    private long lockedThread;

    private Lock lock;

    private Ignite ignite;

    private String key;

    IgniteClusterDistLock(String key, Ignite ignite) {
        this.ignite = ignite;
        this.key = "dew:dist:lock:" + key;
        lock = ignite.getOrCreateCache("dew:dist:lock:" + key).lock(key);
    }

    public long getLockedThread() {
        return lockedThread;
    }

    public void setLockedThread(long lockedThread) {
        this.lockedThread = lockedThread;
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
    public void lock() {
        lock.lock();
        setLockedThread(getCurrentId());
    }

    @Override
    public boolean tryLock() {
        if (lock.tryLock()) {
            setLockedThread(getCurrentId());
            return true;
        }
        return false;
    }

    @Override
    public boolean tryLock(long waitMillSec) throws InterruptedException {
        if (waitMillSec == 0) {
            return tryLock();
        } else {
            if (lock.tryLock(waitMillSec, TimeUnit.MILLISECONDS)) {
                setLockedThread(getCurrentId());
                return true;
            }
            return false;
        }
    }

    @Override
    public boolean tryLock(long waitMillSec, long leaseMillSec) throws InterruptedException {
        if (waitMillSec == 0 && leaseMillSec == 0) {
            return tryLock();
        } else if (waitMillSec == 0) {
            if (tryLock()) {
                new LeaseTask(getCurrentId(), leaseMillSec).start();
                return true;
            }
        } else if (leaseMillSec == 0) {
            return tryLock(waitMillSec);
        } else {
            if (tryLock(waitMillSec)) {
                new LeaseTask(getCurrentId(), leaseMillSec).start();
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean unLock() {
        try {
            if (ignite.getOrCreateCache("dew:dist:lock:" + key).isLocalLocked(key, true)) {
                lock.unlock();
            } else {
                return false;
            }
            return true;
        } catch (IllegalStateException e) {
            logger.error("Ignite Unlock error.", e);
            return false;
        }
    }

    @Override
    public void delete() {
        ignite.destroyCache(key);
    }

    @Override
    public boolean isLocked() {
        return false;
    }

    private long getCurrentId() {
        return Thread.currentThread().getId();
    }

    private class LeaseTask extends Thread {

        private long lockedThreadId;

        private long leaseMillSec;

        public LeaseTask(long lockedThreadId, long leaseMillSec) {
            this.lockedThreadId = lockedThreadId;
            this.leaseMillSec = leaseMillSec;
        }

        @Override
        public void run() {
            try {
                //判断锁住的线程id
                Thread.sleep(leaseMillSec - 1);
                if (getLockedThread() == getLockedThreadId()) {
                    delete();
                }
            } catch (InterruptedException e) {
                logger.error(e.getMessage());
                Thread.currentThread().interrupt();
            }
        }

        public long getLockedThreadId() {
            return lockedThreadId;
        }

        public void setLockedThreadId(long lockedThreadId) {
            this.lockedThreadId = lockedThreadId;
        }

        public long getLeaseMillSec() {
            return leaseMillSec;
        }

        public void setLeaseMillSec(long leaseMillSec) {
            this.leaseMillSec = leaseMillSec;
        }
    }
}
