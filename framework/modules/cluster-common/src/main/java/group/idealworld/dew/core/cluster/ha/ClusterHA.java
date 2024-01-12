package group.idealworld.dew.core.cluster.ha;

import group.idealworld.dew.core.cluster.dto.MessageWrap;
import group.idealworld.dew.core.cluster.ha.dto.HAConfig;
import group.idealworld.dew.core.cluster.ha.entity.PrepareCommitMsg;

import java.sql.SQLException;
import java.util.List;

/**
 * 集群HA处理接口.
 *
 * @author gudaoxuri
 */
public interface ClusterHA {

    /**
     * 初始化.
     *
     * @param haConfig HA配置
     * @throws SQLException the sql exception
     */
    void init(HAConfig haConfig) throws SQLException;

    /**
     * 获取到消息后的处理方法.
     * <p>
     * 多为暂存消息以做灾备
     *
     * @param addr the addr
     * @param msg  the msg
     * @return the id
     */
    String mqAfterPollMsg(String addr, MessageWrap msg);

    /**
     * 消息被确认后的处理方法.
     * <p>
     * 多为删除暂存消息
     *
     * @param id the id
     */
    void mqAfterMsgAcked(String id);

    /**
     * 获取所有已接收但未确认的消息.
     *
     * @param addr the addr
     * @return the list
     */
    List<PrepareCommitMsg> mqFindAllUnCommittedMsg(String addr);

}
