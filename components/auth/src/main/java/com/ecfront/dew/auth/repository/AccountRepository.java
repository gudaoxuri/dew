package com.ecfront.dew.auth.repository;

import com.ecfront.dew.auth.entity.Account;
import com.ecfront.dew.core.repository.DewRepository;

import javax.annotation.Resource;

@Resource
public interface AccountRepository extends DewRepository<Account> {

    Account getByLoginIdOrMobileOrEmail(String loginId, String mobile, String email);

    long countByLoginId(String loginId);

    long countByMobile(String mobile);

    long countByEmail(String email);

    long countByLoginIdAndIdNot(String loginId, long withOutId);

    long countByMobileAndIdNot(String mobile, long withOutId);

    long countByEmailAndIdNot(String email, long withOutId);

    long countByLoginIdAndCodeNot(String loginId, String withOutCode);

    long countByMobileAndCodeNot(String mobile, String withOutCode);

    long countByEmailAndCodeNot(String email, String withOutCode);


}
