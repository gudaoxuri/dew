package com.ecfront.dew.core.cluster.spi.redis;

import com.ecfront.dew.core.cluster.ClusterDistLock;
import com.ecfront.dew.core.cluster.VoidProcessFun;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.Date;
import java.util.UUID;

public class RedisClusterDistLock implements ClusterDistLock {

    private static String instance = UUID.randomUUID().toString();
    private String key;
    private String currThreadId;
    private RedisTemplate<String, String> redisTemplate;

    public RedisClusterDistLock(String key, RedisTemplate<String, String> redisTemplate) {
        this.key = "dew:dist:lock:" + key;
        currThreadId = instance + "-" + Thread.currentThread().getId();
        this.redisTemplate = redisTemplate;
    }

    @Override
    public void lockWithFun(VoidProcessFun fun) throws Exception {
        try {
            lock();
            fun.exec();
        } catch (Exception e) {
            throw e;
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
            } catch (Exception e) {
                throw e;
            } finally {
                unLock();
            }
        }
    }

    @Override
    public void lock() {
        if (!isLock()) {
            redisTemplate.opsForValue().set(key, currThreadId);
        }
    }

    @Override
    public boolean tryLock() {
        if (!isLock()) {
            lock();
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean tryLock(int waitSec) throws InterruptedException {
        synchronized (this) {
            if (waitSec == 0) {
                if (!isLock()) {
                    lock();
                    return true;
                } else {
                    return false;
                }
            } else {
                long now = new Date().getTime();
                while (isLock() && new Date().getTime() - now < waitSec) {
                    Thread.sleep(500);
                }
                if (!isLock()) {
                    lock();
                    return true;
                } else {
                    return false;
                }
            }
        }
    }

    @Override
    public boolean unLock() {
        if (currThreadId.equals(redisTemplate.opsForValue().get(key))) {
            redisTemplate.delete(key);
            return true;
        } else {
            return false;
        }
    }

    private boolean isLock() {
        return redisTemplate.hasKey(key);
    }

    @Override
    public void delete() {
        redisTemplate.delete(key);
    }
}
