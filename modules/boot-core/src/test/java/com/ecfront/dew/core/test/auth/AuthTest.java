package com.ecfront.dew.core.test.auth;


import com.ecfront.dew.common.$;
import com.ecfront.dew.core.Dew;
import com.ecfront.dew.core.test.TestAll;
import com.ecfront.dew.core.test.auth.dto.OptInfoExt;
import org.junit.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.HashMap;

@Component
public class AuthTest {



    private Logger logger = LoggerFactory.getLogger(AuthTest.class);

    public void testAuth() throws Exception {
        AuthExampleController.User user = new AuthExampleController.User();
        user.setIdCard("331023395739483150");
        user.setName("辰");
        user.setPassword("123456");
        user.setPhone("15957199704");
        String registerRes = $.http.post(TestAll.URL + "user/register", user);
        Assert.assertEquals("200", $.json.toJson(registerRes).get("code").asText());
        AuthExampleController.LoginDTO loginDTO = new AuthExampleController.LoginDTO();
        loginDTO.setIdCard(user.getIdCard());
        loginDTO.setPassword(user.getPassword());
        String loginRes1 = $.http.post(TestAll.URL + "auth/login", loginDTO);
        String loginRes2 = $.http.post(TestAll.URL + "auth/login", loginDTO);
        String token1 = $.json.toJson(loginRes1).get("body").asText();
        String token2 = $.json.toJson(loginRes2).get("body").asText();
        Assert.assertEquals("200", $.json.toJson(loginRes1).get("code").asText());
        Assert.assertEquals("200", $.json.toJson(loginRes2).get("code").asText());
        String businRes1 = $.http.get(TestAll.URL + "business/someopt", new HashMap<String, String>() {{
                put("_token_", token1);
        }});

        logger.info("businRes1:   " +businRes1);
        Assert.assertEquals("200", $.json.toJson(businRes1).get("code").asText());
        OptInfoExt optInfoExt = (OptInfoExt) Dew.Auth.getOptInfo(token2).get();
        OptInfoExt optInfoExt2 = (OptInfoExt)Dew.Auth.getOptInfoByAccCode(optInfoExt.getAccountCode()).get();
        Dew.Auth.removeOptInfo(optInfoExt2.getToken());
        String logoutRes = $.http.delete(TestAll.URL + "auth/logout",new HashMap<String ,String>(){{
            put("_token_", token1);
        }});
        Assert.assertEquals("200", $.json.toJson(logoutRes).get("code").asText());
    }
}
