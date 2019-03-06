package com.tairanchina.csp.dew.core.cluster.ha;

import com.tairanchina.csp.dew.core.cluster.ha.entity.PrepareCommitMsg;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public interface ClusterHA {

    void init(Map<String, String> args) throws SQLException;

    String mq_afterPollMsg(String addr, String msg);

    void mq_afterMsgAcked(String id);

    List<PrepareCommitMsg> mq_findAllUnCommittedMsg(String addr);

}
