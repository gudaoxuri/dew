package com.tairanchina.csp.dew.core.jdbc;


import com.tairanchina.csp.dew.Dew;

public interface DSManager {

    static DS select(String dsName) {
        if (dsName == null) {
            dsName = "";
        }
        if (dsName.isEmpty()) {
            return (DS) Dew.applicationContext.getBean("ds");
        } else {
            return (DS) Dew.applicationContext.getBean(dsName + "DS");
        }
    }

}
