package com.ecfront.dew.auth.service;

import com.ecfront.dew.auth.entity.Account;
import com.ecfront.dew.auth.repository.AccountRepository;
import com.ecfront.dew.core.service.SimpleServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class AccountService extends SimpleServiceImpl<AccountRepository, Account> {

}
