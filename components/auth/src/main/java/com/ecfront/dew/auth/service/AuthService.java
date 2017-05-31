package com.ecfront.dew.auth.service;

import com.ecfront.dew.auth.AuthConfig;
import com.ecfront.dew.auth.dto.LoginReq;
import com.ecfront.dew.auth.dto.ModifyLoginInfoReq;
import com.ecfront.dew.auth.entity.Account;
import com.ecfront.dew.auth.helper.CaptchaHelper;
import com.ecfront.dew.auth.repository.AccountRepository;
import com.ecfront.dew.common.$;
import com.ecfront.dew.common.Resp;
import com.ecfront.dew.core.Dew;
import com.ecfront.dew.core.dto.OptInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.Optional;
import java.util.Random;

@Service
public class AuthService {

    private static Logger logger = LoggerFactory.getLogger(AuthService.class);

    @Autowired
    private AuthConfig authConfig;
    @Autowired
    private CacheManager cacheManager;
    @Autowired
    private AccountService accountService;
    @Autowired
    private AccountRepository accountRepository;

    private static Random random = new Random(System.nanoTime());

    public Resp<OptInfo> login(LoginReq loginReq) throws IOException, NoSuchAlgorithmException {
        if (authConfig.getAuth().isCustomLogin()) {
            logger.warn("[login] Custom login enabled");
            return Resp.notImplemented("[login] Custom login enabled");
        }
        if ((loginReq.getLoginId() == null || loginReq.getLoginId().trim().isEmpty()) &&
                (loginReq.getMobile() == null || loginReq.getMobile().trim().isEmpty()) &&
                (loginReq.getEmail() == null || loginReq.getEmail().trim().isEmpty()) &&
                (loginReq.getPassword() == null || loginReq.getPassword().trim().isEmpty())) {
            logger.warn("[login] Missing required field : Login Id / Mobile / Email and password from " + Dew.context().getSourceIP());
            return Resp.badRequest("[login] Missing required field : Login Id / Mobile / Email and password");
        }
        String tryLoginInfo = packageTryLoginInfo(loginReq);
        if (authConfig.getAuth().getLoginCaptchaTimes() != -1) {
            long errorTimes = cacheManager.getLogin().getLoginErrorTimes(tryLoginInfo);
            if (errorTimes > authConfig.getAuth().getLoginCaptchaTimes() &&
                    (loginReq.getCaptcha() == null || loginReq.getCaptcha().isEmpty() ||
                            !loginReq.getCaptcha().equals(cacheManager.getLogin().getCaptchaText(tryLoginInfo)))) {
                createCaptcha(tryLoginInfo);
                logger.warn("[login] Captcha not match by " + tryLoginInfo + " from " + Dew.context().getSourceIP());
                return Resp.forbidden("[login] Captcha not match");
            }
        }
        Optional<Account> accountOpt = Optional.ofNullable(accountRepository.getByLoginIdOrMobileOrEmail(loginReq.getLoginId().trim(), loginReq.getMobile().trim(), loginReq.getEmail().trim()));
        if (!accountOpt.isPresent()) {
            cacheManager.getLogin().addLoginErrorTimes(tryLoginInfo);
            createCaptcha(tryLoginInfo);
            logger.warn("[login] Account not exist by " + tryLoginInfo + " from " + Dew.context().getSourceIP());
            return Resp.notFound("[login] Account not exist");
        }
        if (!accountOpt.get().getEnable()) {
            cacheManager.getLogin().addLoginErrorTimes(tryLoginInfo);
            createCaptcha(tryLoginInfo);
            logger.warn("[login] Account disabled by " + tryLoginInfo + " from " + Dew.context().getSourceIP());
            return Resp.locked("[login] Account disabled");
        }
        if (!$.encrypt.symmetric.validate(
                authConfig.getAuth().getEncryptSalt() + accountOpt.get().getCode() + loginReq.getPassword().trim(), accountOpt.get().getPassword(), authConfig.getAuth().getEncryptAlgorithm())) {
            cacheManager.getLogin().addLoginErrorTimes(tryLoginInfo);
            createCaptcha(tryLoginInfo);
            logger.warn("[login] Password not match by " + tryLoginInfo + " from " + Dew.context().getSourceIP());
            return Resp.conflict("[login] Password not match");
        }
        OptInfo tokenInfo = cacheManager.getToken().addToken(accountOpt.get());
        cacheManager.getLogin().removeLoginErrorTimes(tryLoginInfo);
        cacheManager.getLogin().removeCaptcha(tryLoginInfo);
        logger.info("[login] Success ,token:" + tokenInfo.getToken() + " by " + tryLoginInfo + " from " + Dew.context().getSourceIP());
        return Resp.success(tokenInfo);
    }

    public String packageTryLoginInfo(LoginReq loginReq) {
        return loginReq.getLoginId().trim() + "-" + loginReq.getMobile().trim() + "-" + loginReq.getEmail().trim();
    }

    private String createCaptcha(String tryLoginInfo) throws IOException {
        if (cacheManager.getLogin().getLoginErrorTimes(tryLoginInfo) >= authConfig.getAuth().getLoginCaptchaTimes()) {
            String text = random.nextDouble() + "";
            text = text.substring(text.length() - 4);
            String imageInfo = CaptchaHelper.generateToBase64(text);
            cacheManager.getLogin().addCaptcha(tryLoginInfo, text, imageInfo);
            return imageInfo;
        } else {
            return null;
        }
    }

    public Resp<String> getCaptcha(LoginReq loginReq) throws IOException {
        String tryLoginInfo = packageTryLoginInfo(loginReq);
        return Resp.success(createCaptcha(tryLoginInfo));
    }

    public Resp<Void> logout() {
        cacheManager.getToken().removeToken(Dew.context().optInfo().get().getToken());
        return Resp.success(null);
    }


    public Resp<OptInfo> getLoginInfo() {
        return Resp.success(Dew.context().optInfo().get());
    }

    public Resp<Account> updateAccountByLoginInfo(ModifyLoginInfoReq modifyLoginInfoReq) throws NoSuchAlgorithmException {
        Resp<Account> accountR = accountService.getByCode(Dew.context().optInfo().get().getAccountCode());
        if (!accountR.ok() || accountR.getBody() == null) {
            logger.warn("Login Info not found from " + Dew.context().getSourceIP());
            return Resp.unAuthorized("Login Info not found");
        }
        Account account = accountR.getBody();
        if (!$.encrypt.symmetric.validate(
                authConfig.getAuth().getEncryptSalt() + account.getCode() + modifyLoginInfoReq.getOldPassword().trim(), account.getPassword(), authConfig.getAuth().getEncryptAlgorithm())) {
            logger.warn("Password not match from " + Dew.context().getSourceIP());
            return Resp.conflict("Password not match");
        }
        if (modifyLoginInfoReq.getLoginId() != null && !modifyLoginInfoReq.getLoginId().isEmpty()) {
            account.setLoginId(modifyLoginInfoReq.getLoginId().trim());
        }
        if (modifyLoginInfoReq.getMobile() != null && !modifyLoginInfoReq.getMobile().isEmpty()) {
            account.setMobile(modifyLoginInfoReq.getMobile().trim());
        }
        if (modifyLoginInfoReq.getEmail() != null && !modifyLoginInfoReq.getEmail().isEmpty()) {
            account.setEmail(modifyLoginInfoReq.getEmail().trim());
        }
        if (modifyLoginInfoReq.getName() != null && !modifyLoginInfoReq.getName().isEmpty()) {
            account.setName(modifyLoginInfoReq.getName().trim());
        }
        if (modifyLoginInfoReq.getNewPassword() != null && !modifyLoginInfoReq.getNewPassword().isEmpty()) {
            account.setPassword(modifyLoginInfoReq.getNewPassword());
        } else {
            account.setPassword(modifyLoginInfoReq.getOldPassword());
        }
        accountR = accountService.updateByCode(account.getCode(), account);
        if (accountR.ok()) {
            if (modifyLoginInfoReq.getNewPassword() != null && !modifyLoginInfoReq.getNewPassword().isEmpty()) {
                cacheManager.getToken().removeToken(Dew.context().optInfo().get().getToken());
            } else {
                cacheManager.getToken().updateTokenInfo(accountR.getBody());
            }
        }
        return accountR;
    }

}