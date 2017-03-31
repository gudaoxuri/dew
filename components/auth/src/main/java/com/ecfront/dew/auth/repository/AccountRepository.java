package com.ecfront.dew.auth.repository;

import com.ecfront.dew.auth.entity.Account;
import com.ecfront.dew.core.repository.DewRepository;

import javax.annotation.Resource;

@Resource
public interface AccountRepository extends DewRepository<Account> {

    long countByLoginName(String loginName);

    long countByMobile(String mobile);

    long countByEmail(String email);

    long countByLoginNameAndIdNot(String loginName, long withOutId);

    long countByMobileAndIdNot(String mobile, long withOutId);

    long countByEmailAndIdNot(String email, long withOutId);

    long countByLoginNameAndCodeNot(String loginName, String withOutCode);

    long countByMobileAndCodeNot(String mobile, String withOutCode);

    long countByEmailAndCodeNot(String email, String withOutCode);


}
