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

import lombok.Builder;
import lombok.Data;
import lombok.experimental.Tolerate;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author gudaoxuri
 */
@Data
@Builder
public class DSConfig {

    private String code;
    private String url;
    private String username;
    private String password;
    @Builder.Default
    private Boolean monitor = false;
    @Builder.Default
    private PoolConfig pool = new PoolConfig();

    @Data
    @Builder
    public static class PoolConfig {

        @Builder.Default
        private Integer initialSize = 5;
        @Builder.Default
        private Integer maxActive = 20;

        @Tolerate
        public PoolConfig() {
        }

    }

    @Tolerate
    public DSConfig() {
    }
}
