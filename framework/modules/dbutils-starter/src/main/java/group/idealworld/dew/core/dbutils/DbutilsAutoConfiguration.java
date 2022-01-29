package group.idealworld.dew.core.dbutils;


import group.idealworld.dew.core.dbutils.dto.DBUtilsConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(DBUtilsConfig.class)
public class DbutilsAutoConfiguration {

    @Bean
    public DewDB dewDB(DBUtilsConfig DBUtilsConfig) {
        DewDBUtils.init(DBUtilsConfig);
        return DewDBUtils.use("default");
    }

}
