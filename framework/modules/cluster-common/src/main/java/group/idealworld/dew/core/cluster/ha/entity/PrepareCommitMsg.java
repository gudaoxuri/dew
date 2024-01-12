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
