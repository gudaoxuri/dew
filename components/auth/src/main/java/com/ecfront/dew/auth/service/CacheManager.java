package com.ecfront.dew.auth.service;

import com.ecfront.dew.auth.AuthConfig;
import com.ecfront.dew.auth.entity.Account;
import com.ecfront.dew.common.$;
import com.ecfront.dew.core.Dew;
import com.ecfront.dew.core.dto.OptInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

@Component
public class CacheManager {

    @Autowired
    private Token token;
    @Autowired
    private Login login;

    @Component
    public static class Token {

        @Autowired
        private AuthConfig authConfig;

        private static ReentrantLock tokenLock = new ReentrantLock();

        public OptInfo addToken(Account account) {
            // 加锁，避免在多线程下`TOKEN_ID_REL_FLAG + account.code`竞争问题
            tokenLock.lock();
            removeTokenByAccountCode(account.getCode());
            OptInfo optInfo = new OptInfo();
            optInfo.setAccountCode(account.getCode());
            optInfo.setLoginId(account.getLoginId());
            optInfo.setMobile(account.getMobile());
            optInfo.setEmail(account.getEmail());
            optInfo.setName(account.getName());
            optInfo.setRoles(account.getRoles().stream().map(role -> {
                OptInfo.RoleInfo roleInfo = new OptInfo.RoleInfo();
                roleInfo.setCode(role.getCode());
                roleInfo.setName(role.getName());
                roleInfo.setTenantCode(role.getTenantCode());
                return roleInfo;
            }).collect(Collectors.toList()));
            optInfo.setExt(account.getExt());
            optInfo.setLastLoginTime(new Date());
            Dew.cluster.cache.set(Dew.Constant.TOKEN_ID_REL_FLAG + optInfo.getAccountCode(), optInfo.getToken());
            Dew.cluster.cache.set(Dew.Constant.TOKEN_INFO_FLAG + optInfo.getToken(), $.json.toJsonString(optInfo));
            if (authConfig.getAuth().getTokenExpireSeconds() != -1) {
                Dew.cluster.cache.expire(Dew.Constant.TOKEN_ID_REL_FLAG + optInfo.getAccountCode(), (int) authConfig.getAuth().getTokenExpireSeconds());
                Dew.cluster.cache.expire(Dew.Constant.TOKEN_INFO_FLAG + optInfo.getToken(), (int) authConfig.getAuth().getTokenExpireSeconds());
            }
            tokenLock.unlock();
            return optInfo;
        }

        public void removeTokenByAccountCode(String accountCode) {
            String token = getToken(accountCode);
            if (token != null) {
                removeToken(token);
            }
        }

        public void removeToken(String token) {
            OptInfo tokenInfo = getTokenInfo(token);
            if (tokenInfo != null) {
                Dew.cluster.cache.del(Dew.Constant.TOKEN_ID_REL_FLAG + tokenInfo.getAccountCode());
                Dew.cluster.cache.del(Dew.Constant.TOKEN_INFO_FLAG + token);
            }
        }

        public String getToken(String accountCode) {
            return Dew.cluster.cache.get(Dew.Constant.TOKEN_ID_REL_FLAG + accountCode);
        }

        public OptInfo getTokenInfo(String token) {
            return $.json.toObject(Dew.cluster.cache.get(Dew.Constant.TOKEN_INFO_FLAG + token), OptInfo.class);
        }

        public void updateTokenInfo(Account account) {
            String token = getToken(account.getCode());
            if (token != null) {
                OptInfo oldTokenInfo = getTokenInfo(token);
                if (oldTokenInfo == null) {
                    // 在某些情况下（如缓存被清空）可能存在原token信息不存在，此时要求重新登录
                    removeToken(token);
                } else {
                    OptInfo newTokenInfo = new OptInfo();
                    newTokenInfo.setToken(oldTokenInfo.getToken());
                    newTokenInfo.setAccountCode(account.getCode());
                    newTokenInfo.setLoginId(account.getLoginId());
                    newTokenInfo.setMobile(account.getMobile());
                    newTokenInfo.setEmail(account.getEmail());
                    newTokenInfo.setName(account.getName());
                    newTokenInfo.setRoles(account.getRoles().stream().map(role -> {
                        OptInfo.RoleInfo roleInfo = new OptInfo.RoleInfo();
                        roleInfo.setCode(role.getCode());
                        roleInfo.setName(role.getName());
                        roleInfo.setTenantCode(role.getTenantCode());
                        return roleInfo;
                    }).collect(Collectors.toList()));
                    newTokenInfo.setExt(account.getExt());
                    newTokenInfo.setLastLoginTime(oldTokenInfo.getLastLoginTime());
                    Dew.cluster.cache.set(Dew.Constant.TOKEN_INFO_FLAG + newTokenInfo.getToken(), $.json.toJsonString(newTokenInfo));
                }
            }
        }

    }

    @Component
    public static class Login {
        // 连续登录错误次数
        private static final String LOGIN_ERROR_TIMES_FLAG = "dew:auth:login:error:times:";
        // 登录验证码的字符
        private static final String LOGIN_CAPTCHA_TEXT_FLAG = "dew:auth:login:captcha:text";
        // 登录验证码的图片
        private static final String LOGIN_CAPTCHA_IMAGE_FLAG = "dew:auth:login:captcha:image";

        public long addLoginErrorTimes(String tryLoginInfo) {
            return Dew.cluster.cache.incrBy(LOGIN_ERROR_TIMES_FLAG + tryLoginInfo, 1L);
        }

        public long getLoginErrorTimes(String tryLoginInfo) {
            return Dew.cluster.cache.incrBy(LOGIN_ERROR_TIMES_FLAG + tryLoginInfo, 0L);
        }

        public void removeLoginErrorTimes(String tryLoginInfo) {
            Dew.cluster.cache.del(LOGIN_ERROR_TIMES_FLAG + tryLoginInfo);
        }

        public String getCaptchaText(String tryLoginInfo) {
            return Dew.cluster.cache.hget(LOGIN_CAPTCHA_TEXT_FLAG, tryLoginInfo);
        }

        public String getCaptchaImage(String tryLoginInfo) {
            return Dew.cluster.cache.hget(LOGIN_CAPTCHA_IMAGE_FLAG, tryLoginInfo);
        }

        public void addCaptcha(String tryLoginInfo, String text, String imageInfo) {
            Dew.cluster.cache.hset(LOGIN_CAPTCHA_TEXT_FLAG, tryLoginInfo, text);
            Dew.cluster.cache.hset(LOGIN_CAPTCHA_IMAGE_FLAG, tryLoginInfo, imageInfo);
        }

        public void removeCaptcha(String tryLoginInfo) {
            Dew.cluster.cache.hdel(LOGIN_CAPTCHA_TEXT_FLAG, tryLoginInfo);
            Dew.cluster.cache.hdel(LOGIN_CAPTCHA_IMAGE_FLAG, tryLoginInfo);
        }

    }

    public Token getToken() {
        return token;
    }

    public void setToken(Token token) {
        this.token = token;
    }

    public Login getLogin() {
        return login;
    }

    public void setLogin(Login login) {
        this.login = login;
    }
}
