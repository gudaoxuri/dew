package com.ecfront.dew.example.auth.dto;

import com.ecfront.dew.core.dto.BasicOptInfo;
import com.ecfront.dew.core.dto.OptInfo;

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
