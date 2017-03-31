package com.ecfront.dew.auth.service;

import com.ecfront.dew.auth.entity.Role;
import com.ecfront.dew.auth.repository.ResourceRepository;
import com.ecfront.dew.auth.repository.RoleRepository;
import com.ecfront.dew.common.Resp;
import com.ecfront.dew.core.service.CRUDService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class RoleService implements CRUDService<RoleRepository, Role> {

    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private ResourceRepository resourceRepository;

    @Override
    public Class<Role> getModelClazz() {
        return Role.class;
    }

    @Override
    public RoleRepository getDewRepository() {
        return roleRepository;
    }

    public void addResourceCode(Role role, String resourceCode) {
        role.getResources().add(resourceRepository.getByCode(resourceCode));
    }

    public void removeResourceCode(Role role, String resourceCode) {
        role.setResources(role.getResources().stream().filter(c -> !c.getCode().equals(resourceCode)).collect(Collectors.toSet()));
    }

    @Override
    public Resp<Optional<Object>> preDeleteById(long id) throws RuntimeException {
        return Resp.success(Optional.of(roleRepository.getById(id).getCode()));
    }

    @Override
    public void postDeleteById(long id, Optional<Object> preBody) throws RuntimeException {
        roleRepository.deleteRel((String) preBody.get());
    }

    @Override
    public void postDeleteByCode(String code, Optional<Object> preBody) throws RuntimeException {
        roleRepository.deleteRel(code);
    }

    public Resp<List<Role>> findByTenantCode(String tenantCode) {
        return Resp.success(roleRepository.findByTenantCode(tenantCode));
    }

}
