package com.trc.test;

import com.trc.test.auth.AuthTest;
import com.trc.test.cluster.ClusterTest;
import com.trc.test.notification.NotifyIntegrationTest;
import group.idealworld.dew.test.RedisExtension;
import com.trc.test.web.WebTest;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.testcontainers.junit.jupiter.Testcontainers;

/**
 * General test.
 *
 * @author gudaoxuri
 */
@ExtendWith({ SpringExtension.class, RedisExtension.class })
@ContextConfiguration(initializers = RedisExtension.Initializer.class)
@SpringBootTest(classes = BootTestApplication.class, webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@Testcontainers
public class GeneralTest {

    @Autowired
    private ClusterTest clusterTest;

    @Autowired
    private WebTest webTest;

    @Resource
    private AuthTest authTest;

    @Resource
    private NotifyIntegrationTest notifyIntegrationTest;

    /**
     * Test all.
     *
     * @throws Exception the exception
     */
    @Test
    public void testAll() throws Exception {
        clusterTest.testAll();
        webTest.testAll();
        authTest.testAll();
        notifyIntegrationTest.testAll();
    }

}
