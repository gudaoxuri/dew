package com.ecfront.dew.auth.service;

import com.ecfront.dew.auth.entity.Account;
import com.ecfront.dew.auth.repository.AccountRepository;
import com.ecfront.dew.core.service.CRUService;
import com.ecfront.dew.core.service.CRUSService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AccountService implements CRUSService<AccountRepository, Account> {

    @Autowired
    private AccountRepository accountRepository;

    @Override
    public Class<Account> getModelClazz() {
        return Account.class;
    }

    @Override
    public AccountRepository getDewRepository() {
        return accountRepository;
    }

}
