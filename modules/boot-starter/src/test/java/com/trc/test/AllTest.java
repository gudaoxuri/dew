package com.trc.test;


import com.trc.test.auth.TestAuth;
import com.trc.test.dewutil.TestDewUtil;
import com.trc.test.web.TestWeb;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;


@RunWith(SpringRunner.class)
@SpringBootTest(classes =BootTestApplicationWithAnnotation.class, webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class AllTest {

    public static final String URL = "http://127.0.0.1:8080/";

    @Resource
    private TestCluster testCluster;

    @Resource
    private TestWeb testWeb;

    @Resource
    private TestAuth testAuth;

    @Resource
    private TestDewUtil testDewUtil;


    /**
     * 缓存，map缓存，分布式锁测试
     *
     * @throws Exception
     */
    @Test
    public void testCluster() throws Exception {
        testCluster.testAll();
    }

    /**
     * 数据验证测、响应格式、异常处理等测试
     *
     * @throws Exception
     */
    @Test
    public void testWeb() throws Exception {
        testWeb.testAll();
    }

    /**
     * 权限测试，含logger测试
     *
     * @throws Exception
     */
    @Test
    public void testAuth() throws Exception {
        testAuth.testAuth();
    }

    /**
     * Dew类的util测试
     *
     * @throws Exception
     */
    @Test
    public void testDewUtil() throws Exception {
        testDewUtil.testAll();
    }


}
