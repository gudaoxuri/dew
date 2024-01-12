package group.idealworld.dew.core.dbutils.dto;

import lombok.Builder;
import lombok.Data;
import lombok.experimental.Tolerate;

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
