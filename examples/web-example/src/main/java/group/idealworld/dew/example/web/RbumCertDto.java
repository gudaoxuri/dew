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
