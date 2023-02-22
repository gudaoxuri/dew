package groop.idealworld.dew.ossutils.config;

import lombok.Builder;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author yiye
 **/
@ConfigurationProperties(prefix = "dew.oss")
public class OssConfigProperties {

    /**
     * oss类型，支持oss,obs,minio
     */
    @Builder.Default
    private String ossType = "oss";
    /**
     * access key ID
     */
    private String accessKeyId;

    /**
     * secret
     */
    private String accessKeyIdSecret;

    /**
     * end point
     */
    private String endpoint;

    /**
     * 临时授权码
     */
    private String securityToken;

    public String getOssType() {
        return ossType;
    }

    public void setOssType(String ossType) {
        this.ossType = ossType;
    }

    public String getAccessKeyId() {
        return accessKeyId;
    }

    public void setAccessKeyId(String accessKeyId) {
        this.accessKeyId = accessKeyId;
    }

    public String getAccessKeyIdSecret() {
        return accessKeyIdSecret;
    }

    public void setAccessKeyIdSecret(String accessKeyIdSecret) {
        this.accessKeyIdSecret = accessKeyIdSecret;
    }

    public String getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    public String getSecurityToken() {
        return securityToken;
    }

    public void setSecurityToken(String securityToken) {
        this.securityToken = securityToken;
    }
}
