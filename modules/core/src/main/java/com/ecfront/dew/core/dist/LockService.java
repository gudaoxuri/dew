package com.ecfront.dew.core.dist;


import com.ecfront.dew.core.fun.VoidExecutor;

public interface LockService {

    void lockWithFun(VoidExecutor fun) throws Exception;

    void lockWithFun(long leaseMilliSec, VoidExecutor fun) throws Exception;

    void tryLockWithFun(VoidExecutor fun) throws Exception;

    void tryLockWithFun(long waitMilliSec, VoidExecutor fun) throws Exception;

    void tryLockWithFun(long waitMilliSec, long leaseMilliSec, VoidExecutor fun) throws Exception;

    void lock() throws Exception;

    void lock(long leaseMilliSec) throws Exception;

    boolean tryLock() throws Exception;

    boolean tryLock(long waitMilliSec) throws Exception;

    boolean tryLock(long waitMilliSec, long leaseMilliSec) throws Exception;

    boolean unLock();

    boolean isLock();

}
