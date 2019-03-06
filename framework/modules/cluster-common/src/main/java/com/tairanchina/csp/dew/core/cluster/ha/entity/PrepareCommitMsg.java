package com.tairanchina.csp.dew.core.cluster.ha.entity;

import com.ecfront.dew.common.$;

import java.util.Date;

public class PrepareCommitMsg {
    private String addr;
    private String msgId;
    private String msg;
    private Date createdTime;

    public String getAddr() {
        return addr;
    }

    public void setAddr(String addr) {
        this.addr = addr;
    }

    public String getMsgId() {
        return msgId;
    }

    public void setMsgId(String msgId) {
        this.msgId = msgId;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Date getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(Date createdTime) {
        this.createdTime = createdTime;
    }

    @Override
    public String toString() {
        return $.json.toJsonString(this);
    }
}
