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

package group.idealworld.dew.core.dbutils.dto;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * 配置类
 *
 * @author gudaoxuri
 */
@ConfigurationProperties(prefix = "ds")
@Data
public class DBUtilsConfig {

    private List<DSConfig> ds = new ArrayList<>();
    private DynamicDS dynamicDS = new DynamicDS();

    @Data
    public static class DynamicDS {

        private Boolean enabled = false;
        private String dsCode;
        private String fetchSql = "select code,url,username,password,monitor,pool_initialSize,pool_maxActive from multi_ds";

    }
}
