package com.tairanchina.csp.dew.example.auth.dto;

import com.tairanchina.csp.dew.core.dto.BasicOptInfo;
import com.tairanchina.csp.dew.core.dto.OptInfo;

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
