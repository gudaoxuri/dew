package com.trc.dew.crud.convert;


import com.trc.dew.crud.entity.TestSelectEntity;

public class VOConvert implements VOAssembler<TestSelectEntity,TestSelectEntity> {
    @Override
    public boolean convertAble() {
        return true;
    }
}
