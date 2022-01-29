/*
 * Copyright 2022. the original author or authors
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

package group.idealworld.dew.core.hbase;

import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.ColumnFamilyDescriptorBuilder;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.TableDescriptorBuilder;
import org.apache.hadoop.hbase.util.Bytes;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.annotation.PostConstruct;
import java.io.IOException;

/**
 * Created on 2019/5/6.
 *
 * @author 迹_Jason
 */
@ExtendWith(SpringExtension.class)
@SpringBootApplication
@SpringBootTest
@Disabled("Need start hbase server")
public class HBaseTest {

    private static final String TABLE_NAME = "DMP:hbaseDemo";
    private static final String ROW_KEY = "rk001";
    private static final String FAMILY_NAME = "0";
    private static final String QUALIFIER_NAME = "name";
    private static final String VALUE = "zhangsan";
    @Autowired
    private HBaseTemplate hbaseTemplate;

    /**
     * Before.
     *
     * @throws Exception the exception
     */
    @PostConstruct
    public void before() throws Exception {
        if (!hbaseTemplate.getConnection().getAdmin().tableExists(TableName.valueOf(TABLE_NAME))) {
            TableDescriptorBuilder.ModifyableTableDescriptor tableDesc =
                    new TableDescriptorBuilder.ModifyableTableDescriptor(TableName.valueOf(TABLE_NAME));
            ColumnFamilyDescriptorBuilder.ModifyableColumnFamilyDescriptor columnDesc =
                    new ColumnFamilyDescriptorBuilder.ModifyableColumnFamilyDescriptor(Bytes.toBytes(FAMILY_NAME));
            tableDesc.setColumnFamily(columnDesc);
            hbaseTemplate.getConnection().getAdmin().createTable(tableDesc);
        }
        hbaseTemplate.execute(TABLE_NAME, tb -> {
            Put put = new Put(Bytes.toBytes(ROW_KEY));
            put.addColumn(Bytes.toBytes(FAMILY_NAME), Bytes.toBytes(QUALIFIER_NAME), Bytes.toBytes(VALUE));
            tb.put(put);
            return null;
        });
    }

    /**
     * Hbase test.
     *
     * @throws IOException the io exception
     */
    @Test
    public void hbaseTest() throws IOException {
        String st = hbaseTemplate
                .get(TABLE_NAME, ROW_KEY, FAMILY_NAME, QUALIFIER_NAME,
                        (result, row) -> Bytes.toString(result.value()));
        Assertions.assertEquals(VALUE, st);
    }

    /**
     * After.
     *
     * @throws IOException the io exception
     */
    @AfterEach
    public void after() throws IOException {
        hbaseTemplate.getConnection().getAdmin().disableTable(TableName.valueOf(TABLE_NAME));
        hbaseTemplate.getConnection().getAdmin().deleteTable(TableName.valueOf(TABLE_NAME));
    }
}
