package com.ecfront.dew.auth.service;

import com.ecfront.dew.auth.entity.Resource;
import com.ecfront.dew.auth.repository.ResourceRepository;
import com.ecfront.dew.core.service.SimpleServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class ResourceService extends SimpleServiceImpl<ResourceRepository, Resource> {

}
