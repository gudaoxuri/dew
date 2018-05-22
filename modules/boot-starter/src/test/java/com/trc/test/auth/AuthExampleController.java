package com.trc.test.auth;

import com.ecfront.dew.common.$;
import com.ecfront.dew.common.Resp;
import com.tairanchina.csp.dew.Dew;
import com.tairanchina.csp.dew.core.DewContext;
import com.trc.test.auth.dto.OptInfoExt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MarkerFactory;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/")
public class AuthExampleController {

    private static Map<String, User> MOCK_USER_CONTAINER = new HashMap<>();

    private Logger oriLogger = LoggerFactory.getLogger(AuthExampleController.class);

    @PostConstruct
    public void init() {
        DewContext.setOptInfoClazz(OptInfoExt.class);
    }

    /**
     * 模拟用户注册
     */
    @PostMapping(value = "user/register")
    public Resp<Void> register(@RequestBody User user) {
        // 实际注册处理
        user.setId($.field.createUUID());
        MOCK_USER_CONTAINER.put(user.getId(), user);
        oriLogger.info("oriLogger:        TEST");
        return Resp.success(null);
    }

    /**
     * 模拟用户登录
     */
    @PostMapping(value = "auth/login")
    public Resp<String> login(@RequestBody LoginDTO loginDTO) throws Exception {
        // 实际登录处理
        User user = MOCK_USER_CONTAINER.values().stream().filter(u -> u.getIdCard().equals(loginDTO.getIdCard())).findFirst().get();
        if (!loginDTO.getPassword().equals(user.getPassword())) {
            throw Dew.E.e("ASXXX0", new Exception("密码错误"));
        }
        String token = $.field.createUUID();
        Dew.auth.setOptInfo(new OptInfoExt()
                .setIdCard(user.getIdCard())
                .setAccountCode($.field.createShortUUID())
                .setToken(token)
                .setName(user.getName())
                .setMobile(user.getPhone()));
        oriLogger.info("oriLogger:        TEST");
        return Resp.success(token);
    }

    /**
     * 模拟业务操作
     */
    @GetMapping(value = "business/someopt")
    public Resp<? extends Object> someOpt() {
        // 获取登录用户信息
        Optional<OptInfoExt> optInfoExtOpt = Dew.auth.getOptInfo();
        oriLogger.info("oriLogger:        info");
        try {
            oriLogger.info("oriLogger.info:      TEST-INFO");
            oriLogger.info("oriLogger.info:      TEST-INFO", optInfoExtOpt.get());
            oriLogger.info("oriLogger.info:      TEST-INFO", optInfoExtOpt.get(), new Date());
            oriLogger.info("oriLogger.info:      TEST-INFO", optInfoExtOpt.get(), new Date(), new Date());
            oriLogger.info("oriLogger.info:      TEST-INFO", optInfoExtOpt.get(), new Date(), new Date(), new Date());
            oriLogger.info("oriLogger.info:      TEST-INFO", new Exception("TEST-THROWABLE"));
        } catch (Exception e) {
            oriLogger.info("e.getMessage:        " + e.getMessage());
        }
        try {
            oriLogger.info("oriLogger.isInfoEnabled(marker):      TEST-WARN", oriLogger.isInfoEnabled(MarkerFactory.getMarker("INFO-MARK")));
            oriLogger.info(MarkerFactory.getMarker("INFO-MARK"), "TEST-INFO");
            oriLogger.info(MarkerFactory.getMarker("INFO-MARK"), "TEST-INFO", optInfoExtOpt.get());
            oriLogger.info(MarkerFactory.getMarker("INFO-MARK"), "TEST-INFO", new Date(), optInfoExtOpt.get());
            oriLogger.info(MarkerFactory.getMarker("INFO-MARK"), "TEST-INFO", new Date(), new Date(), optInfoExtOpt.get());
            oriLogger.info(MarkerFactory.getMarker("INFO-MARK"), "TEST-INFO", new Date(), new Date(), new Date(), optInfoExtOpt.get());
            oriLogger.info(MarkerFactory.getMarker("INFO-MARK"), "TEST-INFO", new Exception("TEST-THROWABLE"));
        } catch (Exception e) {
            oriLogger.info("e.getMessage:        " + e.getMessage());
        }
        try {
            oriLogger.info("oriLogger.isDebugEnabled:     " + oriLogger.isDebugEnabled());
            oriLogger.debug("oriLogger.debug:      TEST-DEBUG");
            oriLogger.debug("oriLogger.debug:      TEST-DEBUG", optInfoExtOpt.get());
            oriLogger.debug("oriLogger.debug:      TEST-DEBUG", optInfoExtOpt.get(), new Date());
            oriLogger.debug("oriLogger.debug:      TEST-DEBUG", optInfoExtOpt.get(), new Date(), new Date());
            oriLogger.debug("oriLogger.debug:      TEST-DEBUG", optInfoExtOpt.get(), new Date(), new Date(), new Date());
            oriLogger.debug("oriLogger.debug:      TEST-DEBUG", new Exception("TEST-THROWABLE"));
        } catch (Exception e) {
            oriLogger.info("e.getMessage:        " + e.getMessage());
        }
        try {
            oriLogger.info("oriLogger.isDebugEnabled(marker):      TEST-WARN", oriLogger.isDebugEnabled(MarkerFactory.getMarker("DEBUG-MARK")));
            oriLogger.debug(MarkerFactory.getMarker("DEBUG-MARK"), "TEST-DEBUG");
            oriLogger.debug(MarkerFactory.getMarker("DEBUG-MARK"), "TEST-DEBUG", optInfoExtOpt.get());
            oriLogger.debug(MarkerFactory.getMarker("DEBUG-MARK"), "TEST-DEBUG", new Date(), optInfoExtOpt.get());
            oriLogger.debug(MarkerFactory.getMarker("DEBUG-MARK"), "TEST-DEBUG", new Date(), new Date(), optInfoExtOpt.get());
            oriLogger.debug(MarkerFactory.getMarker("DEBUG-MARK"), "TEST-DEBUG", new Date(), new Date(), new Date(), optInfoExtOpt.get());
            oriLogger.debug(MarkerFactory.getMarker("DEBUG-MARK"), "TEST-DEBUG", new Exception("TEST-THROWABLE"));
        } catch (Exception e) {
            oriLogger.info("e.getMessage:        " + e.getMessage());
        }
        try {
            oriLogger.info("oriLogger.isWarnEnable:     " + oriLogger.isWarnEnabled());
            oriLogger.warn("oriLogger.warn:      TEST-WARN");
            oriLogger.warn("oriLogger.warn:      TEST-WARN", optInfoExtOpt.get());
            oriLogger.warn("oriLogger.warn:      TEST-WARN", optInfoExtOpt.get(), new Date());
            oriLogger.warn("oriLogger.warn:      TEST-WARN", optInfoExtOpt.get(), new Date(), new Date());
            oriLogger.warn("oriLogger.warn:      TEST-WARN", optInfoExtOpt.get(), new Date(), new Date(), new Date());
            oriLogger.warn("oriLogger.warn:      TEST-WARN", new Exception("TEST-THROWABLE"));
        } catch (Exception e) {
            oriLogger.info("e.getMessage:        " + e.getMessage());
        }
        try {
            oriLogger.info("oriLogger.isWarnEnable(marker):      TEST-WARN", oriLogger.isWarnEnabled(MarkerFactory.getMarker("WARN-MARK")));
            oriLogger.warn(MarkerFactory.getMarker("WARN-MARK"), "TEST-WARN");
            oriLogger.warn(MarkerFactory.getMarker("WARN-MARK"), "TEST-WARN", optInfoExtOpt.get());
            oriLogger.warn(MarkerFactory.getMarker("WARN-MARK"), "TEST-WARN", new Date(), optInfoExtOpt.get());
            oriLogger.warn(MarkerFactory.getMarker("WARN-MARK"), "TEST-WARN", new Date(), new Date(), optInfoExtOpt.get());
            oriLogger.warn(MarkerFactory.getMarker("WARN-MARK"), "TEST-WARN", new Date(), new Date(), new Date(), optInfoExtOpt.get());
            oriLogger.warn(MarkerFactory.getMarker("WARN-MARK"), "TEST-WARN", new Exception("TEST-THROWABLE"));
        } catch (Exception e) {
            oriLogger.info("e.getMessage:        " + e.getMessage());
        }
        try {
            oriLogger.info("oriLogger.isErrorEnable:     " + oriLogger.isErrorEnabled());
            oriLogger.error("oriLogger.error:      TEST-ERROR");
            oriLogger.error("oriLogger.error:      TEST-ERROR", optInfoExtOpt.get());
            oriLogger.error("oriLogger.error:      TEST-ERROR", optInfoExtOpt.get(), new Date());
            oriLogger.error("oriLogger.error:      TEST-ERROR", optInfoExtOpt.get(), new Date(), new Date());
            oriLogger.error("oriLogger.error:      TEST-ERROR", optInfoExtOpt.get(), new Date(), new Date(), new Date());
            oriLogger.error("oriLogger.error:      TEST-ERROR", new Exception("TEST-THROWABLE"));

        } catch (Exception e) {
            oriLogger.info("e.getMessage:        " + e.getMessage());
        }
        try {
            oriLogger.info("oriLogger.isErrorEnable(marker):      TEST-ERROR", oriLogger.isErrorEnabled(MarkerFactory.getMarker("ERROR-MARK")));
            oriLogger.error(MarkerFactory.getMarker("ERROR-MARK"), "TEST-ERROR");
            oriLogger.error(MarkerFactory.getMarker("ERROR-MARK"), "TEST-ERROR", optInfoExtOpt.get());
            oriLogger.error(MarkerFactory.getMarker("ERROR-MARK"), "TEST-ERROR", new Date(), optInfoExtOpt.get());
            oriLogger.error(MarkerFactory.getMarker("ERROR-MARK"), "TEST-ERROR", new Date(), new Date(), optInfoExtOpt.get());
            oriLogger.error(MarkerFactory.getMarker("ERROR-MARK"), "TEST-ERROR", new Date(), new Date(), new Date(), optInfoExtOpt.get());
            oriLogger.error(MarkerFactory.getMarker("ERROR-MARK"), "TEST-ERROR", new Exception("TEST-THROWABLE"));
        } catch (Exception e) {
            oriLogger.info("e.getMessage:        " + e.getMessage());
        }
        try {
            oriLogger.info("oriLogger.isTraceEnabled:     " + oriLogger.isTraceEnabled());
            oriLogger.trace("oriLogger.trace:      TEST-TRACE");
            oriLogger.trace("oriLogger.trace:      TEST-TRACE", optInfoExtOpt.get());
            oriLogger.trace("oriLogger.trace:      TEST-TRACE", optInfoExtOpt.get(), new Date());
            oriLogger.trace("oriLogger.trace:      TEST-TRACE", optInfoExtOpt.get(), new Date(), new Date());
            oriLogger.trace("oriLogger.trace:      TEST-TRACE", optInfoExtOpt.get(), new Date(), new Date(), new Date());
            oriLogger.trace("oriLogger.trace:      TEST-TRACE", new Exception("TEST-THROWABLE"));
        } catch (Exception e) {
            oriLogger.info("e.getMessage:        " + e.getMessage());
        }
        try {
            oriLogger.info("oriLogger.isTraceEnabled(marker):      TEST-WARN", oriLogger.isTraceEnabled(MarkerFactory.getMarker("INFO-MARK")));
            oriLogger.trace(MarkerFactory.getMarker("TRACE-MARK"), "TEST-TRACE");
            oriLogger.trace(MarkerFactory.getMarker("TRACE-MARK"), "TEST-TRACE", optInfoExtOpt.get());
            oriLogger.trace(MarkerFactory.getMarker("TRACE-MARK"), "TEST-TRACE", new Date(), optInfoExtOpt.get());
            oriLogger.trace(MarkerFactory.getMarker("TRACE-MARK"), "TEST-TRACE", new Date(), new Date(), optInfoExtOpt.get());
            oriLogger.trace(MarkerFactory.getMarker("TRACE-MARK"), "TEST-TRACE", new Date(), new Date(), new Date(), optInfoExtOpt.get());
            oriLogger.trace(MarkerFactory.getMarker("TRACE-MARK"), "TEST-TRACE", new Exception("TEST-THROWABLE"));
        } catch (Exception e) {
            oriLogger.info("e.getMessage:        " + e.getMessage());
        }
        oriLogger.info("-----test-----");
        return optInfoExtOpt.<Resp<? extends Object>>map(Resp::success).orElseGet(() -> Resp.unAuthorized("用户认证错误"));
    }

    /**
     * 模拟用户注销
     */
    @DeleteMapping(value = "auth/logout")
    public Resp<Void> logout() {
        oriLogger.info("oriLogger:        TEST");
        oriLogger.info("oriLogger:        TEST");
        // 实际注册处理
        Dew.auth.removeOptInfo();
        return Resp.success(null);
    }

    public static class LoginDTO {

        private String idCard;

        private String password;

        public String getIdCard() {
            return idCard;
        }

        public void setIdCard(String idCard) {
            this.idCard = idCard;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }
    }

    public static class User {

        private String id;

        private String name;

        private String phone;

        private String idCard;

        private String password;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getPhone() {
            return phone;
        }

        public void setPhone(String phone) {
            this.phone = phone;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public String getIdCard() {
            return idCard;
        }

        public void setIdCard(String idCard) {
            this.idCard = idCard;
        }
    }

}
