package groop.idealworld.dew.ossutils.general;

import groop.idealworld.dew.ossutils.bean.ImageProcessParam;
import groop.idealworld.dew.ossutils.bean.OssCommonParam;
import groop.idealworld.dew.ossutils.handle.DewOssHandleClient;
import groop.idealworld.dew.ossutils.config.OssConfigProperties;

import java.io.InputStream;

/**
 * @author yiye
 * @date 2022/4/1
 * @description
 **/
public interface DewOssClient {


    /**
     * 创建存储空间，简单创建
     *
     * @param param oss存储空间参数
     */
    void createBucket(OssCommonParam param);

    /**
     * 判断存储空间是否存在
     *
     * @param param oss操作常用参数
     * @return 结果
     */
    Boolean doesBucketExist(OssCommonParam param);

    /**
     * 删除存储空间
     *
     * @param param oss操作常用参数
     */
    void deleteBucket(OssCommonParam param);

    /**
     * 创建oss客户端，基本使用的情况下无需手动创建客户端 --简单创建
     *
     * @param config               oss地址配置信息
     * @return 客户端
     */
    <T> DewOssHandleClient<T> buildOssClient(OssConfigProperties config);


    /**
     * 关闭oss客户端，基本使用的情况下无需手动关闭客户端
     */
    void closeClient();

    /**
     * 文件上传
     * 使用minio的时候上传除字符对象外的影像文件需传头部信息 content-type：例如：文件拓展名为avi,对应的content-type是video/mp4
     * @param param oss操作常用参数
     */
    void uploadObject(OssCommonParam param);

    /**
     * 文件上传 -- 流式上传
     * 使用minio的时候上传除字符对象外的影像文件需传头部信息 content-type：例如：文件拓展名为avi,对应的content-type是video/mp4
     * @param param oss操作常用参数
     * @param inputStream 文件流
     */
    void uploadObject(OssCommonParam param, InputStream inputStream);

    /**
     * 下载文件,获取文件流自行处理
     *
     * @param param oss操作常用参数
     * @return 文件流
     */
    InputStream downloadFile(OssCommonParam param);

    /**
     * 下载文件到本地
     *
     * @param param oss操作常用参数
     */
    void downloadFileLocal(OssCommonParam param);

    /**
     * 判断文件是否存在
     *
     * @param param oss操作常用参数
     * @return 结果
     */
    Boolean doesObjectExist(OssCommonParam param);

     /**
     * 删除文件
     * @param param oss操作常用参数
     */
    void deleteObject(OssCommonParam param);



    /**
     * 获取签名上传url --临时授权
     * @param param oss操作常用参数
     * @return url
     */
    String temporaryUploadUrl(OssCommonParam param);


    /**
     * 获取签名删除URL --临时授权
     * @param param oss操作常用参数
     * @return url
     */
    String temporaryDeleteUrl(OssCommonParam param);


    /**
     * 获取签名查询访问URL --临时授权
     *
     * @param param oss操作常用参数
     * @return url
     */
    String temporaryUrl(OssCommonParam param);


    /**
     * 获取前端缩率图url
     * @param param oss操作常用参数
     * @param process 图片处理参数
     * @return 临时url
     */
    String imageProcess(OssCommonParam param, ImageProcessParam process);




}
