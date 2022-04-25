package groop.idealworld.dew.ossutils.general;

import groop.idealworld.dew.ossutils.config.OssConfigProperties;

/**
 * @ProjectName: build
 * @Package: groop.idealworld.dew.ossutils.general
 * @ClassName: OssClientInitProcess
 * @Author: yiye
 * @Description: init
 * @Date: 2022/4/24 10:45
 * @Version: 1.0
 */
public interface OssClientInitProcess {
    /**
     * 初始化原始客户端
     * @param config
     * @return
     */
    boolean initClient(OssConfigProperties config);
}
