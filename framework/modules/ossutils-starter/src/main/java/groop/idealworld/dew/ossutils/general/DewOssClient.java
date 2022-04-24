package groop.idealworld.dew.ossutils.general;

import com.aliyun.oss.ClientBuilderConfiguration;
import com.aliyun.oss.model.CreateBucketRequest;
import com.obs.services.ObsConfiguration;
import groop.idealworld.dew.ossutils.bean.ImageProcessParam;
import groop.idealworld.dew.ossutils.bean.OssCommonParam;
import groop.idealworld.dew.ossutils.config.OssConfigProperties;
import groop.idealworld.dew.ossutils.constants.OssTypeEnum;
import groop.idealworld.dew.ossutils.general.impl.MinioService;
import groop.idealworld.dew.ossutils.general.impl.ObsService;
import groop.idealworld.dew.ossutils.general.impl.OssService;
import groop.idealworld.dew.ossutils.handle.DewOssHandleClient;
import io.minio.MinioClient;

import java.io.FileInputStream;
import java.io.InputStream;

/**
 * @ProjectName: build
 * @Package: groop.idealworld.dew.ossutils.handle
 * @ClassName: DewOssClient
 * @Author: yiye
 * @Description: oss集成操作类
 * @Date: 2022/4/22 17:15
 * @Version: 1.0
 */
public class DewOssClient implements OssClientOptProcess {

    private OssClientOptProcess ossClientOptProcess;

    public DewOssClient(OssConfigProperties config){
        if(OssTypeEnum.MINIO.getCode().equals(config.getOssType())){
            MinioService minioService = new MinioService();
            minioService.initClient(config);
            ossClientOptProcess = minioService;
        }else if(OssTypeEnum.OSS.getCode().equals(config.getOssType())){
            OssService ossService = new OssService();
            ossService.initClient(config);
            ossClientOptProcess = ossService;
        }else if(OssTypeEnum.OBS.getCode().equals(config.getOssType())){
            ObsService obsService = new ObsService();
            obsService.initClient(config);
            ossClientOptProcess = obsService;
        }
    }


    /**
     * 创建存储空间，简单创建
     *
     * @param param oss存储空间参数
     */
    @Override
    public Object createBucket(OssCommonParam param) {
        return ossClientOptProcess.createBucket(param);
    }

    /**
     * 判断存储空间是否存在
     *
     * @param param oss操作常用参数
     * @return 结果
     */
    @Override
    public Boolean doesBucketExist(OssCommonParam param) {
        return ossClientOptProcess.doesBucketExist(param);
    }

    /**
     * 删除存储空间
     *
     * @param param oss操作常用参数
     */
    @Override
    public void deleteBucket(OssCommonParam param) {
        ossClientOptProcess.deleteBucket(param);
    }

    /**
     * 创建oss客户端，基本使用的情况下无需手动创建客户端 --简单创建
     *
     * @param config oss地址配置信息
     * @return 客户端
     */
    @Override
    public <T> DewOssHandleClient<T> buildOssClient(OssConfigProperties config) {
        return ossClientOptProcess.buildOssClient(config);
    }

    /**
     * 关闭oss客户端，基本使用的情况下无需手动关闭客户端
     */
    @Override
    public void closeClient() {
        ossClientOptProcess.closeClient();
    }

    /**
     * 文件上传
     * 使用minio的时候上传除字符对象外的影像文件需传头部信息 content-type：例如：文件拓展名为avi,对应的content-type是video/mp4
     *
     * @param param oss操作常用参数
     */
    @Override
    public void uploadObject(OssCommonParam param) {
        ossClientOptProcess.uploadObject(param);
    }

    /**
     * 文件上传 -- 流式上传
     * 使用minio的时候上传除字符对象外的影像文件需传头部信息 content-type：例如：文件拓展名为avi,对应的content-type是video/mp4
     *
     * @param param       oss操作常用参数
     * @param inputStream 文件流
     */
    @Override
    public void uploadObject(OssCommonParam param, InputStream inputStream) {
        ossClientOptProcess.uploadObject(param,inputStream);
    }

    /**
     * 下载文件,获取文件流自行处理
     *
     * @param param oss操作常用参数
     * @return 文件流
     */
    @Override
    public InputStream downloadFile(OssCommonParam param) {
        return ossClientOptProcess.downloadFile(param);
    }

    /**
     * 下载文件到本地
     *
     * @param param oss操作常用参数
     */
    @Override
    public void downloadFileLocal(OssCommonParam param) {
        ossClientOptProcess.downloadFileLocal(param);
    }

    /**
     * 判断文件是否存在
     *
     * @param param oss操作常用参数
     * @return 结果
     */
    @Override
    public Boolean doesObjectExist(OssCommonParam param) {
        return ossClientOptProcess.doesObjectExist(param);
    }

    /**
     * 删除文件
     *
     * @param param oss操作常用参数
     */
    @Override
    public void deleteObject(OssCommonParam param) {
        ossClientOptProcess.deleteObject(param);
    }

    /**
     * 获取签名上传url --临时授权
     *
     * @param param oss操作常用参数
     * @return url
     */
    @Override
    public String temporaryUploadUrl(OssCommonParam param) {
        return ossClientOptProcess.temporaryUploadUrl(param);
    }

    /**
     * 获取签名删除URL --临时授权
     *
     * @param param oss操作常用参数
     * @return url
     */
    @Override
    public String temporaryDeleteUrl(OssCommonParam param) {
        return ossClientOptProcess.temporaryDeleteUrl(param);
    }

    /**
     * 获取签名查询访问URL --临时授权
     *
     * @param param oss操作常用参数
     * @return url
     */
    @Override
    public String temporaryUrl(OssCommonParam param) {
        return ossClientOptProcess.temporaryUrl(param);
    }

    /**
     * 获取前端缩率图url
     *
     * @param param   oss操作常用参数
     * @param process 图片处理参数
     * @return 临时url
     */
    @Override
    public String imageProcess(OssCommonParam param, ImageProcessParam process) {
        return ossClientOptProcess.imageProcess(param,process);
    }

    /**
     * 创建oss客户端,基本使用的情况下无需手动创建客户端，个性化配置
     *
     * @param properties oss地址配置信息
     * @param config     拓展额外配置信息,个性化配置
     * @return 客户端
     */
    @Override
    public <T> DewOssHandleClient<T> buildOssClient(OssConfigProperties properties, ClientBuilderConfiguration config) {
        return ossClientOptProcess.buildOssClient(properties,config);
    }

    /**
     * 简单创建存储空间,个性化需求，createBucketRequest属于oss包中的类
     *
     * @param createBucketRequest createBucketRequest
     */
    @Override
    public void createBucket(CreateBucketRequest createBucketRequest) {
        ossClientOptProcess.createBucket(createBucketRequest);
    }

    /**
     * 使用签名URL上传文件 --临时授权
     *
     * @param param       oss操作常用参数
     * @param inputStream 文件输入流
     */
    @Override
    public void temporaryUploadFile(OssCommonParam param, FileInputStream inputStream) {
        ossClientOptProcess.temporaryUploadFile(param,inputStream);
    }

    /**
     * 创建oss客户端,基本使用的情况下无需手动创建客户端,增加个性化配置
     *
     * @param properties oss地址配置信息
     * @param config     拓展额外配置信息,个性化配置
     * @return 客户端
     */
    @Override
    public <T> DewOssHandleClient<T> buildOssClient(OssConfigProperties properties, ObsConfiguration config) {
        return ossClientOptProcess.buildOssClient(properties,config);
    }




}
