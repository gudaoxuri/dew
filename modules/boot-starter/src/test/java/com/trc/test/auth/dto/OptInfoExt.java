package com.trc.test.auth.dto;

import com.tairanchina.csp.dew.core.dto.BasicOptInfo;

public class OptInfoExt extends BasicOptInfo<OptInfoExt> {

    private String idCard;

    public String getIdCard() {
        return idCard;
    }

    public OptInfoExt setIdCard(String idCard) {
        this.idCard = idCard;
        return this;
    }
}
