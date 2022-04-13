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

    private String name;

    OssTypeEnum (String name ){
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


}
