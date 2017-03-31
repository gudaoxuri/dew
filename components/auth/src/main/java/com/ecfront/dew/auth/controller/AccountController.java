package com.ecfront.dew.auth.controller;

import com.ecfront.dew.auth.entity.Account;
import com.ecfront.dew.auth.service.AccountService;
import com.ecfront.dew.core.controller.CRUSController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/auth/manage/account/")
public class AccountController implements CRUSController<AccountService, Account> {

    @Autowired
    private AccountService accountService;

    @Override
    public AccountService getDewService() {
        return accountService;
    }

}
