package groop.idealworld.dew.ossutils.constants;

/**
 * @ProjectName: build
 * @Package: groop.idealworld.dew.ossutils.constant
 * @ClassName: OssTypeEnum
 * @Author: yiye
 * @Description: oss类型枚举
 * @Date: 2022/4/5 7:14 下午
 * @Version: 1.0
 */
public enum OssTypeEnum {
    /**
     * 支持oss类型
     */
    OSS("oss"),
    OBS("obs"),
    MINIO("minio");

    private String code;

    OssTypeEnum (String name ){
        this.code = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public static boolean contains(String code){
        for (OssTypeEnum ossTypeEnum : OssTypeEnum.values()) {
            if (ossTypeEnum.getCode().equals(code)) {
                return true;
            }
        }
        return false;
    }


}
