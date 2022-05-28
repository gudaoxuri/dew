package groop.idealworld.dew.ossutils.general;

import groop.idealworld.dew.ossutils.config.OssConfigProperties;

/**
 * @author: yiye
 */
public interface OssClientInitProcess {
    /**
     * 初始化原始客户端
     *
     * @param config
     * @return 是否初始化成功
     */
    boolean initClient(OssConfigProperties config);
}
