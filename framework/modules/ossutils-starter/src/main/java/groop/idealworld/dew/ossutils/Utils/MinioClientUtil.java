package groop.idealworld.dew.ossutils.Utils;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSException;
import io.minio.MinioClient;
import org.springframework.util.ObjectUtils;

/**
 * @author yiye
 * @date 2022/4/2
 * @description
 **/
public class MinioClientUtil {

    private static final ThreadLocal<MinioClient> OSS_CLIENT_THREAD_LOCAL = new ThreadLocal<>();

    public static MinioClient getOssClient() {
        MinioClient minioClient = OSS_CLIENT_THREAD_LOCAL.get();
        if(ObjectUtils.isEmpty(minioClient)){
            throw new OSSException("ossClient未创建");
        }
        return minioClient;
    }

    public static void setOssClient(MinioClient minioClient) {
        OSS_CLIENT_THREAD_LOCAL.set(minioClient);
    }

    public static void removeOssClient() {
        OSS_CLIENT_THREAD_LOCAL.remove();
    }
}
