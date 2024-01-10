package group.idealworld.dew.ossutils.utils;

import group.idealworld.dew.ossutils.config.OssConfigProperties;
import group.idealworld.dew.ossutils.general.DewOssClient;

/**
 * @author yiye
 **/
public class OssClientUtil {

    private static final ThreadLocal<Object> OSS_CLIENT_THREAD_LOCAL = new ThreadLocal<>();

    private OssClientUtil() {
    }

    public static Object getOssClient() {
        return OSS_CLIENT_THREAD_LOCAL.get();
    }

    public static void setOssClient(Object ossClient) {
        OSS_CLIENT_THREAD_LOCAL.set(ossClient);
    }

    public static void removeOssClient() {
        if (OSS_CLIENT_THREAD_LOCAL.get() != null) {
            OSS_CLIENT_THREAD_LOCAL.remove();
        }
    }

    public static DewOssClient init(OssConfigProperties ossConfigProperties) {
        return new DewOssClient(ossConfigProperties);
    }


}
