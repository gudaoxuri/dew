package com.trc.test.auth;

import group.idealworld.dew.core.auth.dto.BasicOptInfo;

/**
 * Opt info ext.
 *
 * @author gudaoxuri
 */
public class OptInfoExt extends BasicOptInfo {

    private String idCard;

    /**
     * Gets id card.
     *
     * @return the id card
     */
    public String getIdCard() {
        return idCard;
    }

    /**
     * Sets id card.
     *
     * @param idCard the id card
     * @return the id card
     */
    public OptInfoExt setIdCard(String idCard) {
        this.idCard = idCard;
        return this;
    }
}
