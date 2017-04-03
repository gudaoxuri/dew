package com.ecfront.dew.auth.service;

import com.ecfront.dew.auth.entity.Resource;
import com.ecfront.dew.auth.repository.ResourceRepository;
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
    public Resp<Optional<Object>> preDeleteById(long id) throws RuntimeException {
        return Resp.success(Optional.of(resourceRepository.getById(id).getCode()));
    }

    @Override
    public void postDeleteById(long id, Optional<Object> preBody) throws RuntimeException {
        resourceRepository.deleteRel((String) preBody.get());
        Dew.Service.mq.convertAndSend(Dew.Constant.MQ_AUTH_RESOURCE_REMOVE, "",preBody.get());
    }

    @Override
    public void postDeleteByCode(String code, Optional<Object> preBody) throws RuntimeException {
        resourceRepository.deleteRel(code);
        Dew.Service.mq.convertAndSend(Dew.Constant.MQ_AUTH_RESOURCE_REMOVE,"", code);
    }

    @Override
    public Resource postSave(Resource entity, Optional<Object> preBody) throws RuntimeException {
        Dew.Service.mq.convertAndSend(Dew.Constant.MQ_AUTH_RESOURCE_ADD,"", entity);
        return entity;
    }

    @Override
    public Resource postUpdate(Resource entity, Optional<Object> preBody) throws RuntimeException {
        Dew.Service.mq.convertAndSend(Dew.Constant.MQ_AUTH_RESOURCE_ADD,"", entity);
        return entity;
    }

    public Resp<Map<String, List<Resource>>> findByTenantCodeGroupByCategory(String tenantCode) {
        Map<String, List<Resource>> result = resourceRepository.findByTenantCode(tenantCode).stream()
                .collect(Collectors.groupingBy(Resource::getCategory, Collectors.toList()));
        return Resp.success(result);
    }

}