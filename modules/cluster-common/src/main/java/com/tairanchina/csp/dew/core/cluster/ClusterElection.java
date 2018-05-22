package com.tairanchina.csp.dew.core.cluster;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 领导者选举
 */
public interface ClusterElection {

    Logger logger = LoggerFactory.getLogger(ClusterElection.class);

    /**
     * 执行（重新）选举
     *
     * 需调用方定时调用此接口
     *
     * @throws Exception
     */
    void election() throws Exception;

    /**
     * 退出选举，暂未实现
     * @throws Exception
     */
    void quit() throws Exception;

    /**
     * 当前工程是否是领导者
     * @return 是否是领导者
     */
    boolean isLeader();

}
