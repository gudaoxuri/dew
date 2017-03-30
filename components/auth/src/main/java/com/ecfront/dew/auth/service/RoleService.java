package com.ecfront.dew.auth.service;

import com.ecfront.dew.auth.entity.Role;
import com.ecfront.dew.auth.repository.RoleRepository;
import com.ecfront.dew.core.service.SimpleServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class RoleService extends SimpleServiceImpl<RoleRepository, Role> {

}
