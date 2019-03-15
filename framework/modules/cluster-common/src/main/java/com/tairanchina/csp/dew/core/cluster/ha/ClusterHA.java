package com.tairanchina.csp.dew.core.cluster.ha;

import com.tairanchina.csp.dew.core.cluster.ha.dto.HAConfig;
import com.tairanchina.csp.dew.core.cluster.ha.entity.PrepareCommitMsg;

import java.sql.SQLException;
import java.util.List;

public interface ClusterHA {

    void init(HAConfig haConfig) throws SQLException;

    String mq_afterPollMsg(String addr, String msg);

    void mq_afterMsgAcked(String id);

    List<PrepareCommitMsg> mq_findAllUnCommittedMsg(String addr);

}
