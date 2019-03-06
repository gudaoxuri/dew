package com.tairanchina.csp.dew.core.cluster;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.concurrent.atomic.AtomicLong;

public abstract class AbsClusterElection implements ClusterElection {

    protected static final Logger logger = LoggerFactory.getLogger(AbsClusterElection.class);

    protected static final long FLAG_UNINITIALIZED = 0;
    protected static final long FLAG_LEADER = 1;
    protected static final long FLAG_FOLLOWER = -1;

    // 0 未初始化， 1 是领导者 -1 不是领导者
    protected AtomicLong leader = new AtomicLong(FLAG_UNINITIALIZED);

    /**
     * 执行（重新）选举
     * <p>
     * 需调用方定时调用此接口
     *
     * @throws Exception
     */
    protected abstract void election();

    /**
     * 退出选举，暂未实现
     *
     * @throws Exception
     */
    protected void quit() {
        throw new NotImplementedException();
    }

    /**
     * 当前工程是否是领导者
     *
     * @return 是否是领导者
     */
    @Override
    public boolean isLeader() {
        while (leader.get() == FLAG_UNINITIALIZED) {
            try {
                Thread.sleep(100);
                logger.trace("Waiting leader election...");
            } catch (InterruptedException e) {
                logger.error("Leader election error", e);
            }
        }
        return leader.get() == FLAG_LEADER;
    }

}
