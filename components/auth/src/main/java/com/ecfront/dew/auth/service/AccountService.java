package com.ecfront.dew.auth.service;

import com.ecfront.dew.auth.entity.Account;
import com.ecfront.dew.auth.repository.AccountRepository;
import com.ecfront.dew.auth.repository.RoleRepository;
import com.ecfront.dew.common.Resp;
import com.ecfront.dew.core.Dew;
import com.ecfront.dew.core.service.CRUSService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

/**
 * 账号服务
 * <p>
 * 核心逻辑：LoginName Mobile Email 都必须全局唯一，可以用于登录，
 * preSave/Update会做前置检查，但在并发时可能失效，所以数据库也做了unique，
 * 但业务上允许三选一，所以可能存在空值写入数据库导致unique检查问题，所以对空的字段使用虚拟数据填充
 */
@Service
public class AccountService implements CRUSService<AccountRepository, Account> {

    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private RoleRepository roleRepository;

    @Override
    public Class<Account> getModelClazz() {
        return Account.class;
    }

    @Override
    public AccountRepository getDewRepository() {
        return accountRepository;
    }

    private static final String VIRTUAL_INFO_PREFIX = "v_";
    private static final String VIRTUAL_INFO_EMAIL = "@virtual.is";

    @Override
    public Resp<Account> preSave(Account entity) throws RuntimeException {
        if ((entity.getLoginName() == null || entity.getLoginName().trim().isEmpty()) &&
                (entity.getMobile() == null || entity.getMobile().trim().isEmpty()) &&
                (entity.getEmail() == null || entity.getEmail().trim().isEmpty())) {
            return Resp.badRequest("Login Name / Mobile / Email not empty.");
        }
        entity.setCode(Dew.Util.createUUID());
        boolean exist;
        if (entity.getLoginName() != null && !entity.getLoginName().trim().isEmpty()) {
            exist = accountRepository.countByLoginName(entity.getLoginName().trim()) != 0;
            if (exist) {
                return Resp.conflict("Login Name exist.");
            }
            entity.setLoginName(entity.getLoginName().trim());
        } else {
            entity.setLoginName(VIRTUAL_INFO_PREFIX + entity.getCode());
        }
        if (entity.getMobile() != null && !entity.getMobile().trim().isEmpty()) {
            exist = accountRepository.countByMobile(entity.getMobile().trim()) != 0;
            if (exist) {
                return Resp.conflict("Mobile exist.");
            }
            entity.setMobile(entity.getMobile().trim());
        } else {
            entity.setMobile(VIRTUAL_INFO_PREFIX + entity.getCode());
        }
        if (entity.getEmail() != null && !entity.getEmail().trim().isEmpty()) {
            exist = accountRepository.countByEmail(entity.getEmail().trim()) != 0;
            if (exist) {
                return Resp.conflict("Email exist.");
            }
            if (!Dew.Util.checkEmail(entity.getEmail().trim())) {
                return Resp.badRequest("Email format error.");
            }
            entity.setEmail(entity.getEmail().trim());
        } else {
            entity.setEmail(VIRTUAL_INFO_PREFIX + entity.getCode() + VIRTUAL_INFO_EMAIL);
        }
        return Resp.success(entity);
    }

    @Override
    public Resp<Account> preUpdateById(long id, Account entity) throws RuntimeException {
        if ((entity.getLoginName() == null || entity.getLoginName().trim().isEmpty()) &&
                (entity.getMobile() == null || entity.getMobile().trim().isEmpty()) &&
                (entity.getEmail() == null || entity.getEmail().trim().isEmpty())) {
            return Resp.badRequest("Login Name / Mobile / Email not empty.");
        }
        boolean exist;
        if (entity.getLoginName() != null && !entity.getLoginName().trim().isEmpty()) {
            exist = accountRepository.countByLoginNameAndIdNot(entity.getLoginName().trim(), id) != 0;
            if (exist) {
                return Resp.conflict("Login Name exist.");
            }
            entity.setLoginName(entity.getLoginName().trim());
        } else {
            return Resp.badRequest("Login Name not empty.");
        }
        if (entity.getMobile() != null && !entity.getMobile().trim().isEmpty()) {
            exist = accountRepository.countByMobileAndIdNot(entity.getMobile().trim(), id) != 0;
            if (exist) {
                return Resp.conflict("Mobile exist.");
            }
            entity.setMobile(entity.getMobile().trim());
        } else {
            return Resp.badRequest("Mobile not empty.");
        }
        if (entity.getEmail() != null && !entity.getEmail().trim().isEmpty()) {
            exist = accountRepository.countByEmailAndIdNot(entity.getEmail().trim(), id) != 0;
            if (exist) {
                return Resp.conflict("Email exist.");
            }
            if (!Dew.Util.checkEmail(entity.getEmail().trim())) {
                return Resp.badRequest("Email format error.");
            }
            entity.setEmail(entity.getEmail().trim());
        } else {
            return Resp.badRequest("Email not empty.");
        }
        return Resp.success(entity);
    }

    @Override
    public Resp<Account> preUpdateByCode(String code, Account entity) throws RuntimeException {
        if ((entity.getLoginName() == null || entity.getLoginName().trim().isEmpty()) &&
                (entity.getMobile() == null || entity.getMobile().trim().isEmpty()) &&
                (entity.getEmail() == null || entity.getEmail().trim().isEmpty())) {
            return Resp.badRequest("Login Name / Mobile / Email not empty.");
        }
        boolean exist;
        if (entity.getLoginName() != null && !entity.getLoginName().trim().isEmpty()) {
            exist = accountRepository.countByLoginNameAndCodeNot(entity.getLoginName().trim(), code) != 0;
            if (exist) {
                return Resp.conflict("Login Name exist.");
            }
            entity.setLoginName(entity.getLoginName().trim());
        } else {
            return Resp.badRequest("Login Name not empty.");
        }
        if (entity.getMobile() != null && !entity.getMobile().trim().isEmpty()) {
            exist = accountRepository.countByMobileAndCodeNot(entity.getMobile().trim(), code) != 0;
            if (exist) {
                return Resp.conflict("Mobile exist.");
            }
            entity.setMobile(entity.getMobile().trim());
        } else {
            return Resp.badRequest("Mobile not empty.");
        }
        if (entity.getEmail() != null && !entity.getEmail().trim().isEmpty()) {
            exist = accountRepository.countByEmailAndCodeNot(entity.getEmail().trim(), code) != 0;
            if (exist) {
                return Resp.conflict("Email exist.");
            }
            if (!Dew.Util.checkEmail(entity.getEmail().trim())) {
                return Resp.badRequest("Email format error.");
            }
            entity.setEmail(entity.getEmail().trim());
        } else {
            return Resp.badRequest("Email not empty.");
        }
        return Resp.success(entity);
    }

    public void addRoleCode(Account account, String roleCode) {
        account.getRoles().add(roleRepository.getByCode(roleCode));
    }

    public void removeRoleCode(Account account, String roleCode) {
        account.setRoles(account.getRoles().stream().filter(c -> !c.getCode().equals(roleCode)).collect(Collectors.toSet()));
    }

}
