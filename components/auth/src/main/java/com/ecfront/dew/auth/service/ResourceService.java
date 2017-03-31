package com.ecfront.dew.auth.service;

import com.ecfront.dew.auth.entity.Resource;
import com.ecfront.dew.auth.repository.ResourceRepository;
import com.ecfront.dew.core.service.CRUDService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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


}
