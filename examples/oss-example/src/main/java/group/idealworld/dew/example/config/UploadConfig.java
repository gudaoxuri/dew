package group.idealworld.dew.example.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.MultipartConfigFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.unit.DataSize;

import javax.servlet.MultipartConfigElement;
import java.io.File;

/**
 * @author Laughstorm
 */
@Configuration
public class UploadConfig {
    @Bean
    public MultipartConfigElement multipartConfigElement() {
        MultipartConfigFactory factory = new MultipartConfigFactory();
        //  单个数据大小
        // KB,MB
        factory.setMaxFileSize(DataSize.ofBytes(1024*1024*10));
        /// 总上传数据大小
        factory.setMaxRequestSize(DataSize.ofBytes(1024*1024*100));
        File file = new File("/Users/yiye/projectSpace/other/dew/examples/oss-example/file");
        if (!file.exists()) {
            file.mkdirs();
        }
        factory.setLocation(file.getAbsolutePath());
        return factory.createMultipartConfig();
    }
}
