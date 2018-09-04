package com.trc.test;


import com.trc.test.auth.AuthTest;
import com.trc.test.cluster.ClusterTest;
import com.trc.test.web.WebTest;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;


@RunWith(SpringRunner.class)
@SpringBootTest(classes = BootTestApplication.class, webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class AllTest {

    @Autowired
    private ClusterTest clusterTest;

    @Autowired
    private WebTest webTest;

    @Resource
    private AuthTest authTest;

    @Test
    public void testAll() throws Exception {
        clusterTest.testAll();
        webTest.testAll();
        authTest.testAll();
    }

}
