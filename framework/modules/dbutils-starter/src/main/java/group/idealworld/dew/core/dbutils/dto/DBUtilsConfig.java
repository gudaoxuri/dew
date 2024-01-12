package group.idealworld.dew.core.dbutils.dto;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

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
