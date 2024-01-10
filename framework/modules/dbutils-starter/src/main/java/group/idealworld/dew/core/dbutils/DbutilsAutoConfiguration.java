package group.idealworld.dew.core.dbutils;


import group.idealworld.dew.core.dbutils.dto.DBUtilsConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(DBUtilsConfig.class)
public class DbutilsAutoConfiguration {

    @Autowired
    private DBUtilsConfig dbUtilsConfig;

    @Bean
    public DewDB dewDB() {
        DewDBUtils.init(dbUtilsConfig);
        return DewDBUtils.use("default");
    }

}
