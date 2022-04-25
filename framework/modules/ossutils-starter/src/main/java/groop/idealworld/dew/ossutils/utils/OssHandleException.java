package groop.idealworld.dew.ossutils.utils;

import groop.idealworld.dew.ossutils.bean.OssCommonParam;
import org.springframework.util.StringUtils;

/**
 * @ProjectName: build
 * @Package: groop.idealworld.dew.ossutils.Utils
 * @ClassName: OssHandleException
 * @Author: yiye
 * @Description: 异常处理
 * @Date: 2022/4/24 15:25
 * @Version: 1.0
 */
public class OssHandleException {

    /**
     * @param param
     * @return
     * @throws Exception
     * @Description: 异常处理
     * @Date: 2020/4/24 15:25
     * @Version: 1.0
     */
    public static void isNull(OssCommonParam param) {
        if (param == null) {
            throw new IllegalArgumentException("param必要参数不能为空");
        }
        if (!StringUtils.hasLength(param.getBucketName()) || !StringUtils.hasLength(param.getObjectName())){
            throw new IllegalArgumentException("操作对象存储服务器必要参数不能为空");
        }
    }

    /**
     * @param param
     * @return
     * @throws Exception
     * @Description: 异常处理
     * @Date: 2020/4/24 15:25
     * @Version: 1.0
     */
    public static void isExpirationNull(OssCommonParam param) {
        isNull(param);
        if (param.getExpiration() == null || param.getExpiration() <= 0) {
            throw new IllegalArgumentException("expiration不能为空");
        }
    }
}
