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

@RestController
public abstract class SimpleVOController<T extends SimpleServiceImpl, V extends Object, E extends IdEntity> implements VOAssembler<V, E> {

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

    protected static final Logger logger = LoggerFactory.getLogger(SimpleVOController.class);


    @GetMapping("")
    public Resp<List<V>> find() {
        Resp<List<E>> result = simpleService.find();
        if (result.ok()) {
            List<V> body = result.getBody().stream().map(i -> entityToVO(i)).collect(Collectors.toList());
            return Resp.success(body);
        } else {
            return Resp.customFail(result.getCode(), result.getMessage());
        }
    }

    @GetMapping("{pageNumber}/{pageSize}/")
    public Resp<PageDTO<V>> paging(@PathVariable int pageNumber, @PathVariable int pageSize) {
        Resp<PageDTO<E>> result = simpleService.paging(pageNumber, pageSize);
        if (result.ok()) {
            List<V> body = result.getBody().getObjects().stream().map(i -> entityToVO(i)).collect(Collectors.toList());
            PageDTO<V> page = PageDTO.build(pageNumber, pageSize, result.getBody().getRecordTotal(), body);
            return Resp.success(page);
        } else {
            return Resp.customFail(result.getCode(), result.getMessage());
        }
    }

    @GetMapping("{id}/")
    public Resp<V> get(@PathVariable String id) {
        Resp<E> result = simpleService.get(id);
        if (result.ok()) {
            V body = entityToVO(result.getBody());
            return Resp.success(body);
        } else {
            return Resp.customFail(result.getCode(), result.getMessage());
        }
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

    @PutMapping(value = "{pk}/")
    public Resp<V> update(@PathVariable String id, @RequestBody V vo) {
        Resp<E> result = simpleService.update(id, voToEntity(vo));
        if (result.ok()) {
            V body = entityToVO(result.getBody());
            return Resp.success(body);
        } else {
            return Resp.customFail(result.getCode(), result.getMessage());
        }
    }

    @DeleteMapping(value = "{pk}/")
    public Resp<Object> delete(@PathVariable String id) {
        return simpleService.delete(id);
    }


}

