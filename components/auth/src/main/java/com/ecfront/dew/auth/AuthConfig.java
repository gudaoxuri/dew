package com.ecfront.dew.auth;


import com.ecfront.dew.core.config.DewConfig;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "dew")
public class AuthConfig extends DewConfig {

    private DewAuth auth=new DewAuth();

    public static class DewAuth {

        public static final String ENCRYPT_ALGORITHM_SHA265 = "SHA-265";
        public static final String ENCRYPT_ALGORITHM_BCRYPT = "bcrypt";

        private boolean customLogin = false;
        private long tokenExpireSeconds = -1;
        private int loginCaptchaTimes = 3;
        private String encryptAlgorithm = ENCRYPT_ALGORITHM_BCRYPT;
        private String encryptSalt = "";

        public boolean isCustomLogin() {
            return customLogin;
        }

        public void setCustomLogin(boolean customLogin) {
            this.customLogin = customLogin;
        }

        public long getTokenExpireSeconds() {
            return tokenExpireSeconds;
        }

        public void setTokenExpireSeconds(long tokenExpireSeconds) {
            this.tokenExpireSeconds = tokenExpireSeconds;
        }

        public int getLoginCaptchaTimes() {
            return loginCaptchaTimes;
        }

        public void setLoginCaptchaTimes(int loginCaptchaTimes) {
            this.loginCaptchaTimes = loginCaptchaTimes;
        }

        public String getEncryptAlgorithm() {
            return encryptAlgorithm;
        }

        public void setEncryptAlgorithm(String encryptAlgorithm) {
            this.encryptAlgorithm = encryptAlgorithm;
        }

        public String getEncryptSalt() {
            return encryptSalt;
        }

        public void setEncryptSalt(String encryptSalt) {
            this.encryptSalt = encryptSalt;
        }

    }

    public DewAuth getAuth() {
        return auth;
    }

    public void setAuth(DewAuth auth) {
        this.auth = auth;
    }
}
