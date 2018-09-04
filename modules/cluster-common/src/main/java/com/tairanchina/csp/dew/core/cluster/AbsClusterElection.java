package com.tairanchina.csp.dew.core.cluster;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.concurrent.atomic.AtomicBoolean;

public abstract class AbsClusterElection implements ClusterElection {

    protected static final Logger logger = LoggerFactory.getLogger(AbsClusterElection.class);

    protected AtomicBoolean leader = new AtomicBoolean(false);

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
        return leader.get();
    }

}
