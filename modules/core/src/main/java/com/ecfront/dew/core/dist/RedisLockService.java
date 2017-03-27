package com.ecfront.dew.core.dist;

import com.ecfront.dew.core.Dew;
import com.ecfront.dew.core.fun.VoidExecutor;

import java.util.Date;
import java.util.concurrent.TimeUnit;

public class RedisLockService implements LockService {

    private String key;

    private String currThreadId;

    public RedisLockService(String key) {
        this.key = "dew:dist:lock:" + key;
        currThreadId = Dew.Info.name + "-" + Dew.Info.instance + "-" + Thread.currentThread().getId();
    }

    @Override
    public void lockWithFun(VoidExecutor fun) throws Exception {
        lockWithFun(-1, fun);
    }

    @Override
    public void lockWithFun(long leaseMilliSec, VoidExecutor fun) throws Exception {
        try {
            lock(leaseMilliSec);
            fun.exec();
        } catch (Exception e) {
            throw e;
        } finally {
            unLock();
        }
    }

    @Override
    public void tryLockWithFun(VoidExecutor fun) throws Exception {
        tryLockWithFun(-1, -1, fun);
    }

    @Override
    public void tryLockWithFun(long waitMilliSec, VoidExecutor fun) throws Exception {
        tryLockWithFun(waitMilliSec, -1, fun);
    }

    @Override
    public void tryLockWithFun(long waitMilliSec, long leaseMilliSec, VoidExecutor fun) throws Exception {
        if (tryLock(waitMilliSec, leaseMilliSec)) {
            try {
                fun.exec();
            } catch (Exception e) {
                throw e;
            } finally {
                unLock();
            }
        }
    }

    @Override
    public void lock() throws Exception {
        lock(-1);
    }

    @Override
    public void lock(long leaseMilliSec) throws Exception {
        if (isLock()) {
            throw new Exception("[Dist] Lock key: " + key + " exist!");
        }
        Dew.redis.opsForValue().set(key, currThreadId);
        if (leaseMilliSec != -1) {
            Dew.redis.expire(key, leaseMilliSec, TimeUnit.MILLISECONDS);
        }
    }

    @Override
    public boolean tryLock() throws Exception {
        return tryLock(-1, -1);
    }

    @Override
    public boolean tryLock(long waitMilliSec) throws Exception {
        return tryLock(waitMilliSec, -1);
    }

    @Override
    public boolean tryLock(long waitMilliSec, long leaseMilliSec) throws Exception {
        synchronized (this) {
            if (waitMilliSec == -1) {
                if (!isLock()) {
                    lock(leaseMilliSec);
                    return true;
                } else {
                    return false;
                }
            } else {
                long now = new Date().getTime();
                while (isLock() && new Date().getTime() - now < waitMilliSec) {
                    Thread.sleep(500);
                }
                if (!isLock()) {
                    lock(leaseMilliSec);
                    return true;
                } else {
                    return false;
                }
            }
        }
    }

    @Override
    public boolean unLock() {
        if (currThreadId.equals(Dew.redis.opsForValue().get(key))) {
            Dew.redis.delete(key);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean isLock() {
        return Dew.redis.hasKey(key);
    }
}
