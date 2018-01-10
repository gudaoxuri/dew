package com.ecfront.dew.core.jdbc;


import com.ecfront.dew.Dew;
import com.ecfront.dew.Dew;

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
