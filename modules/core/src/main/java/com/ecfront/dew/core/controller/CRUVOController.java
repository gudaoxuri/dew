package com.ecfront.dew.core.controller;


import com.ecfront.dew.common.Resp;
import com.ecfront.dew.core.dto.PageDTO;
import com.ecfront.dew.core.entity.IdEntity;
import com.ecfront.dew.core.service.CRUService;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

public interface CRUVOController<T extends CRUService, V extends Object, E extends IdEntity> extends DewVOController<T, V, E> {

    @GetMapping("")
    @ApiOperation(value = "获取记录列表")
    default Resp<List<V>> find() {
        Resp<List<E>> result = getDewService().find();
        if (result.ok()) {
            List<V> body = result.getBody().stream().map(i -> entityToVO(i)).collect(Collectors.toList());
            return Resp.success(body);
        } else {
            return Resp.customFail(result.getCode(), result.getMessage());
        }
    }

    @GetMapping("{pageNumber}/{pageSize}")
    @ApiOperation(value = "获取记录分页列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageNumber", value = "当前页（从0开始）", paramType = "path", dataType = "int", required = true),
            @ApiImplicitParam(name = "pageSize", value = "每页显示记录数", paramType = "path", dataType = "int", required = true),
    })
    default Resp<PageDTO<V>> paging(@PathVariable int pageNumber, @PathVariable int pageSize) {
        Resp<PageDTO<E>> result = getDewService().paging(pageNumber, pageSize);
        if (result.ok()) {
            List<V> body = result.getBody().getObjects().stream().map(i -> entityToVO(i)).collect(Collectors.toList());
            PageDTO<V> page = PageDTO.build(pageNumber, pageSize, result.getBody().getRecordTotal(), body);
            return Resp.success(page);
        } else {
            return Resp.customFail(result.getCode(), result.getMessage());
        }
    }

    @GetMapping("{id}")
    @ApiOperation(value = "根据ID获取一条记录")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "记录ID", paramType = "path", dataType = "int", required = true),
    })
    default Resp<V> getById(@PathVariable long id) {
        Resp<E> result = getDewService().getById(id);
        if (result.ok()) {
            V body = entityToVO(result.getBody());
            return Resp.success(body);
        } else {
            return Resp.customFail(result.getCode(), result.getMessage());
        }
    }

    @GetMapping("code/{code}")
    @ApiOperation(value = "根据Code获取一条记录", notes = "记录实体必须存在@Code注解")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "code", value = "记录Code", paramType = "path", dataType = "string", required = true),
    })
    default Resp<V> getByCode(@PathVariable String code) {
        Resp<E> result = getDewService().getByCode(code);
        if (result.ok()) {
            V body = entityToVO(result.getBody());
            return Resp.success(body);
        } else {
            return Resp.customFail(result.getCode(), result.getMessage());
        }
    }

    @PostMapping(value = "")
    @ApiOperation(value = "保存记录")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "vo", value = "记录实例", paramType = "body", dataType = "V", required = true),
    })
    default Resp<V> save(@RequestBody V vo) {
        Resp<E> result = getDewService().save(voToEntity(vo));
        if (result.ok()) {
            V body = entityToVO(result.getBody());
            return Resp.success(body);
        } else {
            return Resp.customFail(result.getCode(), result.getMessage());
        }
    }

    @PutMapping(value = "{id}")
    @ApiOperation(value = "根据ID更新记录")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "记录ID", paramType = "path", dataType = "int", required = true),
            @ApiImplicitParam(name = "vo", value = "记录实例", paramType = "body", dataType = "V", required = true),
    })
    default Resp<V> updateById(@PathVariable long id, @RequestBody V vo) {
        Resp<E> result = getDewService().updateById(id, voToEntity(vo));
        if (result.ok()) {
            V body = entityToVO(result.getBody());
            return Resp.success(body);
        } else {
            return Resp.customFail(result.getCode(), result.getMessage());
        }
    }

    @PutMapping(value = "code/{code}")
    @ApiOperation(value = "根据Code更新记录", notes = "记录实体必须存在@Code注解")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "code", value = "记录Code", paramType = "path", dataType = "String", required = true),
            @ApiImplicitParam(name = "vo", value = "记录实例", paramType = "body", dataType = "V", required = true),
    })
    default Resp<V> updateByCode(@PathVariable String code, @RequestBody V vo) {
        Resp<E> result = getDewService().updateByCode(code, voToEntity(vo));
        if (result.ok()) {
            V body = entityToVO(result.getBody());
            return Resp.success(body);
        } else {
            return Resp.customFail(result.getCode(), result.getMessage());
        }
    }

}

