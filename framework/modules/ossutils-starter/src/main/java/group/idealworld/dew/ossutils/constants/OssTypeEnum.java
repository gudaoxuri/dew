package group.idealworld.dew.ossutils.constants;

/**
 * @author yiye
 */
public enum OssTypeEnum {
    /**
     * 支持oss类型
     */
    OSS("oss"),
    OBS("obs"),
    MINIO("minio");

    private String code;

    OssTypeEnum(String name) {
        this.code = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public static boolean contains(String code) {
        for (OssTypeEnum ossTypeEnum : OssTypeEnum.values()) {
            if (ossTypeEnum.getCode().equals(code)) {
                return true;
            }
        }
        return false;
    }


}
