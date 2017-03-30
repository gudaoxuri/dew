package com.ecfront.dew.core.controller;


import com.ecfront.dew.common.Resp;
import com.ecfront.dew.core.dto.PageDTO;
import com.ecfront.dew.core.entity.IdEntity;
import com.ecfront.dew.core.service.SimpleServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import java.util.stream.Collectors;

public abstract class SimpleVOController<T extends SimpleServiceImpl, V extends Object, E extends IdEntity> implements VOAssembler<V, E> {

    protected static final Logger logger = LoggerFactory.getLogger(SimpleVOController.class);

    @Autowired
    protected T simpleService;

    private Class<V> voClass;
    private Class<E> entityClass;

    public SimpleVOController() {
        Type[] type = ((ParameterizedType) this.getClass()
                .getGenericSuperclass()).getActualTypeArguments();
        if (type.length == 2) {
            //vo same as entity
            voClass = (Class<V>) type[1];
            entityClass = (Class<E>) type[1];
        } else {
            voClass = (Class<V>) type[1];
            entityClass = (Class<E>) type[2];
        }
    }

    @GetMapping("")
    public Resp<List<V>> find(@RequestParam(required = false) Boolean enable) {
        Resp<List<E>> result;
        if (enable == null) {
            result = simpleService.find();
        } else if (enable) {
            result = simpleService.findEnable();
        } else {
            result = simpleService.findDisable();
        }
        if (result.ok()) {
            List<V> body = result.getBody().stream().map(i -> entityToVO(i)).collect(Collectors.toList());
            return Resp.success(body);
        } else {
            return Resp.customFail(result.getCode(), result.getMessage());
        }
    }

    @GetMapping("{pageNumber}/{pageSize}")
    public Resp<PageDTO<V>> paging(@PathVariable int pageNumber, @PathVariable int pageSize, @RequestParam(required = false) Boolean enable) {
        Resp<PageDTO<E>> result;
        if (enable == null) {
            result = simpleService.paging(pageNumber, pageSize);
        } else if (enable) {
            result = simpleService.pagingEnable(pageNumber, pageSize);
        } else {
            result = simpleService.pagingDisable(pageNumber, pageSize);
        }
        if (result.ok()) {
            List<V> body = result.getBody().getObjects().stream().map(i -> entityToVO(i)).collect(Collectors.toList());
            PageDTO<V> page = PageDTO.build(pageNumber, pageSize, result.getBody().getRecordTotal(), body);
            return Resp.success(page);
        } else {
            return Resp.customFail(result.getCode(), result.getMessage());
        }
    }

    @GetMapping("{id}")
    public Resp<V> get(@PathVariable long id) {
        Resp<E> result = simpleService.get(id);
        if (result.ok()) {
            V body = entityToVO(result.getBody());
            return Resp.success(body);
        } else {
            return Resp.customFail(result.getCode(), result.getMessage());
        }
    }

    @GetMapping("{id}/enable")
    public Resp<Void> enable(@PathVariable long id) {
        return simpleService.enable(id);
    }

    @GetMapping("{id}/disable")
    public Resp<Void> disable(@PathVariable long id) {
        return simpleService.disable(id);
    }

    @PostMapping(value = "")
    public Resp<V> save(@RequestBody V vo) {
        Resp<E> result = simpleService.save(voToEntity(vo));
        if (result.ok()) {
            V body = entityToVO(result.getBody());
            return Resp.success(body);
        } else {
            return Resp.customFail(result.getCode(), result.getMessage());
        }
    }

    @PutMapping(value = "{id}")
    public Resp<V> update(@PathVariable long id, @RequestBody V vo) {
        Resp<E> result = simpleService.update(id, voToEntity(vo));
        if (result.ok()) {
            V body = entityToVO(result.getBody());
            return Resp.success(body);
        } else {
            return Resp.customFail(result.getCode(), result.getMessage());
        }
    }

    @DeleteMapping(value = "{id}")
    public Resp<Object> delete(@PathVariable long id) {
        return simpleService.delete(id);
    }


}

