package com.ecfront.dew.auth.service;

import com.ecfront.dew.auth.entity.Resource;
import com.ecfront.dew.auth.repository.ResourceRepository;
import com.ecfront.dew.common.Resp;
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
    }

    @Override
    public void postDeleteByCode(String code, Optional<Object> preBody) throws RuntimeException {
        resourceRepository.deleteRel(code);
    }

    public Resp<Map<String, List<Resource>>> findByTenantCodeGroupByCategory(String tenantCode) {
        Map<String, List<Resource>> result = resourceRepository.findByTenantCode(tenantCode).stream()
                .collect(Collectors.groupingBy(Resource::getCategory, Collectors.toList()));
        return Resp.success(result);
    }

}