package com.ecfront.dew.core.controller;

import com.ecfront.dew.common.Page;
import com.ecfront.dew.common.Resp;

import java.util.List;
import java.util.stream.Collectors;

/**
 * VO组装器
 *
 * @param <V> View
 * @param <E> Entity
 */
public interface VOAssembler<V, E> {

    boolean convertAble();

    default V entityToVO(E entity) {
        return (V) entity;
    }

    default E voToEntity(V vo) {
        return (E) vo;
    }

    default Resp<List<V>> convertList(Resp<List<E>> resp) {
        if (!convertAble()) {
            return (Resp) resp;
        }
        if (resp.ok()) {
            List<V> body = resp.getBody().stream().map(this::entityToVO).collect(Collectors.toList());
            return Resp.success(body);
        } else {
            return Resp.customFail(resp.getCode(), resp.getMessage());
        }
    }

    default Resp<Page<V>> convertPage(Resp<Page<E>> resp) {
        if (!convertAble()) {
            return (Resp) resp;
        }
        if (resp.ok()) {
            List<V> body = resp.getBody().getObjects().stream().map(this::entityToVO).collect(Collectors.toList());
            Page<V> page = Page.build(resp.getBody().getPageNumber(), resp.getBody().getPageSize(), resp.getBody().getRecordTotal(), body);
            return Resp.success(page);
        } else {
            return Resp.customFail(resp.getCode(), resp.getMessage());
        }
    }

    default Resp<V> convertObject(Resp<E> resp) {
        if (!convertAble()) {
            return (Resp) resp;
        }
        if (resp.ok()) {
            V body = entityToVO(resp.getBody());
            return Resp.success(body);
        } else {
            return Resp.customFail(resp.getCode(), resp.getMessage());
        }
    }

}
