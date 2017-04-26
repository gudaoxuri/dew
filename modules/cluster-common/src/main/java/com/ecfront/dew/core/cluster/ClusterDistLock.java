package com.ecfront.dew.core.cluster;

public interface ClusterDistLock {

    void lockWithFun(VoidProcessFun fun) throws Exception;

    void tryLockWithFun(VoidProcessFun fun) throws Exception;

    void tryLockWithFun(int waitSec, VoidProcessFun fun) throws Exception;

    void lock();

    boolean tryLock();

    boolean tryLock(int waitSec) throws InterruptedException;

    boolean unLock();

    void delete();

}
