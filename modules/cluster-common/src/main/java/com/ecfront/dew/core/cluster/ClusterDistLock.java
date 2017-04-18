package com.ecfront.dew.core.cluster;

public interface ClusterDistLock {

    void lockWithFun(VoidProcessFun fun) throws Exception;

    void lockWithFun(int leaseSec, VoidProcessFun fun) throws Exception;

    void tryLockWithFun(VoidProcessFun fun) throws Exception;

    void tryLockWithFun(int waitSec, VoidProcessFun fun) throws Exception;

    void tryLockWithFun(int waitSec, int leaseSec, VoidProcessFun fun) throws Exception;

    void lock();

    void lock(int leaseSec);

    boolean tryLock();

    boolean tryLock(int waitSec) throws InterruptedException;

    boolean tryLock(int waitSec, int leaseSec) throws InterruptedException;

    boolean unLock();

    boolean isLock();

    void delete();

}
