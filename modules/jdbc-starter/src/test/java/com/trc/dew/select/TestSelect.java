package com.trc.dew.select;

import com.ecfront.dew.common.Page;
import com.tairanchina.csp.dew.Dew;
import com.tairanchina.csp.dew.jdbc.DewDS;
import com.trc.dew.select.dao.SystemConfigDao;
import com.trc.dew.select.dao.TestInterfaceDao;
import com.trc.dew.select.dto.ModelDTO;
import com.trc.dew.select.entity.TestSelectEntity;
import org.junit.Assert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class TestSelect {

    @Autowired
    private TestInterfaceDao dao;

    @Autowired
    private SystemConfigDao systemConfigDao;

    public void testAll() throws Exception {
        initialize();
        testMulti();
        testInterface();
    }

    private void initialize() throws Exception {
        JdbcTemplate jdbcTemplate = ((DewDS) Dew.ds()).jdbc();
        jdbcTemplate.execute("CREATE TABLE IF NOT EXISTS test_select_entity\n" +
                "(\n" +
                "id int primary key auto_increment,\n" +
                "code varchar(32),\n" +
                "field_a varchar(255),\n" +
                "field_c varchar(255) not null,\n" +
                "create_user varchar(32) not null,\n" +
                "create_time datetime,\n" +
                "update_user varchar(32) not null,\n" +
                "update_time datetime,\n" +
                "enabled bool\n" +
                ")");
        jdbcTemplate.execute("insert INTO test_select_entity(code,field_a,field_c,create_user,create_time,update_user,update_time,enabled) " +
                "values('aa','测试A','测试B','jiaj','2017-07-08','j','2017-07-08',TRUE)");

        jdbcTemplate.execute("CREATE TABLE `system_config` (\n" +
                "  `id` char(32) NOT NULL,\n" +
                "  `value` varchar(200) DEFAULT NULL COMMENT '参数数值',\n" +
                "  `description` varchar(255) DEFAULT NULL COMMENT '参数说明',\n" +
                "  `level` varchar(255) DEFAULT NULL COMMENT '级别（字典）',\n" +
                "  `create_user` char(32) DEFAULT NULL COMMENT '创建人编码',\n" +
                "  `create_time` datetime DEFAULT NULL COMMENT '创建时间',\n" +
                "  `update_user` char(32) DEFAULT NULL COMMENT '更新人编码',\n" +
                "  `update_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',\n" +
                "  PRIMARY KEY (`id`)\n" +
                ") COMMENT='系统配置';");
        jdbcTemplate.execute("INSERT INTO system_config (`id`,`value`,`description`,`level`,create_user,create_time,update_user,update_time) " +
                "VALUES ('id','value','description','level','jiaj','2017-07-08','j','2017-07-08')");
    }

    private void testInterface() throws Exception {
        TestSelectEntity model = new TestSelectEntity();
        model.setFieldA("测试A");
        Page<TestSelectEntity> page = dao.queryByCustomPaging(model, 1L, 10);
        Assert.assertTrue(page != null);
        page = dao.queryByDefaultPaging(model);
        Assert.assertTrue(page != null);
        List<TestSelectEntity> list = dao.queryList(model);
        Assert.assertTrue(list != null);
        list = dao.queryByField("测试A");
        Assert.assertTrue(list != null);
        list = dao.queryByTowFields("测试A", "测试B");
        Assert.assertTrue(list != null);
        Map<String, Object> objectMap = dao.getMapById(1);
        Assert.assertTrue(objectMap != null);
        model = dao.getById(1);
        Assert.assertTrue(model != null);
    }

    private void testMulti() {
        // 加载entityclassinfo
        TestSelectEntity model = new TestSelectEntity();
        Page<TestSelectEntity> page = dao.queryByCustomPaging(model, 1L, 10);
        Assert.assertTrue(page != null);
        ModelDTO modelDTO = systemConfigDao.testLink("value");
        Assert.assertNotNull(modelDTO);
    }
}
