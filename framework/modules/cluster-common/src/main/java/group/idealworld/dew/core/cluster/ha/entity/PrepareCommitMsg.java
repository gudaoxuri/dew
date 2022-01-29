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

package group.idealworld.dew.core.cluster.ha.entity;

import com.ecfront.dew.common.$;
import group.idealworld.dew.core.cluster.dto.MessageWrap;

import java.util.Date;

/**
 * 待提交消息实体.
 *
 * @author gudaoxuri
 */
public class PrepareCommitMsg {
    private String addr;
    private String msgId;
    private MessageWrap msg;
    private Date createdTime;

    /**
     * Gets addr.
     *
     * @return the addr
     */
    public String getAddr() {
        return addr;
    }

    /**
     * Sets addr.
     *
     * @param addr the addr
     */
    public void setAddr(String addr) {
        this.addr = addr;
    }

    /**
     * Gets msg id.
     *
     * @return the msg id
     */
    public String getMsgId() {
        return msgId;
    }

    /**
     * Sets msg id.
     *
     * @param msgId the msg id
     */
    public void setMsgId(String msgId) {
        this.msgId = msgId;
    }

    /**
     * Gets msg.
     *
     * @return the msg
     */
    public MessageWrap getMsg() {
        return msg;
    }

    /**
     * Sets msg.
     *
     * @param msg the msg
     */
    public void setMsg(MessageWrap msg) {
        this.msg = msg;
    }

    /**
     * Gets created time.
     *
     * @return the created time
     */
    public Date getCreatedTime() {
        return createdTime;
    }

    /**
     * Sets created time.
     *
     * @param createdTime the created time
     */
    public void setCreatedTime(Date createdTime) {
        this.createdTime = createdTime;
    }

    @Override
    public String toString() {
        return $.json.toJsonString(this);
    }
}
