package groop.idealworld.dew.ossutils.bean;

import java.util.Map;

/**
 * @author yiye
 */
public class OssCommonParam {
    /**
     * bucket名称
     */
    private String bucketName;

    /**
     * 填写Object完整路径，例如exampledir/exampleobject.txt。Object完整路径中不能包含Bucket名称
     */
    private String objectName;

    /**
     * 本地路径，包含文件名以及类型,上传文件或下载到本地时必传
     */
    private String path;

    /**
     * 到期时间，授权时长，单位毫秒 --默认过期时间为360000毫秒，最大值为32400000毫秒。临时授权相关必传
     */
    private Long expiration;

    /**
     * 头部信息,用户自定义信息，非必传
     */
    private Map<String, String> customHeaders;

    public String getBucketName() {
        return bucketName;
    }

    public void setBucketName(String bucketName) {
        this.bucketName = bucketName;
    }

    public String getObjectName() {
        return objectName;
    }

    public void setObjectName(String objectName) {
        this.objectName = objectName;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Long getExpiration() {
        return expiration;
    }

    public void setExpiration(Long expiration) {
        this.expiration = expiration;
    }

    public Map<String, String> getCustomHeaders() {
        return customHeaders;
    }

    public void setCustomHeaders(Map<String, String> customHeaders) {
        this.customHeaders = customHeaders;
    }
}
