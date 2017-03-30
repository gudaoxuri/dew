package com.ecfront.dew.auth.controller;

import com.ecfront.dew.auth.entity.Account;
import com.ecfront.dew.auth.service.AccountService;
import com.ecfront.dew.core.controller.SimpleController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/auth/manage/account/")
public class AccountController extends SimpleController<AccountService, Account> {


}
