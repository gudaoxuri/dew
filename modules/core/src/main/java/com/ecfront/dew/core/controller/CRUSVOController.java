package com.ecfront.dew.core.controller;


import com.ecfront.dew.common.Resp;
import com.ecfront.dew.core.dto.PageDTO;
import com.ecfront.dew.core.entity.IdEntity;
import com.ecfront.dew.core.service.CRUSService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.stream.Collectors;

public interface CRUSVOController<T extends CRUSService, V extends Object, E extends IdEntity> extends CRUVOController<T, V, E> {

    @GetMapping(value = "",params = {"enable"})
    default Resp<List<V>> findByStatus(@RequestParam(required = false) Boolean enable) {
        Resp<List<E>> result;
        if (enable == null) {
            result = getDewService().find();
        } else if (enable) {
            result = getDewService().findEnable();
        } else {
            result = getDewService().findDisable();
        }
        if (result.ok()) {
            List<V> body = result.getBody().stream().map(i -> entityToVO(i)).collect(Collectors.toList());
            return Resp.success(body);
        } else {
            return Resp.customFail(result.getCode(), result.getMessage());
        }
    }

    @GetMapping(value = "{pageNumber}/{pageSize}",params = {"enable"})
    default Resp<PageDTO<V>> pagingByStatus(@PathVariable int pageNumber, @PathVariable int pageSize, @RequestParam(required = false) Boolean enable) {
        Resp<PageDTO<E>> result;
        if (enable == null) {
            result = getDewService().paging(pageNumber, pageSize);
        } else if (enable) {
            result = getDewService().pagingEnable(pageNumber, pageSize);
        } else {
            result = getDewService().pagingDisable(pageNumber, pageSize);
        }
        if (result.ok()) {
            List<V> body = result.getBody().getObjects().stream().map(i -> entityToVO(i)).collect(Collectors.toList());
            PageDTO<V> page = PageDTO.build(pageNumber, pageSize, result.getBody().getRecordTotal(), body);
            return Resp.success(page);
        } else {
            return Resp.customFail(result.getCode(), result.getMessage());
        }
    }


    @GetMapping("{id}/enable")
    default Resp<Void> enableById(@PathVariable long id) {
        return getDewService().enableById(id);
    }

    @GetMapping("{id}/disable")
    default Resp<Void> disableById(@PathVariable long id) {
        return getDewService().disableById(id);
    }

    @GetMapping("code/{code}/enable")
    default Resp<Void> enableByCode(@PathVariable String code) {
        return getDewService().enableByCode(code);
    }

    @GetMapping("code/{code}/disable")
    default Resp<Void> disableByCode(@PathVariable String code) {
        return getDewService().disableByCode(code);
    }


}

