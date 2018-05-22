package com.tairanchina.csp.dew.core.controller;

import com.ecfront.dew.common.Resp;
import com.tairanchina.csp.dew.core.service.CRUDService;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;

public interface CRUDVOController<T extends CRUDService, P, V, E> extends CRUVOController<T, P, V, E> {

    @Override
    default boolean convertAble() {
        return true;
    }

    @DeleteMapping(value = "{id}")
    @ApiOperation(value = "根据ID删除记录")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "记录ID", paramType = "path", dataType = "int", required = true),
    })
    default Resp<Void> deleteById(@PathVariable P id) {
        return getService().deleteById(id);
    }

    @DeleteMapping(value = "code/{code}")
    @ApiOperation(value = "根据Code删除记录", notes = "记录实体必须存在@Code注解")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "code", value = "记录Code", paramType = "path", dataType = "string", required = true),
    })
    default Resp<Void> deleteByCode(@PathVariable String code) {
        return getService().deleteByCode(code);
    }

}

