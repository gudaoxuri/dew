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

package group.idealworld.dew.example.hbase;

import group.idealworld.dew.core.hbase.HBaseTemplate;
import org.apache.hadoop.hbase.util.Bytes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * Cluster example initiator.
 *
 * @author 迹_Jason
 */
@Component
public class HBaseExampleInitiator {

    private static final Logger logger = LoggerFactory.getLogger(HBaseExampleInitiator.class);

    @Autowired
    private HBaseTemplate hbaseTemplate;

    /**
     * Init.
     *
     * @throws Exception the exception
     */
    @PostConstruct
    public void init() throws Exception {
        String st = hbaseTemplate.get("DMP:D10_DOP.FDN.V2.T_APP_USER", "0002093140000000",
                "0", "reg_platform", (result, row) -> Bytes.toString(result.value()));
        logger.info("result:{}", st);
    }


}
