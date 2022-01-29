/*
 * Copyright 2021. the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package group.idealworld.dew.core.dbutils;

import group.idealworld.dew.core.dbutils.dto.Meta;
import group.idealworld.dew.core.dbutils.dto.Page;
import lombok.Data;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;


@SpringBootApplication
@SpringBootTest
public class DbutilsTest {

    private static final Logger log = LoggerFactory.getLogger(DbutilsTest.class);

    @Autowired
    private DewDB dewDB;

    @Test
    public void testCreateAndUpdate() throws SQLException, SQLException {
        Map<String, String> fields = new HashMap<>();
        fields.put("id", "long");
        fields.put("name", "String");
        fields.put("age", "Int");
        fields.put("height1", "Float");
        fields.put("height2", "Double");
        fields.put("createTime", "Date");
        fields.put("asset", "BigDecimal");
        fields.put("addr", "String");
        fields.put("enable", "Boolean");
        fields.put("txt", "text");
        dewDB.createTableIfNotExist("test", "测试表", fields, new HashMap<String, String>() {{
            put("name", "姓名");
            put("age", "年龄");
        }}, new ArrayList<String>() {{
            add("name");
        }}, new ArrayList<String>() {{
            add("name");
        }}, "id");
        Map<String, Object> values = new HashMap<>();
        values.put("id", 100);
        values.put("name", "gudaoxuri");
        values.put("age", 29);
        values.put("height1", 1.1);
        values.put("height2", 1.1d);
        values.put("asset", new BigDecimal("2.343"));
        values.put("enable", true);
        values.put("addr", "浙江杭州");
        //  values.put("createTime", new java.sql.Date());
        values.put("txt", "浙江杭州");
        dewDB.insert("test", values);
        values.put("name", "孤岛旭日");
        dewDB.modify("test", "id", 100, values);
        Map<String, Object> res = dewDB.getByPk("test", "id", 100);
        Assert.assertEquals("孤岛旭日", res.get("name"));
        Assert.assertEquals(29, res.get("age"));
        Assert.assertEquals(1.1f, res.get("height1"));
        Assert.assertEquals(1.1d, res.get("height2"));
        Assert.assertEquals("浙江杭州", res.get("addr"));
        Assert.assertEquals("浙江杭州", res.get("txt"));
        dewDB.delete("test", "id", 100);
        Assert.assertNull(dewDB.getByPk("test", "id", 100));
    }

    @Test
    public void testMeta() throws Exception {
        testCreateTable(dewDB);
        List<Meta> metas = dewDB.getMetaData("tuser");
        Assert.assertEquals("id", metas.get(0).getLabel());
        Meta meta = dewDB.getMetaData("tuser", "name");
        Assert.assertEquals("name", meta.getLabel());
        testDropTable(dewDB);
    }

    @Test
    public void testFlow() throws SQLException, IOException {
        testCreateTable(dewDB);
        dewDB.update("insert into tuser (id,name,password,age,asset,enable) values ( ? , ? , ? , ? , ? , ? )",
                1, "张三", "123", 22, 2333.22, true);
        dewDB.batch("insert into tuser (id,name,password,age,asset,enable) values ( ? , ? , ? , ? , ? , ? )", new Object[][]{
                {2, "李四", "123", 22, 2333.22, true},
                {3, "王五1", "123", 22, 2333.22, false},
                {4, "王五2", "123", 22, 2333.22, false},
                {5, "王五3", "123", 20, 2333.22, false}
        });
        // get
        Assert.assertEquals(1, dewDB.get("select * from tuser where id = ?", 1).get("id"));
        // count
        Assert.assertEquals(5, dewDB.count("select * from tuser"));
        // find
        Assert.assertEquals(4, dewDB.find("select * from tuser where age = ?", 22).size());
        // page
        Page<Map<String, Object>> pageResult = dewDB.page("select * from tuser", 1, 2);
        Assert.assertEquals(5, pageResult.getRecordTotal());
        Assert.assertEquals(3, pageResult.getPageTotal());
        // get
        User user = dewDB.get("select * from tuser where id = ? ", User.class, 1);
        Assert.assertEquals(1, user.getId());
        // find
        List<User> users = dewDB.find("select * from tuser where age = ?", User.class, 22);
        Assert.assertEquals(4, users.size());

        testDropTable(dewDB);
    }

    @Test
    public void testPool() throws Exception {
        testCreateTable(dewDB);
        dewDB.update("insert into tuser (id,name,password,age,asset,enable) values ( ? , ? , ? , ? , ? , ? )",
                1, "张三", "123", 22, 2333.22, true);
        dewDB.batch("insert into tuser (id,name,password,age,asset,enable) values ( ? , ? , ? , ? , ? , ? )", new Object[][]{
                {2, "李四", "123", 22, 2333.22, true},
                {3, "王五1", "123", 22, 2333.22, false},
                {4, "王五2", "123", 22, 2333.22, false},
                {5, "王五3", "123", 20, 2333.22, false}
        });
        final CountDownLatch watch = new CountDownLatch(10000);
        final AtomicInteger count = new AtomicInteger(0);
        for (int i = 0; i < 100; i++) {
            new Thread(() -> {
                for (int i1 = 0; i1 < 100; i1++) {
                    try {
                        log.debug(">>>>>>>>>>>>>>" + count.incrementAndGet());
                        watch.countDown();
                        // find
                        Assert.assertEquals(4, dewDB.find("select * from tuser where age = ?", 22).size());
                        // page
                        Page<Map<String, Object>> pageResult = dewDB.page("select * from tuser", 1, 2);
                        Assert.assertEquals(5, pageResult.getRecordTotal());
                        Assert.assertEquals(3, pageResult.getPageTotal());
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }
        watch.await();
        testDropTable(dewDB);
    }


    @Test
    public void testTransaction() throws SQLException {
        testCreateTable(dewDB);
        //rollback test
        dewDB.open();
        dewDB.update("insert into tuser (id,name,password,age,asset,enable) values ( ? , ? , ? , ? , ? , ? )",
                1, "张三", "123", 22, 2333.22, true);
        dewDB.rollback();
        Assert.assertEquals(0, dewDB.count("select * from tuser"));

        //error test
        dewDB.open();
        dewDB.update("insert into tuser (id,name,password,age,asset,enable) values ( ? , ? , ? , ? , ? , ? )",
                1, "张三", "123", 22, 2333.22, true);
        //has error
        try {
            dewDB.update("insert into tuser (id,name,password,age,asset,enable) values ( ? , ? , ? , ? , ? , ? )",
                    1, "张三", "123", 22, 2333.22);
            dewDB.commit();
        } catch (SQLException e) {
            log.warn("[DewDBUtils]Has Error!");
        }
        Assert.assertEquals(0, dewDB.count("select * from tuser"));

        //commit test
        dewDB.open();
        dewDB.update("insert into tuser (id,name,password,age,asset,enable) values ( ? , ? , ? , ? , ? , ? )",
                1, "张三", "123", 22, 2333.22, true);
        dewDB.commit();
        Assert.assertEquals(1, dewDB.count("select * from tuser"));

        testDropTable(dewDB);
    }

    @Test
    public void testDataType() throws Exception {
        DewDB db = DewDBUtils.use("default");
        db.ddl("create table datatype(" +
                "id int not null," +
                "name varchar(255)," +
                "dt date," +
                "dt2 datetime," +
                "ts timestamp," +
                "age int," +
                "primary key(id)" +
                ")");
        Date now = new Date();
        db.insert("datatype", new HashMap<String, Object>() {
            {
                put("id", 1);
                put("name", "测试");
                put("age", 1);
                put("dt", now);
                put("dt2", now);
                put("ts", new Timestamp(now.getTime()));
            }
        });
        List<DataTypeTest> result = db.find("select * from datatype", DataTypeTest.class);
        Assert.assertEquals(1, result.size());
        Assert.assertEquals(1L, result.get(0).getId().longValue());
        Assert.assertEquals("测试", result.get(0).getName());
        Assert.assertEquals(1, result.get(0).getAge().longValue());
        Assert.assertEquals(now.getTime(), result.get(0).getTs().getTime());
        Assert.assertEquals(now.getDay(), result.get(0).getDt().getDay());
        Assert.assertEquals(now.getTime(), result.get(0).getDt2().getTime());
    }

    @Data
    public static class DataTypeTest {
        private Long id;
        private String name;
        private Date dt;
        private Date dt2;
        private Timestamp ts;
        private Long age;
    }

    private void testCreateTable(DewDB db) throws SQLException {
        db.ddl("create table tuser(" +
                "id int not null," +
                "name varchar(255)," +
                "password varchar(255)," +
                "age int," +
                "asset decimal," +
                "enable boolean," +
                "primary key(id)" +
                ")");
    }

    private void testDropTable(DewDB db) throws SQLException {
        db.ddl("drop table tuser");
    }

}
