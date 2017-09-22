package com.ecfront.dew.core.controller;


import com.ecfront.dew.common.Page;
import com.ecfront.dew.common.Resp;
import com.ecfront.dew.core.service.CRUService;
import com.ecfront.dew.core.validation.CreateGroup;
import com.ecfront.dew.core.validation.UpdateGroup;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

public interface CRUVOController<T extends CRUService, P, V, E> extends DewVOController<T, P, V, E> {

    @Override
    default boolean convertAble() {
        return true;
    }

    @GetMapping("")
    @ApiOperation(value = "获取记录列表")
    default Resp<List<V>> find() {
        return convertList(getService().find());
    }

    @GetMapping("{pageNumber}/{pageSize}")
    @ApiOperation(value = "获取记录分页列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageNumber", value = "当前页（从0开始）", paramType = "path", dataType = "int", required = true),
            @ApiImplicitParam(name = "pageSize", value = "每页显示记录数", paramType = "path", dataType = "int", required = true),
    })
    default Resp<Page<V>> paging(@PathVariable long pageNumber, @PathVariable int pageSize) {
        return convertPage(getService().paging(pageNumber, pageSize));
    }

    @GetMapping("{id}")
    @ApiOperation(value = "根据ID获取一条记录")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "记录ID", paramType = "path", dataType = "int", required = true),
    })
    default Resp<V> getById(@PathVariable P id) {
        return convertObject(getService().getById(id));
    }

    @GetMapping("code/{code}")
    @ApiOperation(value = "根据Code获取一条记录", notes = "记录实体必须存在@Code注解")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "code", value = "记录Code", paramType = "path", dataType = "string", required = true),
    })
    default Resp<V> getByCode(@PathVariable String code) {
        return convertObject(getService().getByCode(code));
    }

    @PostMapping(value = "")
    @ApiOperation(value = "保存记录")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "vo", value = "记录实例", paramType = "body", dataType = "V", required = true),
    })
    default Resp<V> save(@RequestBody @Validated(CreateGroup.class) V vo) {
        return convertObject(getService().save(voToEntity(vo)));
    }

    @PutMapping(value = "{id}")
    @ApiOperation(value = "根据ID更新记录")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "记录ID", paramType = "path", dataType = "int", required = true),
            @ApiImplicitParam(name = "vo", value = "记录实例", paramType = "body", dataType = "V", required = true),
    })
    default Resp<V> updateById(@PathVariable P id, @RequestBody @Validated(UpdateGroup.class) V vo) {
        return convertObject(getService().updateById(id, voToEntity(vo)));
    }

    @PutMapping(value = "code/{code}")
    @ApiOperation(value = "根据Code更新记录", notes = "记录实体必须存在@Code注解")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "code", value = "记录Code", paramType = "path", dataType = "String", required = true),
            @ApiImplicitParam(name = "vo", value = "记录实例", paramType = "body", dataType = "V", required = true),
    })
    default Resp<V> updateByCode(@PathVariable String code, @RequestBody @Validated(UpdateGroup.class) V vo) {
        return convertObject(getService().updateByCode(code, voToEntity(vo)));
    }

}