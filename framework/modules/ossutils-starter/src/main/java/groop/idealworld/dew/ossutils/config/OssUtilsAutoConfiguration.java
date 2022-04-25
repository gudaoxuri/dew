package groop.idealworld.dew.ossutils.config;


import groop.idealworld.dew.ossutils.utils.OssClientUtil;
import groop.idealworld.dew.ossutils.constants.OssTypeEnum;
import groop.idealworld.dew.ossutils.general.DewOssClient;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author yiye
 * @date 2022/4/1
 * @description
 **/
@Configuration
@ConditionalOnWebApplication
@EnableConfigurationProperties(OssConfigProperties.class)
public class OssUtilsAutoConfiguration {
    @Bean
    public DewOssClient initOssClient(OssConfigProperties ossConfigProperties) {
        if( !OssTypeEnum.contains(ossConfigProperties.getOssType())){
            throw new IllegalArgumentException("ossType is not support,expect:oss,obs,minio");
        }
        return OssClientUtil.init(ossConfigProperties);
    }


}
