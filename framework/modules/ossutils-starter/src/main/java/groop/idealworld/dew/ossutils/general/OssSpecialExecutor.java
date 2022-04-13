package groop.idealworld.dew.ossutils.general;

import com.aliyun.oss.ClientBuilderConfiguration;
import com.aliyun.oss.model.CreateBucketRequest;
import groop.idealworld.dew.ossutils.bean.OssCommonParam;
import groop.idealworld.dew.ossutils.config.OssConfigProperties;
import groop.idealworld.dew.ossutils.handle.DewOssHandleClient;

import java.io.FileInputStream;

/**
 * @ProjectName: build
 * @Package: groop.idealworld.dew.ossutils.general
 * @ClassName: OssSpecialExcutor
 * @Author: yiye
 * @Description: 阿里特有操作接口
 * @Date: 2022/4/5 1:03 上午
 * @Version: 1.0
 */
public interface OssSpecialExecutor extends DewOssClient {
    /**
     * 创建oss客户端,基本使用的情况下无需手动创建客户端，个性化配置
     *
     * @param properties               oss地址配置信息
     * @param config                   拓展额外配置信息,个性化配置
     * @return 客户端
     */
    <T> DewOssHandleClient<T> buildOssClient(OssConfigProperties properties, ClientBuilderConfiguration config);
    /**
     * 简单创建存储空间,个性化需求，createBucketRequest属于oss包中的累
     * @param createBucketRequest createBucketRequest
     */
    void createBucket(CreateBucketRequest createBucketRequest);

    /**
     * 使用签名URL上传文件 --临时授权
     * @param param oss操作常用参数
     * @param inputStream 文件输入流
     */
    void temporaryUploadFile(OssCommonParam param, FileInputStream inputStream);



}
