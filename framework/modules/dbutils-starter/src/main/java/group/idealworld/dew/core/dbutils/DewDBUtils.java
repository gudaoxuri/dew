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

package group.idealworld.dew.core.dbutils;

import group.idealworld.dew.core.dbutils.dto.DBUtilsConfig;
import group.idealworld.dew.core.dbutils.dto.DSConfig;
import group.idealworld.dew.core.dbutils.process.DSLoader;

/**
 * 操作入口类.
 *
 * @author gudaoxuri
 */
public class DewDBUtils {



    /**
     * 初始化数据源.
     *
     * @param dbUtilsConfig 加载配置信息
     */

    public static void init(DBUtilsConfig dbUtilsConfig){
        DSLoader.load(dbUtilsConfig);
    }

    /**
     * 添加数据源.
     *
     * @param dsConfig 配置文件
     */
    public static void addDS(DSConfig dsConfig) {
        DSLoader.addDS(dsConfig);
    }

    /**
     * 删除数据源.
     *
     * @param dsCode 数据源编码
     */
    public static void removeDS(String dsCode) {
        DSLoader.removeDS(dsCode);
    }

    /**
     * 删除数据源.
     *
     * @param dewDB DB实例
     */
    public static void removeDS(DewDB dewDB) {
        if (dewDB != null
                && dewDB.getDsInfo() != null
                && dewDB.getDsInfo().getDsConfig() != null) {
            DSLoader.removeDS(dewDB.getDsInfo().getDsConfig().getCode());
        }
    }

    /**
     * 选择DB实例.
     *
     * @param dsCode 数据源编码
     * @return DB实例
     */
    public static DewDB use(String dsCode) {
        return DewDB.pick(dsCode);
    }
}
