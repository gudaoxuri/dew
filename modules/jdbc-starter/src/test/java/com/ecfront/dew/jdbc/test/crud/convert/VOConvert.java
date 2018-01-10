package com.ecfront.dew.jdbc.test.crud.convert;


import com.ecfront.dew.core.controller.VOAssembler;
import com.ecfront.dew.jdbc.test.crud.entity.TestSelectEntity;

public class VOConvert implements VOAssembler<TestSelectEntity,TestSelectEntity> {
    @Override
    public boolean convertAble() {
        return true;
    }
}
