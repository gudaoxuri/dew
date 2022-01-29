/*
 * Copyright 2022. the original author or authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package group.idealworld.dew.core.cluster.ha;

import group.idealworld.dew.core.cluster.dto.MessageWrap;
import group.idealworld.dew.core.cluster.ha.entity.PrepareCommitMsg;
import group.idealworld.dew.core.cluster.ha.dto.HAConfig;

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
    String mq_afterPollMsg(String addr, MessageWrap msg);

    /**
     * 消息被确认后的处理方法.
     * <p>
     * 多为删除暂存消息
     *
     * @param id the id
     */
    void mq_afterMsgAcked(String id);

    /**
     * 获取所有已接收但未确认的消息.
     *
     * @param addr the addr
     * @return the list
     */
    List<PrepareCommitMsg> mq_findAllUnCommittedMsg(String addr);

}
