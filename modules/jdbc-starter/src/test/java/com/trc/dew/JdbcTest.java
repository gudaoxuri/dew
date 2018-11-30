package com.trc.dew;

import com.trc.dew.ds.TestJDBC;
import com.trc.dew.select.TestSelect;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = JdbcApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class JdbcTest {

    @Resource
    private TestJDBC testJDBC;

    @Resource
    private TestSelect testSelect;

    @Test
    public void testSelect() throws Exception {
        testSelect.testAll();
    }

    @Test
    public void testJDBC() throws Exception {
        testJDBC.testAll();
    }

}
