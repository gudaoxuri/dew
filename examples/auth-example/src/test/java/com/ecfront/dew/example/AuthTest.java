package com.ecfront.dew.example;

import com.ecfront.dew.common.$;
import com.ecfront.dew.common.Resp;
import com.ecfront.dew.example.auth.AuthExampleApplication;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashMap;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = AuthExampleApplication.class, webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class AuthTest {

    private static final String url = "http://127.0.0.1:8080/";

    @Test
    public void testAuth() throws Exception {
        // 模拟用户注册
        $.http.post(url + "/user/register", "{\"name\":\"张三\",\"idCard\":\"123456\",\"password\":\"123\"}");
        // 模拟用户登录
        String token = Resp.generic($.http.post(url + "/auth/login", "{\"password\":\"123\",\"idCard\":\"123456\"}"), String.class).getBody();
        // 模拟业务操作
        Resp<Void> resp = Resp.generic($.http.get(url + "/business/someopt", new HashMap<String, String>() {{
            put("_token_", token);
        }}), Void.class);
        Assert.assertTrue(resp.ok());
        // 模拟用户注销
        $.http.delete(url + "/auth/logout", new HashMap<String, String>() {{
            put("_token_", token);
        }});
        resp = Resp.generic($.http.get(url + "/business/someopt", new HashMap<String, String>() {{
            put("_token_", token);
        }}), Void.class);
        Assert.assertFalse(resp.ok());
    }

}