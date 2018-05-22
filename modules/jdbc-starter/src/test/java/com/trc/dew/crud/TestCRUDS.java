package com.trc.dew.crud;

import com.ecfront.dew.common.$;
import com.ecfront.dew.common.Page;
import com.ecfront.dew.common.Resp;

import com.tairanchina.csp.dew.Dew;
import com.tairanchina.csp.dew.jdbc.DewDS;
import com.trc.dew.crud.convert.VOConvert;
import com.trc.dew.crud.entity.TestSelectEntity;
import com.trc.dew.crud.service.CRUDSTestService;
import com.trc.dew.select.dao.TestInterfaceDao;
import org.junit.Assert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class TestCRUDS {

    private String url = "http://127.0.0.1:8080/crud";

    private long pageNumber = 1;
    private long pageSize = 10;

    @Autowired(required = false)
    private TestInterfaceDao dao;

    @Autowired
    private CRUDSTestService crudsTestService;

    public void testAll() throws Exception {
        initialize();
        testCRUD();
        testCRUDS();
        testServiceAndDao();
    }

    private void testServiceAndDao(){
        List<TestSelectEntity> list = new ArrayList<>();
        for (int i=0;i<10;i++){
            TestSelectEntity testSelectEntity = new TestSelectEntity();
            testSelectEntity.setId(100+i);
            testSelectEntity.setFieldA("aaa");
            testSelectEntity.setFieldB("ccc");
            list.add(testSelectEntity);
        }
        dao.insert(list);
        Assert.assertTrue(crudsTestService.existById(102).getBody());
        TestSelectEntity testSelectEntity = crudsTestService.getById(102).getBody();
        Assert.assertTrue(crudsTestService.existByCode(testSelectEntity.getCode()).getBody());
        long num = dao.countAll();
        long enNum = dao.countEnabled();
        long disNum = dao.countDisabled();
        Assert.assertTrue(num==enNum+disNum);
        Page<TestSelectEntity> pagingEnabled = dao.pagingEnabled(1,10);
        Assert.assertTrue(pagingEnabled.getObjects().size() ==10 || (pagingEnabled.getObjects().size() == enNum));
        Page<TestSelectEntity> pagingDisabled = dao.pagingDisabled(1,10);
        Assert.assertTrue(pagingEnabled.getObjects().size() == 10 || (pagingDisabled.getObjects().size() == disNum));
    }

    private void testCRUD() throws Exception {
        // findAll
        Resp<List<TestSelectEntity>> entitiesResp = Resp.genericList($.http.get(url + "/" ), TestSelectEntity.class);
        long recordTotal = entitiesResp.getBody().size();
        VOConvert voConvert = new VOConvert();
        voConvert.convertList(entitiesResp);
        // paging
        Resp<Page<TestSelectEntity>> entitiesPageResp = Resp.genericPage($.http.get(url + String.format("/%d/%d/", pageNumber, pageSize)), TestSelectEntity.class);
        Assert.assertEquals(pageNumber, entitiesPageResp.getBody().getPageNumber());
        Assert.assertEquals(pageSize, entitiesPageResp.getBody().getPageSize());
        Assert.assertEquals(recordTotal, entitiesPageResp.getBody().getRecordTotal());
        long pageTotal = entitiesPageResp.getBody().getPageTotal();
        // save
        TestSelectEntity entity = new TestSelectEntity();
        entity.setFieldA("测试A");
        entity.setFieldB("测试B");
        Resp<TestSelectEntity> entityResp = Resp.generic($.http.post(url + "/", entity), TestSelectEntity.class);
        Assert.assertEquals(entity.getFieldA(), entityResp.getBody().getFieldA());
        Assert.assertEquals(entity.getFieldB(), entityResp.getBody().getFieldB());
        Assert.assertTrue(entityResp.getBody().getCreateTime().isEqual(entityResp.getBody().getUpdateTime()));
        Assert.assertTrue(entityResp.getBody().getEnabled());
        Object id = entityResp.getBody().getId();
        String code = entityResp.getBody().getCode();
        // getById
        entityResp = Resp.generic($.http.get(url + "/" + id), TestSelectEntity.class);
        Assert.assertEquals(entity.getFieldA(), entityResp.getBody().getFieldA());
        Assert.assertEquals(entity.getFieldB(), entityResp.getBody().getFieldB());
        Assert.assertTrue(entityResp.getBody().getCreateTime().isEqual(entityResp.getBody().getUpdateTime()));
        Assert.assertTrue(entityResp.getBody().getEnabled());
        Assert.assertEquals(id, entityResp.getBody().getId());
        Assert.assertEquals(code, entityResp.getBody().getCode());
        // getByCode
        entityResp = Resp.generic($.http.get(url + "/code/" + code), TestSelectEntity.class);
        Assert.assertEquals(entity.getFieldA(), entityResp.getBody().getFieldA());
        Assert.assertEquals(entity.getFieldB(), entityResp.getBody().getFieldB());
        Assert.assertTrue(entityResp.getBody().getCreateTime().isEqual(entityResp.getBody().getUpdateTime()));
        Assert.assertTrue(entityResp.getBody().getEnabled());
        Assert.assertEquals(id, entityResp.getBody().getId());
        Assert.assertEquals(code, entityResp.getBody().getCode());
        // updateById
        entity.setFieldA("测试A->C");
        entityResp = Resp.generic($.http.put(url + "/" + id, entity), TestSelectEntity.class);
        Assert.assertEquals(entity.getFieldA(), entityResp.getBody().getFieldA());
        Assert.assertEquals(entity.getFieldB(), entityResp.getBody().getFieldB());
        Assert.assertFalse(entityResp.getBody().getCreateTime().isEqual(entityResp.getBody().getUpdateTime()));
        Assert.assertTrue(entityResp.getBody().getEnabled());
        Assert.assertEquals(id, entityResp.getBody().getId());
        Assert.assertEquals(code, entityResp.getBody().getCode());
        // updateByCode
        entity.setFieldB("测试B->D");
        entityResp = Resp.generic($.http.put(url + "/code/" + code, entity), TestSelectEntity.class);
        Assert.assertEquals(entity.getFieldA(), entityResp.getBody().getFieldA());
        Assert.assertEquals(entity.getFieldB(), entityResp.getBody().getFieldB());
        Assert.assertFalse(entityResp.getBody().getCreateTime().isEqual(entityResp.getBody().getUpdateTime()));
        Assert.assertTrue(entityResp.getBody().getEnabled());
        Assert.assertEquals(id, entityResp.getBody().getId());
        Assert.assertEquals(code, entityResp.getBody().getCode());
        // getById
        entityResp = Resp.generic($.http.get(url + "/" + id), TestSelectEntity.class);
        Assert.assertEquals(entity.getFieldA(), entityResp.getBody().getFieldA());
        Assert.assertEquals(entity.getFieldB(), entityResp.getBody().getFieldB());
        Assert.assertFalse(entityResp.getBody().getCreateTime().isEqual(entityResp.getBody().getUpdateTime()));
        Assert.assertTrue(entityResp.getBody().getEnabled());
        Assert.assertEquals(id, entityResp.getBody().getId());
        Assert.assertEquals(code, entityResp.getBody().getCode());
        // getByCode
        entityResp = Resp.generic($.http.get(url + "/code/" + code), TestSelectEntity.class);
        Assert.assertEquals(entity.getFieldA(), entityResp.getBody().getFieldA());
        Assert.assertEquals(entity.getFieldB(), entityResp.getBody().getFieldB());
        Assert.assertFalse(entityResp.getBody().getCreateTime().isEqual(entityResp.getBody().getUpdateTime()));
        Assert.assertTrue(entityResp.getBody().getEnabled());
        Assert.assertEquals(id, entityResp.getBody().getId());
        Assert.assertEquals(code, entityResp.getBody().getCode());
        // findAll
        entitiesResp = Resp.genericList($.http.get(url + "/"), TestSelectEntity.class);
        Assert.assertEquals(recordTotal + 1, entitiesResp.getBody().size());
        // paging
        entitiesPageResp = Resp.genericPage($.http.get(url + String.format("/%d/%d/", pageNumber, pageSize)), TestSelectEntity.class);
        Assert.assertEquals(pageNumber, entitiesPageResp.getBody().getPageNumber());
        Assert.assertEquals(pageSize, entitiesPageResp.getBody().getPageSize());
        Assert.assertEquals(recordTotal + 1, entitiesPageResp.getBody().getRecordTotal());
        voConvert.convertPage(entitiesPageResp);
        // deleteById
        Resp deleteResp = Resp.generic($.http.delete(url + "/" + id), Void.class);
        Assert.assertTrue(deleteResp.ok());
        // findAll
        entitiesResp = Resp.genericList($.http.get(url + "/"), TestSelectEntity.class);
        Assert.assertEquals(recordTotal, entitiesResp.getBody().size());
        voConvert.convertObject(entityResp);
        // paging
        entitiesPageResp = Resp.genericPage($.http.get(url + String.format("/%d/%d/", pageNumber, pageSize)), TestSelectEntity.class);
        Assert.assertEquals(pageNumber, entitiesPageResp.getBody().getPageNumber());
        Assert.assertEquals(pageSize, entitiesPageResp.getBody().getPageSize());
        Assert.assertEquals(pageTotal, entitiesPageResp.getBody().getPageTotal());
        Assert.assertEquals(recordTotal, entitiesPageResp.getBody().getRecordTotal());
        // save
        entity = new TestSelectEntity();
        entity.setFieldA("测试A");
        entity.setFieldB("测试B");
        entityResp = Resp.generic($.http.post(url + "/", entity), TestSelectEntity.class);
        id = entityResp.getBody().getId();
        code = entityResp.getBody().getCode();
        // deleteByCode
        deleteResp = Resp.generic($.http.delete(url + "/code/" + code), Void.class);
        Assert.assertTrue(deleteResp.ok());
        // findAll
        entitiesResp = Resp.genericList($.http.get(url + "/"), TestSelectEntity.class);
        Assert.assertEquals(recordTotal, entitiesResp.getBody().size());
    }

    private void testCRUDS() throws Exception {
        // save
        TestSelectEntity entity = new TestSelectEntity();
        entity.setFieldA("测试A");
        entity.setFieldB("测试B");
        entity.setEnabled(true);
        Resp<TestSelectEntity> entityResp = Resp.generic($.http.post(url + "/", entity), TestSelectEntity.class);
        Object oneId = entityResp.getBody().getId();
        String oneCode = entityResp.getBody().getCode();
        // save
        TestSelectEntity otherEntity = new TestSelectEntity();
        otherEntity.setFieldA("测试C");
        otherEntity.setFieldB("测试D");
        otherEntity.setEnabled(false);
        entityResp = Resp.generic($.http.post(url + "/", otherEntity), TestSelectEntity.class);
        Object otherId = entityResp.getBody().getId();
        String otherCode = entityResp.getBody().getCode();
        // getById
        entityResp = Resp.generic($.http.get(url + "/" + oneId), TestSelectEntity.class);
        Assert.assertTrue(entityResp.getBody().getEnabled());
        entityResp = Resp.generic($.http.get(url + "/" + otherId), TestSelectEntity.class);
        Assert.assertFalse(entityResp.getBody().getEnabled());
        // disabledById
        Resp statusResp = Resp.generic($.http.delete(url + "/" + oneId + "/disable"), Void.class);
        Assert.assertTrue(statusResp.ok());
        // enabledById
        statusResp = Resp.generic($.http.put(url + "/" + otherId + "/enable", ""), Void.class);
        Assert.assertTrue(statusResp.ok());
        // getById
        entityResp = Resp.generic($.http.get(url + "/" + oneId), TestSelectEntity.class);
        Assert.assertFalse(entityResp.getBody().getEnabled());
        entityResp = Resp.generic($.http.get(url + "/" + otherId), TestSelectEntity.class);
        Assert.assertTrue(entityResp.getBody().getEnabled());
        // enabledByCode
        statusResp = Resp.generic($.http.put(url + "/code/" + oneCode + "/enable", ""), Void.class);
        Assert.assertTrue(statusResp.ok());
        // disableByCode
        statusResp = Resp.generic($.http.delete(url + "/code/" + otherCode + "/disable"), Void.class);
        Assert.assertTrue(statusResp.ok());
        // getById
        entityResp = Resp.generic($.http.get(url + "/" + oneId), TestSelectEntity.class);
        Assert.assertTrue(entityResp.getBody().getEnabled());
        entityResp = Resp.generic($.http.get(url + "/" + otherId), TestSelectEntity.class);
        Assert.assertFalse(entityResp.getBody().getEnabled());
        // findByStatus status=true
        Resp<List<TestSelectEntity>> entitiesResp = Resp.genericList($.http.get(url + "/?enabled=true"), TestSelectEntity.class);
        Assert.assertEquals(2, entitiesResp.getBody().size());
        Assert.assertTrue(entitiesResp.getBody().get(1).getEnabled());
        Assert.assertEquals(entity.getFieldA(), entitiesResp.getBody().get(1).getFieldA());
        // findByStatus status=false
        entitiesResp = Resp.genericList($.http.get(url + "/?enabled=false"), TestSelectEntity.class);
        Assert.assertEquals(1, entitiesResp.getBody().size());
        Assert.assertFalse(entitiesResp.getBody().get(0).getEnabled());
        Assert.assertEquals(otherEntity.getFieldA(), entitiesResp.getBody().get(0).getFieldA());
        // pagingByStatus status=true
        Resp<Page<TestSelectEntity>> pageEntitiesResp = Resp.genericPage($.http.get(url + "/1/10/?enabled=true"), TestSelectEntity.class);
        Assert.assertEquals(pageNumber, pageEntitiesResp.getBody().getPageNumber());
        Assert.assertEquals(pageSize, pageEntitiesResp.getBody().getPageSize());
        Assert.assertEquals(1, pageEntitiesResp.getBody().getPageTotal());
        Assert.assertEquals(2, pageEntitiesResp.getBody().getRecordTotal());
        Assert.assertEquals(entity.getFieldA(), pageEntitiesResp.getBody().getObjects().get(1).getFieldA());
        // pagingByStatus status=false
        pageEntitiesResp = Resp.genericPage($.http.get(url + "/1/10/?enabled=false"), TestSelectEntity.class);
        Assert.assertEquals(pageNumber, pageEntitiesResp.getBody().getPageNumber());
        Assert.assertEquals(pageSize, pageEntitiesResp.getBody().getPageSize());
        Assert.assertEquals(1, pageEntitiesResp.getBody().getPageTotal());
        Assert.assertEquals(1, pageEntitiesResp.getBody().getRecordTotal());
        Assert.assertEquals(otherEntity.getFieldA(), pageEntitiesResp.getBody().getObjects().get(0).getFieldA());
    }

    private void initialize() throws Exception {
        // ddl
        ((DewDS) Dew.ds()).jdbc().execute("DROP TABLE if EXISTS test_select_entity");
        ((DewDS) Dew.ds()).jdbc().execute("CREATE TABLE IF NOT EXISTS test_select_entity\n" +
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
        ((DewDS) Dew.ds()).jdbc().execute("INSERT  INTO  test_select_entity " +
                "(code,field_a,field_c,create_user,create_time,update_user,update_time,enabled) VALUES " +
                "('A','A-a','A-b','ding',NOW(),'ding',NOW(),TRUE )");
    }
}
