package com.ecfront.dew.appexample;

import com.ecfront.dew.core.Dew;
import com.ecfront.dew.core.dist.LockService;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = APPExampleApplication.class, properties = {"spring.profiles.active=test"})
public class LockTest {

    @Test
    public void testLock() throws Exception {
        LockService lock = Dew.Service.lock("abc");
        Assert.assertTrue(!lock.isLock());
        lock.lock();
        Assert.assertTrue(lock.isLock());
    }

}
