package groop.idealworld.dew.ossutils.config;


import groop.idealworld.dew.ossutils.constants.OssTypeEnum;
import groop.idealworld.dew.ossutils.general.DewOssClient;
import groop.idealworld.dew.ossutils.general.impl.MinioService;
import groop.idealworld.dew.ossutils.general.impl.ObsService;
import groop.idealworld.dew.ossutils.general.impl.OssService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

/**
 * @author yiye
 * @date 2022/4/1
 * @description
 **/
@Configuration
@ConditionalOnWebApplication
@EnableConfigurationProperties(OssConfigProperties.class)
public class OssUtilsAutoConfiguration {


//    @Bean
//    public Map<String, DewOssClient> initOssMap() {
//        OssService ossService = new OssService();
//        ObsService obsService = new ObsService();
//        MinioService minioService = new MinioService();
//        Map<String, DewOssClient> ossHashMap = new HashMap<>();
//        ossHashMap.put(OssTypeEnum.OSS.getName(),ossService);
//        ossHashMap.put(OssTypeEnum.OBS.getName(),obsService);
//        ossHashMap.put(OssTypeEnum.MINIO.getName(),minioService);
//        return ossHashMap;
//    }

    @Bean
    public DewOssClient initOssBean(OssConfigProperties ossConfigProperties) {
        OssService ossService = new OssService();
        ObsService obsService = new ObsService();
        MinioService minioService = new MinioService();
        Map<String, DewOssClient> ossHashMap = new HashMap<>();
        ossHashMap.put(OssTypeEnum.OSS.getName(),ossService);
        ossHashMap.put(OssTypeEnum.OBS.getName(),obsService);
        ossHashMap.put(OssTypeEnum.MINIO.getName(),minioService);
        return ossHashMap.get(ossConfigProperties.getOssType());
    }


}
