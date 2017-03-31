package com.ecfront.dew.auth.repository;

import com.ecfront.dew.auth.entity.Resource;
import com.ecfront.dew.core.repository.DewRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

@javax.annotation.Resource
public interface ResourceRepository extends DewRepository<Resource> {

    @Modifying
    @Query(value = "DELETE FROM dew_rel_role_resource  WHERE resource_code = ?1", nativeQuery = true)
    void deleteRel(String resourceCode);

    @Query(value = "SELECT t FROM #{#entityName} t WHERE t.tenantCode = ?1 OR t.tenantCode = ''")
    List<Resource> findByTenantCode(String tenantCode);
}
