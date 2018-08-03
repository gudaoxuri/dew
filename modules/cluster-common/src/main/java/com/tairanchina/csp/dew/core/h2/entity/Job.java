package com.tairanchina.csp.dew.core.h2.entity;

import com.ecfront.dew.common.$;

import java.util.Date;

/**
 * Created by hzlizx on 2018/7/31 0031
 */
public class Job {
    private String address;
    private String jobId;
    private String status;
    private String msg;
    private Date createdTime;

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getJobId() {
        return jobId;
    }

    public void setJobId(String jobId) {
        this.jobId = jobId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
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
