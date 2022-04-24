package groop.idealworld.dew.ossutils.utils;

import groop.idealworld.dew.ossutils.config.OssConfigProperties;
import groop.idealworld.dew.ossutils.general.DewOssClient;

/**
 * @author yiye
 * @date 2022/4/2
 * @description
 **/
public class OssClientUtil {

    private static final ThreadLocal<Object> OSS_CLIENT_THREAD_LOCAL = new ThreadLocal<>();

    public static Object getOssClient() {
        return OSS_CLIENT_THREAD_LOCAL.get();
    }

    public static void setOssClient(Object ossClient) {
        OSS_CLIENT_THREAD_LOCAL.set(ossClient);
    }

    public static void removeOssClient() {
        if(OSS_CLIENT_THREAD_LOCAL.get() != null){
            OSS_CLIENT_THREAD_LOCAL.remove();
        }
    }

    public static DewOssClient init(OssConfigProperties ossConfigProperties) {
        return new DewOssClient(ossConfigProperties);
    }


}
