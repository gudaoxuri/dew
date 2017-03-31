package com.ecfront.dew.core.controller;


import com.ecfront.dew.common.Resp;
import com.ecfront.dew.core.entity.IdEntity;
import com.ecfront.dew.core.service.CRUDService;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;

public interface CRUDVOController<T extends CRUDService, V extends Object, E extends IdEntity> extends CRUVOController<T, V, E> {

    @DeleteMapping(value = "{id}")
    default Resp<Void> deleteById(@PathVariable long id) {
        return getDewService().deleteById(id);
    }

    @DeleteMapping(value = "code/{code}")
    default Resp<Void> deleteById(@PathVariable String code) {
        return getDewService().deleteByCode(code);
    }

}

