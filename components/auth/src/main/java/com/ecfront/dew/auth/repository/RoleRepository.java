package com.ecfront.dew.auth.repository;

import com.ecfront.dew.auth.entity.Role;
import com.ecfront.dew.core.repository.DewRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import javax.annotation.Resource;
import java.util.List;

@Resource
public interface RoleRepository extends DewRepository<Role> {

    @Modifying
    @Query(value = "DELETE FROM dew_rel_account_role  WHERE role_code = ?1", nativeQuery = true)
    void deleteRel(String roleCode);

    @Query(value = "SELECT t FROM #{#entityName} t WHERE t.tenantCode = ?1 OR t.tenantCode = ''")
    List<Role> findByTenantCode(String tenantCode);

}
