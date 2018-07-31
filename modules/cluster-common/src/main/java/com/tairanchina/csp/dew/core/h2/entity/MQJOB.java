package com.tairanchina.csp.dew.core.h2.entity;

import com.ecfront.dew.common.$;

import java.util.Date;

/**
 * Created by hzlizx on 2018/7/31 0031
 */
public class MQJOB {
    private String JOB_ID;

    private String STATUS;
    private String MSG;

    private Date CREATED_TIME;

    public String getJOB_ID() {
        return JOB_ID;
    }

    public void setJOB_ID(String JOB_ID) {
        this.JOB_ID = JOB_ID;
    }

    public String getSTATUS() {
        return STATUS;
    }

    public void setSTATUS(String STATUS) {
        this.STATUS = STATUS;
    }


    public Date getCREATED_TIME() {
        return CREATED_TIME;
    }

    public void setCREATED_TIME(Date CREATED_TIME) {
        this.CREATED_TIME = CREATED_TIME;
    }

    public String getMSG() {
        return MSG;
    }

    public void setMSG(String MSG) {
        this.MSG = MSG;
    }

    @Override
    public String toString() {
        return $.json.toJsonString(this);
    }
}
