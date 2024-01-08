package group.idealworld.dew.ossutils.utils;

import group.idealworld.dew.ossutils.bean.OssCommonParam;
import org.springframework.util.StringUtils;

/**
 * @author yiye
 */
public class OssHandleException {

    private OssHandleException() {
    }

    public static void isNull(OssCommonParam param) {
        if (param == null) {
            throw new IllegalArgumentException("param必要参数不能为空");
        }
        if (!StringUtils.hasLength(param.getBucketName()) || !StringUtils.hasLength(param.getObjectName())) {
            throw new IllegalArgumentException("操作对象存储服务器必要参数不能为空");
        }
    }

    public static void isExpirationNull(OssCommonParam param) {
        isNull(param);
        if (param.getExpiration() == null || param.getExpiration() <= 0) {
            throw new IllegalArgumentException("expiration不能为空");
        }
    }
}
