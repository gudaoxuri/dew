package com.ecfront.dew.auth.service;

import com.ecfront.dew.auth.entity.Resource;
import com.ecfront.dew.auth.repository.ResourceRepository;
import com.ecfront.dew.common.$;
import com.ecfront.dew.common.Resp;
import com.ecfront.dew.core.Dew;
import com.ecfront.dew.core.service.CRUDService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ResourceService implements CRUDService<ResourceRepository, Resource> {

    @Autowired
    private ResourceRepository resourceRepository;

    @Override
    public Class<Resource> getModelClazz() {
        return Resource.class;
    }

    @Override
    public ResourceRepository getDewRepository() {
        return resourceRepository;
    }

    @Override
    public Resp<Optional<Object>> preSave(Resource entity) throws RuntimeException {
        entity.setMethod(entity.getMethod().toUpperCase());
        return CRUDService.super.preSave(entity);
    }

    @Override
    public Resp<Optional<Object>> preUpdateById(long id, Resource entity) throws RuntimeException {
        entity.setMethod(entity.getMethod().toUpperCase());
        return CRUDService.super.preUpdateById(id, entity);
    }

    @Override
    public Resp<Optional<Object>> preUpdateByCode(String code, Resource entity) throws RuntimeException {
        entity.setMethod(entity.getMethod().toUpperCase());
        return CRUDService.super.preUpdateByCode(code, entity);
    }

    @Override
    public Resp<Optional<Object>> preDeleteById(long id) throws RuntimeException {
        return Resp.success(Optional.of(resourceRepository.getById(id).getCode()));
    }

    @Override
    public void postDeleteById(long id, Optional<Object> preBody) throws RuntimeException {
        resourceRepository.deleteRel((String) preBody.get());
        Dew.cluster.mq.publish(Dew.Constant.MQ_AUTH_RESOURCE_REMOVE, (String) preBody.get());
    }

    @Override
    public void postDeleteByCode(String code, Optional<Object> preBody) throws RuntimeException {
        resourceRepository.deleteRel(code);
        Dew.cluster.mq.publish(Dew.Constant.MQ_AUTH_RESOURCE_REMOVE, code);
    }

    @Override
    public Resource postSave(Resource entity, Optional<Object> preBody) throws RuntimeException {
        Dew.cluster.mq.publish(Dew.Constant.MQ_AUTH_RESOURCE_ADD, $.json.toJsonString(entity));
        return entity;
    }

    @Override
    public Resource postUpdate(Resource entity, Optional<Object> preBody) throws RuntimeException {
        Dew.cluster.mq.publish(Dew.Constant.MQ_AUTH_RESOURCE_ADD, $.json.toJsonString(entity));
        return entity;
    }

    public Resp<Map<String, List<Resource>>> findByTenantCodeGroupByCategory(String tenantCode) {
        Map<String, List<Resource>> result = resourceRepository.findByTenantCode(tenantCode).stream()
                .collect(Collectors.groupingBy(Resource::getCategory, Collectors.toList()));
        return Resp.success(result);
    }

}