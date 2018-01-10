package com.ecfront.dew.jdbc;

import com.ecfront.dew.jdbc.sharding.TestSharding;
import com.ecfront.dew.jdbc.test.crud.TestCRUDS;
import com.ecfront.dew.jdbc.test.ds.TestJDBC;
import com.ecfront.dew.jdbc.test.select.TestSelect;
import com.ecfront.dew.jdbc.test.crud.TestCRUDS;
import com.ecfront.dew.jdbc.test.ds.TestJDBC;
import com.ecfront.dew.jdbc.test.select.TestSelect;
import com.ecfront.dew.jdbc.sharding.TestSharding;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = JDBCApplication.class, webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class JdbcTest {
    @Resource
    private TestJDBC testJDBC;

    @Resource
    private TestCRUDS testCRUDS;

    @Resource
    private TestSelect testSelect;

    @Resource
    private TestSharding testSharding;



    /**
     * Select注解测试
     *
     * @throws Exception
     */
    @Test
    public void testSelect() throws Exception {
        testSelect.testAll();
    }

    /**
     * @throws Exception
     */
    @Test
    public void testJDBC() throws Exception {
        testJDBC.testAll();
    }


    /**
     * 脚手架测试
     *
     * @throws Exception
     */
    @Test
    public void testCRUD() throws Exception {
        testCRUDS.testAll();
    }

    @Test
    public void testShardingJDBC()throws Exception{
        testSharding.testSharding();
    }
}
