package groop.idealworld.dew.ossutils.general;

import com.obs.services.ObsConfiguration;
import groop.idealworld.dew.ossutils.config.OssConfigProperties;
import groop.idealworld.dew.ossutils.handle.DewOssHandleClient;

/**
 * @ProjectName: build
 * @Package: groop.idealworld.dew.ossutils.general
 * @ClassName: ObsSpecialExector
 * @Author: yiye
 * @Description: 华为特有操作接口
 * @Date: 2022/4/5 1:05 上午
 * @Version: 1.0
 */
public interface ObsSpecialExecutor extends DewOssClient {
    /**
     * 创建oss客户端,基本使用的情况下无需手动创建客户端,增加个性化配置
     *
     * @param properties               oss地址配置信息
     * @param config       拓展额外配置信息,个性化配置
     * @return 客户端
     */
    <T> DewOssHandleClient<T> buildOssClient(OssConfigProperties properties, ObsConfiguration config);
}
