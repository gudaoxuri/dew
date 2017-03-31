package com.ecfront.dew.auth.service;

import com.ecfront.dew.auth.entity.Role;
import com.ecfront.dew.auth.repository.RoleRepository;
import com.ecfront.dew.core.service.CRUDService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RoleService implements CRUDService<RoleRepository, Role> {

    @Autowired
    private RoleRepository roleRepository;

    @Override
    public Class<Role> getModelClazz() {
        return Role.class;
    }

    @Override
    public RoleRepository getDewRepository() {
        return roleRepository;
    }

}
