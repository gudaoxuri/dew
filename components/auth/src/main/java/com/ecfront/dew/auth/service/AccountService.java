package com.ecfront.dew.auth.service;

import com.ecfront.dew.auth.AuthConfig;
import com.ecfront.dew.auth.entity.Account;
import com.ecfront.dew.auth.repository.AccountRepository;
import com.ecfront.dew.auth.repository.RoleRepository;
import com.ecfront.dew.common.EncryptHelper;
import com.ecfront.dew.common.JsonHelper;
import com.ecfront.dew.common.Resp;
import com.ecfront.dew.core.Dew;
import com.ecfront.dew.core.service.CRUSService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.NoSuchAlgorithmException;
import java.util.Optional;
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
    @Autowired
    private AuthConfig authConfig;

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
    public Resp<Optional<Object>> preSave(Account entity) throws RuntimeException {
        if ((entity.getLoginId() == null || entity.getLoginId().trim().isEmpty()) &&
                (entity.getMobile() == null || entity.getMobile().trim().isEmpty()) &&
                (entity.getEmail() == null || entity.getEmail().trim().isEmpty())) {
            return Resp.badRequest("Login Id / Mobile / Email not empty.");
        }
        entity.setCode(Dew.Util.createUUID());
        boolean exist;
        if (entity.getLoginId() != null && !entity.getLoginId().trim().isEmpty()) {
            exist = accountRepository.countByLoginId(entity.getLoginId().trim()) != 0;
            if (exist) {
                return Resp.conflict("Login Id exist.");
            }
            entity.setLoginId(entity.getLoginId().trim());
        } else {
            entity.setLoginId(VIRTUAL_INFO_PREFIX + entity.getCode());
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
        if (entity.getExt() == null || entity.getExt().isEmpty()) {
            entity.setExt("{}");
        }
        entity.setPassword(packageEncryptPwd(entity.getCode(), entity.getPassword()));
        return Resp.success(Optional.empty());
    }

    @Override
    public Resp<Optional<Object>> preUpdateById(long id, Account entity) throws RuntimeException {
        if ((entity.getLoginId() == null || entity.getLoginId().trim().isEmpty()) &&
                (entity.getMobile() == null || entity.getMobile().trim().isEmpty()) &&
                (entity.getEmail() == null || entity.getEmail().trim().isEmpty())) {
            return Resp.badRequest("Login Id / Mobile / Email not empty.");
        }
        boolean exist;
        if (entity.getLoginId() != null && !entity.getLoginId().trim().isEmpty()) {
            exist = accountRepository.countByLoginIdAndIdNot(entity.getLoginId().trim(), id) != 0;
            if (exist) {
                return Resp.conflict("Login Id exist.");
            }
            entity.setLoginId(entity.getLoginId().trim());
        } else {
            return Resp.badRequest("Login Id not empty.");
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
        entity.setPassword(packageEncryptPwd(entity.getCode(), entity.getPassword()));
        return Resp.success(Optional.empty());
    }

    @Override
    public Resp<Optional<Object>> preUpdateByCode(String code, Account entity) throws RuntimeException {
        if ((entity.getLoginId() == null || entity.getLoginId().trim().isEmpty()) &&
                (entity.getMobile() == null || entity.getMobile().trim().isEmpty()) &&
                (entity.getEmail() == null || entity.getEmail().trim().isEmpty())) {
            return Resp.badRequest("Login Id / Mobile / Email not empty.");
        }
        boolean exist;
        if (entity.getLoginId() != null && !entity.getLoginId().trim().isEmpty()) {
            exist = accountRepository.countByLoginIdAndCodeNot(entity.getLoginId().trim(), code) != 0;
            if (exist) {
                return Resp.conflict("Login Id exist.");
            }
            entity.setLoginId(entity.getLoginId().trim());
        } else {
            return Resp.badRequest("Login Id not empty.");
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
        entity.setPassword(packageEncryptPwd(entity.getCode(), entity.getPassword()));
        return Resp.success(Optional.empty());
    }

    @Override
    public void postEnableById(long id, Optional<Object> preBody) throws RuntimeException {
        Dew.Service.mq.convertAndSend(Dew.Constant.MQ_AUTH_ACCOUNT_ADD, "", JsonHelper.toJsonString(getById(id).getBody()));
    }

    @Override
    public void postEnableByCode(String code, Optional<Object> preBody) throws RuntimeException {
        Dew.Service.mq.convertAndSend(Dew.Constant.MQ_AUTH_ACCOUNT_ADD, "", JsonHelper.toJsonString(getByCode(code).getBody()));
    }

    @Override
    public void postDisableById(long id, Optional<Object> preBody) throws RuntimeException {
        Dew.Service.mq.convertAndSend(Dew.Constant.MQ_AUTH_ACCOUNT_REMOVE, "", getById(id).getBody().getCode());
    }

    @Override
    public void postDisableByCode(String code, Optional<Object> preBody) throws RuntimeException {
        Dew.Service.mq.convertAndSend(Dew.Constant.MQ_AUTH_ACCOUNT_REMOVE, "", code);
    }

    @Override
    public Account postSave(Account entity, Optional<Object> preBody) throws RuntimeException {
        Dew.Service.mq.convertAndSend(Dew.Constant.MQ_AUTH_ACCOUNT_ADD, "", JsonHelper.toJsonString(entity));
        return entity;
    }

    @Override
    public Account postUpdate(Account entity, Optional<Object> preBody) throws RuntimeException {
        Dew.Service.mq.convertAndSend(Dew.Constant.MQ_AUTH_ACCOUNT_ADD, "", JsonHelper.toJsonString(entity));
        return entity;
    }

    public void addRoleCode(Account account, String roleCode) {
        account.getRoles().add(roleRepository.getByCode(roleCode));
    }

    public void removeRoleCode(Account account, String roleCode) {
        account.setRoles(account.getRoles().stream().filter(c -> !c.getCode().equals(roleCode)).collect(Collectors.toSet()));
    }

    private String packageEncryptPwd(String code, String password) {
        try {
            return EncryptHelper.Symmetric.encrypt(authConfig.getAuth().getEncryptSalt() + code + password, authConfig.getAuth().getEncryptAlgorithm());
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return System.nanoTime() + "";
        }
    }
}
