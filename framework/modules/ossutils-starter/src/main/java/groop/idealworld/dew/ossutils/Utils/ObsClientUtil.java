package groop.idealworld.dew.ossutils.Utils;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSException;
import com.obs.services.ObsClient;
import org.springframework.util.ObjectUtils;

/**
 * @author yiye
 * @date 2022/4/2
 * @description
 **/
public class ObsClientUtil {

    private static final ThreadLocal<ObsClient> OBS_CLIENT_THREAD_LOCAL = new ThreadLocal<>();

    public static ObsClient getOssClient() {
        ObsClient obsClient = OBS_CLIENT_THREAD_LOCAL.get();
        if(ObjectUtils.isEmpty(obsClient)){
            throw new OSSException("ossClient未创建");
        }
        return obsClient;
    }

    public static void setOssClient(ObsClient ossClient) {
        OBS_CLIENT_THREAD_LOCAL.set(ossClient);
    }

    public static void removeOssClient() {
        OBS_CLIENT_THREAD_LOCAL.remove();
    }
}
