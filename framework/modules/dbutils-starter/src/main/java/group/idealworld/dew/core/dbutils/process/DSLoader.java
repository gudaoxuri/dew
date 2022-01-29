/*
 * Copyright 2020. the original author or authors.
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

package group.idealworld.dew.core.dbutils.process;

import com.alibaba.druid.pool.DruidDataSource;
import group.idealworld.dew.core.dbutils.DewDBUtils;
import group.idealworld.dew.core.dbutils.dialect.Dialect;
import group.idealworld.dew.core.dbutils.dialect.DialectFactory;
import group.idealworld.dew.core.dbutils.dto.DBUtilsConfig;
import group.idealworld.dew.core.dbutils.dto.DSConfig;
import group.idealworld.dew.core.dbutils.utils.YamlHelper;
import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import javax.sql.DataSource;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
public class DSLoader {

    private static final Map<String, DSInfo> MULTI_DS = new HashMap<>();

    public static DSInfo getDSInfo(String dsCode) {
        if (!MULTI_DS.containsKey(dsCode)) {
            throw new RuntimeException("[DewDBUtils]Can't find dsCode [" + dsCode + "]");
        }
        return MULTI_DS.get(dsCode);
    }


    public static void load(DBUtilsConfig dbUtilsConfig) {
        loadDS(dbUtilsConfig.getDs());
        if (dbUtilsConfig.getDynamicDS().getEnabled()) {
            loadDynamicDS(dbUtilsConfig.getDynamicDS().getDsCode(), dbUtilsConfig.getDynamicDS().getFetchSql());
        }
    }

    public static void addDS(DSConfig dsConfig) {
        log.info("[DewDBUtils]Add DS {}", dsConfig.getCode());
        loadDS(new ArrayList<DSConfig>() {
            {
                add(dsConfig);
            }
        });
    }

    public static void removeDS(String dsCode) {
        log.info("[DewDBUtils]Remove DS {}", dsCode);
        if (MULTI_DS.containsKey(dsCode)) {
            MULTI_DS.get(dsCode).setDataSource(null);
            MULTI_DS.remove(dsCode);
        }
    }

    private static void loadDS(List<DSConfig> dsConfigs) {
        dsConfigs.forEach(dsConfig -> {
            Dialect dialect = DialectFactory.parseDialect(dsConfig.getUrl());
            assert dialect != null;
            MULTI_DS.put(dsConfig.getCode(), DSInfo.builder()
                    .dataSource(loadPool(dsConfig, dialect))
                    .dialect(dialect)
                    .dsConfig(dsConfig)
                    .build());
            log.debug("Loaded pool: [{}] {}", dsConfig.getCode(), dsConfig.getUrl());
        });
    }

    public static DataSource loadPool(DSConfig dsConfig, Dialect dialect) {
        DruidDataSource dataSource = new DruidDataSource();
        dataSource.setUrl(dsConfig.getUrl());
        dataSource.setDriverClassName(dialect.getDriver());
        dataSource.setUsername(dsConfig.getUsername());
        dataSource.setPassword(dsConfig.getPassword());
        dataSource.setValidationQuery(dialect.validationQuery());
        if (dsConfig.getPool().getInitialSize() != null) {
            dataSource.setInitialSize(dsConfig.getPool().getInitialSize());
        }
        if (dsConfig.getPool().getMaxActive() != null) {
            dataSource.setMaxActive(dsConfig.getPool().getMaxActive());
        }
        if (dsConfig.getMonitor()) {
            try {
                dataSource.setFilters("wall,mergeStat");
            } catch (SQLException e) {
                log.warn("[DewDBUtils]Monitor set error", e);
            }
        }
        return dataSource;
    }

    private static void loadDynamicDS(String dsCode, String fetchSql) {
        List<Map<String, Object>> result = null;
        try {
            result = DewDBUtils.use(dsCode).find(fetchSql);
        } catch (Exception e) {
            log.error("[DewDBUtils]Multi DS load error : " + e);
        }
        if (null != result) {
            List<DSConfig> dsConfigs = result.stream()
                    .filter(Objects::nonNull)
                    .map(res -> {
                        DSConfig dsConfig = new DSConfig();
                        dsConfig.setPool(new DSConfig.PoolConfig());
                        dsConfig.setCode(res.get("code").toString());
                        dsConfig.setUrl(res.get("url").toString());
                        dsConfig.setUsername(res.get("username").toString());
                        dsConfig.setPassword(res.get("password").toString());
                        dsConfig.setUrl(res.get("url").toString());
                        dsConfig.setMonitor(Integer.parseInt(res.get("monitor").toString()) == 1);
                        dsConfig.getPool().setInitialSize(Integer.parseInt(res.get("pool_initialsize").toString()));
                        dsConfig.getPool().setMaxActive(Integer.parseInt(res.get("pool_maxactive").toString()));
                        return dsConfig;
                    })
                    .collect(Collectors.toList());
            loadDS(dsConfigs);
        }
    }

    @Data
    @Builder
    public static class DSInfo {

        private DataSource dataSource;
        private Dialect dialect;
        private DSConfig dsConfig;

    }

}
