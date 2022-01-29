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

package group.idealworld.dew.example.web;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.Date;

/**
 * 资源凭证Dto
 *
 * @author gudaoxuri
 */
public class RbumCertDto {

    @Data
    @SuperBuilder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(title = "添加资源凭证请求")
    public static class RbumCertAddReq implements Serializable {

        @NotNull
        @NotBlank
        @Size(max = 1000)
        @Schema(title = "应用认证用途", required = true)
        private String note;

        @Schema(title = "应用认证有效时间")
        private Date validTime;

    }

    @Data
    @SuperBuilder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(title = "资源凭证详细信息")
    public static class RbumCertDetailResp implements Serializable {

        @NotNull
        @NotBlank
        @Size(max = 1000)
        @Schema(title = "应用认证用途", required = true)
        private String note;

        @NotNull
        @NotBlank
        @Size(max = 255)
        @Schema(title = "应用认证名称", required = true)
        private String ak;

        @NotNull
        @Schema(title = "应用认证有效时间", required = true)
        private Date validTime;

    }

    @Data
    @SuperBuilder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(title = "修改资源凭证请求")
    public static class RbumCertModifyReq implements Serializable {

        @Size(max = 1000)
        @Schema(title = "应用认证用途")
        private String note;

        @Schema(title = "应用认证有效时间")
        private Date validTime;

    }


}
