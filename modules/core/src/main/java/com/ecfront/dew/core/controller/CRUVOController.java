package com.ecfront.dew.core.controller;


import com.ecfront.dew.common.Resp;
import com.ecfront.dew.core.dto.PageDTO;
import com.ecfront.dew.core.entity.IdEntity;
import com.ecfront.dew.core.service.CRUService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

public interface CRUVOController<T extends CRUService, V extends Object, E extends IdEntity> extends DewVOController<T, V, E> {

    @GetMapping("")
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

