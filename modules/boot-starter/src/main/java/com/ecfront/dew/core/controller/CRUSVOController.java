package com.ecfront.dew.core.controller;


import com.ecfront.dew.common.Page;
import com.ecfront.dew.common.Resp;
import com.ecfront.dew.core.service.CRUSService;
import com.ecfront.dew.core.service.CRUSService;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import java.util.List;

public interface CRUSVOController<T extends CRUSService, P, V, E> extends CRUVOController<T, P, V, E> {

    @Override
    default boolean convertAble() {
        return true;
    }

    @GetMapping(value = "", params = {"enabled"})
    @ApiOperation(value = "根据状态获取记录列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "enabled", value = "状态", paramType = "query", dataType = "boolean"),
    })
    default Resp<List<V>> findByStatus(@RequestParam(required = false) Boolean enabled) {
        Resp<List<E>> result;
        if (enabled == null) {
            result = getService().find();
        } else if (enabled) {
            result = getService().findEnabled();
        } else {
            result = getService().findDisabled();
        }
        return convertList(result);
    }

    @GetMapping(value = "{pageNumber}/{pageSize}", params = {"enabled"})
    @ApiOperation(value = "根据状态获取记录分页列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageNumber", value = "当前页（从0开始）", paramType = "path", dataType = "int", required = true),
            @ApiImplicitParam(name = "pageSize", value = "每页显示记录数", paramType = "path", dataType = "int", required = true),
            @ApiImplicitParam(name = "enabled", value = "状态", paramType = "query", dataType = "boolean"),
    })
    default Resp<Page<V>> pagingByStatus(@PathVariable long pageNumber, @PathVariable int pageSize, @RequestParam(required = false) Boolean enabled) {
        Resp<Page<E>> result;
        if (enabled == null) {
            result = getService().paging(pageNumber, pageSize);
        } else if (enabled) {
            result = getService().pagingEnabled(pageNumber, pageSize);
        } else {
            result = getService().pagingDisabled(pageNumber, pageSize);
        }
        return convertPage(result);
    }

    @PutMapping("{id}/enable")
    @ApiOperation(value = "根据ID启用记录")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "记录ID", paramType = "path", dataType = "int", required = true),
    })
    default Resp<Void> enableById(@PathVariable P id) {
        return getService().enableById(id);
    }

    @DeleteMapping("{id}/disable")
    @ApiOperation(value = "根据ID禁用记录")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "记录ID", paramType = "path", dataType = "int", required = true),
    })
    default Resp<Void> disableById(@PathVariable P id) {
        return getService().disableById(id);
    }

    @PutMapping("code/{code}/enable")
    @ApiOperation(value = "根据Code启用记录", notes = "记录实体必须存在@Code注解")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "code", value = "记录Code", paramType = "path", dataType = "string", required = true),
    })
    default Resp<Void> enableByCode(@PathVariable String code) {
        return getService().enableByCode(code);
    }

    @DeleteMapping("code/{code}/disable")
    @ApiOperation(value = "根据Code禁用记录", notes = "记录实体必须存在@Code注解")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "code", value = "记录Code", paramType = "path", dataType = "string", required = true),
    })
    default Resp<Void> disableByCode(@PathVariable String code) {
        return getService().disableByCode(code);
    }


}

