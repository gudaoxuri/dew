package com.ecfront.dew.auth;

import com.ecfront.dew.common.Resp;
import com.ecfront.dew.core.Dew;
import com.ecfront.dew.common.PageDTO;
import com.ecfront.dew.common.RespHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;

import java.util.List;


public abstract class BasicTest {

    @Autowired
    private TestRestTemplate testRestTemplate;

    private String token;

    protected String u(String uri) {
        if (uri.contains("?")) {
            return uri + "&" + Dew.Constant.TOKEN_VIEW_FLAG + "=" + token;
        } else {
            return uri + "?" + Dew.Constant.TOKEN_VIEW_FLAG + "=" + token;
        }
    }

    protected <E> Resp<E> postAndReturnObj(String uri, Object body, Class<E> clazz) {
        return RespHelper.generic(testRestTemplate.postForObject(u(uri), body, Resp.class), clazz);
    }

    protected <E> Resp<List<E>> postAndReturnList(String uri, Object body, Class<E> clazz) {
        return RespHelper.genericList(testRestTemplate.postForObject(u(uri), body, Resp.class), clazz);
    }

    protected <E> Resp<PageDTO<E>> postAndReturnPage(String uri, Object body, Class<E> clazz) {
        return RespHelper.genericPage(testRestTemplate.postForObject(u(uri), body, Resp.class), clazz);
    }

    protected void put(String uri, Object body) {
        testRestTemplate.put(u(uri), body, Resp.class);
    }

    protected <E> Resp<E> getAndReturnObj(String uri, Class<E> clazz) {
        return RespHelper.generic(testRestTemplate.getForObject(u(uri), Resp.class), clazz);
    }

    protected <E> Resp<List<E>> getAndReturnList(String uri, Class<E> clazz) {
        return RespHelper.genericList(testRestTemplate.getForObject(u(uri), Resp.class), clazz);
    }

    protected <E> Resp<PageDTO<E>> getAndReturnPage(String uri, Class<E> clazz) {
        return RespHelper.genericPage(testRestTemplate.getForObject(u(uri), Resp.class), clazz);
    }

    protected void delete(String uri) {
        testRestTemplate.delete(u(uri), Resp.class);
    }

}
