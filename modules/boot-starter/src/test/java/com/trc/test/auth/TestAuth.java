package com.trc.test.auth;


import com.ecfront.dew.common.$;
import com.tairanchina.csp.dew.Dew;
import com.trc.test.AllTest;
import com.trc.test.auth.dto.OptInfoExt;
import org.junit.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.HashMap;

@Component
public class TestAuth {


    private Logger logger = LoggerFactory.getLogger(TestAuth.class);

    public void testAuth() throws Exception {
        AuthExampleController.User user = new AuthExampleController.User();
        user.setIdCard("331023395739483150");
        user.setName("辰");
        user.setPassword("123456");
        user.setPhone("15957199704");
        String registerRes = $.http.post(AllTest.URL + "user/register", user);
        Assert.assertEquals("200", $.json.toJson(registerRes).get("code").asText());
        AuthExampleController.LoginDTO loginDTO = new AuthExampleController.LoginDTO();
        loginDTO.setIdCard(user.getIdCard());
        loginDTO.setPassword(user.getPassword());
        String loginRes1 = $.http.post(AllTest.URL + "auth/login", loginDTO);
        String loginRes2 = $.http.post(AllTest.URL + "auth/login", loginDTO);
        String token1 = $.json.toJson(loginRes1).get("body").asText();
        String token2 = $.json.toJson(loginRes2).get("body").asText();
        Assert.assertEquals("200", $.json.toJson(loginRes1).get("code").asText());
        Assert.assertEquals("200", $.json.toJson(loginRes2).get("code").asText());
        String businRes1 = $.http.get(AllTest.URL + "business/someopt", new HashMap<String, String>() {{
            put("_token_", token1);
        }});
        logger.info("businRes1:   " + businRes1);
        Assert.assertEquals("200", $.json.toJson(businRes1).get("code").asText());
        OptInfoExt optInfoExt = (OptInfoExt) Dew.auth.getOptInfo(token2).get();
        Dew.auth.removeOptInfo(optInfoExt.getToken());
        String logoutRes = $.http.delete(AllTest.URL + "auth/logout", new HashMap<String, String>() {{
            put("_token_", token1);
        }});
        Assert.assertEquals("200", $.json.toJson(logoutRes).get("code").asText());
    }

    public void testAuth2() throws Exception {
        String token1 = "eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiJ9.eyJzdWIiOiJzdWJqZWN0X2lkIiwiaV92IjoxNTAzODk5ODY2NTIyLCJrX3QiOiJrX3QiLCJpZGVudF9pZCI6ImlkZW50X2lkIiwiaXNzIjoidWNiaXdrdDRzOXI1dmgzNHJiIiwic192IjoxNTAzODk5ODY2NTIzLCJleHAiOjE1MDY2NzE4MTMsImlhdCI6MTUwNjU4NTQyNX0.h-Ld_sa3NxuvCYBQ0Hd3AxyPpmdi0Mh7gNTCz-l-ed-XFcbVzSQshhUrLRJ769fctUjIafMO7kEdL8Xl6tjvkwVIHhNRsIeVJWPgejPO6V3hTefMq8oru4EUmALFlctb6_JVyHIAG99_LnCjdbLzUn3R0IZglQVmkDT1rngCJNM";
        String businRes1 = $.http.get(AllTest.URL + "business/someopt", new HashMap<String, String>() {{
            put("_token_", token1);
        }});

        logger.info("businRes1:   " + businRes1);
    }
}
