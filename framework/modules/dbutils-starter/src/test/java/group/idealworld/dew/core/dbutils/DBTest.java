package group.idealworld.dew.core.dbutils;

import com.alibaba.druid.pool.DruidDataSource;
import group.idealworld.dew.core.dbutils.dto.Meta;
import group.idealworld.dew.core.dbutils.dto.Page;
import group.idealworld.dew.core.dbutils.process.DSLoader;
import group.idealworld.dew.test.MySqlExtension;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@ExtendWith({ SpringExtension.class, MySqlExtension.class })
@ContextConfiguration(initializers = MySqlExtension.Initializer.class)
@SpringBootTest
@Testcontainers
public class DBTest {

    private DewDB db;

    @Autowired
    private ConfigurableApplicationContext configurableApplicationContext;

    @BeforeEach
    public void before() {
        db = new DewDB(DSLoader.getDSInfo("default"));
        DruidDataSource dataSource = (DruidDataSource) db.getDsInfo().getDataSource();
        dataSource.setUrl(configurableApplicationContext.getEnvironment().getProperty("spring.datasource.url"));
        db.getDsInfo().setDataSource(dataSource);
    }

    @Test
    public void testCreateAndUpdate() throws SQLException, IOException {
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
        db.createTableIfNotExist("test", "测试表", fields, new HashMap<String, String>() {
            {
                put("name", "姓名");
                put("age", "年龄");
            }
        }, new ArrayList<String>() {
            {
                add("name");
            }
        }, new ArrayList<String>() {
            {
                add("name");
            }
        }, "id");
        Map<String, Object> values = new HashMap<>();
        values.put("id", 100);
        values.put("name", "gudaoxuri");
        values.put("age", 29);
        values.put("height1", 1.1);
        values.put("height2", 1.1d);
        values.put("asset", new BigDecimal("2.343"));
        values.put("enable", true);
        values.put("addr", "浙江杭州");
        // values.put("createTime", new java.sql.Date());
        values.put("txt", "浙江杭州");
        db.insert("test", values);
        values.put("name", "孤岛旭日");
        db.modify("test", "id", 100, values);
        Map<String, Object> res = db.getByPk("test", "id", 100);
        Assert.assertEquals("孤岛旭日", res.get("name"));
        Assert.assertEquals(29, res.get("age"));
        Assert.assertEquals(1.1f, res.get("height1"));
        Assert.assertEquals(1.1d, res.get("height2"));
        Assert.assertEquals("浙江杭州", res.get("addr"));
        Assert.assertEquals("浙江杭州", res.get("txt"));
        db.delete("test", "id", 100);
        Assert.assertNull(db.getByPk("test", "id", 100));
    }

    @Test
    public void testMeta() throws Exception {
        testCreateTable(db);
        List<Meta> metas = db.getMetaData("tuser");
        Assert.assertEquals("id", metas.get(0).getLabel());
        Meta meta = db.getMetaData("tuser", "name");
        Assert.assertEquals("name", meta.getLabel());
        testDropTable(db);
    }

    @Test
    public void testFlow() throws SQLException, IOException {
        testCreateTable(db);
        db.update("insert into tuser (id,name,password,age,asset,enable) values ( ? , ? , ? , ? , ? , ? )",
                1, "张三", "123", 22, 2333.22, true);
        db.batch("insert into tuser (id,name,password,age,asset,enable) values ( ? , ? , ? , ? , ? , ? )",
                new Object[][] {
                        { 2, "李四", "123", 22, 2333.22, true },
                        { 3, "王五1", "123", 22, 2333.22, false },
                        { 4, "王五2", "123", 22, 2333.22, false },
                        { 5, "王五3", "123", 20, 2333.22, false }
                });
        // get
        Assert.assertEquals(1, db.get("select * from tuser where id = ?", 1).get("id"));
        // count
        Assert.assertEquals(5, db.count("select * from tuser"));
        // find
        Assert.assertEquals(4, db.find("select * from tuser where age = ?", 22).size());
        // page
        Page<Map<String, Object>> pageResult = db.page("select * from tuser", 1, 2);
        Assert.assertEquals(5, pageResult.getRecordTotal());
        Assert.assertEquals(3, pageResult.getPageTotal());
        // get
        User user = db.get("select * from tuser where id = ? ", User.class, 1);
        Assert.assertEquals(1, user.getId());
        // find
        List<User> users = db.find("select * from tuser where age = ?", User.class, 22);
        Assert.assertEquals(4, users.size());

        testDropTable(db);
    }

    @Test
    public void testPool() throws Exception {
        testCreateTable(db);
        db.update("insert into tuser (id,name,password,age,asset,enable) values ( ? , ? , ? , ? , ? , ? )",
                1, "张三", "123", 22, 2333.22, true);
        db.batch("insert into tuser (id,name,password,age,asset,enable) values ( ? , ? , ? , ? , ? , ? )",
                new Object[][] {
                        { 2, "李四", "123", 22, 2333.22, true },
                        { 3, "王五1", "123", 22, 2333.22, false },
                        { 4, "王五2", "123", 22, 2333.22, false },
                        { 5, "王五3", "123", 20, 2333.22, false }
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
                        Assert.assertEquals(4, db.find("select * from tuser where age = ?", 22).size());
                        // page
                        Page<Map<String, Object>> pageResult = db.page("select * from tuser", 1, 2);
                        Assert.assertEquals(5, pageResult.getRecordTotal());
                        Assert.assertEquals(3, pageResult.getPageTotal());
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }
        watch.await();
        testDropTable(db);
    }

    @Test
    public void testTransaction() throws SQLException {
        testCreateTable(db);
        // rollback test
        db.open();
        db.update("insert into tuser (id,name,password,age,asset,enable) values ( ? , ? , ? , ? , ? , ? )",
                1, "张三", "123", 22, 2333.22, true);
        db.rollback();
        Assert.assertEquals(0, db.count("select * from tuser"));

        // error test
        db.open();
        db.update("insert into tuser (id,name,password,age,asset,enable) values ( ? , ? , ? , ? , ? , ? )",
                1, "张三", "123", 22, 2333.22, true);
        // has error
        try {
            db.update("insert into tuser (id,name,password,age,asset,enable) values ( ? , ? , ? , ? , ? , ? )",
                    1, "张三", "123", 22, 2333.22);
            db.commit();
        } catch (SQLException e) {
            log.warn("[DewDBUtils]Has Error!");
        }
        Assert.assertEquals(0, db.count("select * from tuser"));

        // commit test
        db.open();
        db.update("insert into tuser (id,name,password,age,asset,enable) values ( ? , ? , ? , ? , ? , ? )",
                1, "张三", "123", 22, 2333.22, true);
        db.commit();
        Assert.assertEquals(1, db.count("select * from tuser"));

        testDropTable(db);
    }

    @Test
    public void testDataType() throws Exception {
        // modify pg not support datetime
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
        db.insert("datatype", new HashMap<>() {
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
        Assert.assertEquals(now.getDay(), result.get(0).getDt().getDay());
    }

    @Data
    public static class DataTypeTest {
        private Long id;
        private String name;
        private Date dt;
        private LocalDateTime dt2;
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