package groop.idealworld.dew.ossutils.general.impl;

import groop.idealworld.dew.ossutils.Utils.OssClientUtil;
import groop.idealworld.dew.ossutils.bean.ImageProcessParam;
import groop.idealworld.dew.ossutils.bean.OssCommonParam;
import groop.idealworld.dew.ossutils.config.OssConfigProperties;
import groop.idealworld.dew.ossutils.general.MinioSpecialExecutor;
import groop.idealworld.dew.ossutils.handle.DewOssHandleClient;
import io.minio.*;
import io.minio.http.Method;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.io.InputStream;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * @author yiye
 * @date 2022/4/1
 * @description
 **/
@Service("minio")
public class MinioService implements MinioSpecialExecutor {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Resource
    OssConfigProperties ossConfigProperties;

    /**
     * 创建存储空间，简单创建
     *
     * @param param oss存储空间参数
     */
    @Override
    public void createBucket(OssCommonParam param) {
        MinioClient minioClient =isNull(param);
        try {
            boolean found = minioClient.bucketExists(BucketExistsArgs.builder()
                    .bucket(param.getBucketName())
                    .build());
            if (found){
                logger.info("mybucket already exists,bucketName:{}",param.getBucketName());
            }else {
                minioClient.makeBucket(MakeBucketArgs.builder().bucket(param.getBucketName()).build());
                logger.info("bucket is created successfully,bucketName : {}",param.getBucketName());
            }
        } catch (Exception e){
            throw new RuntimeException("minio操作异常",e);
        }
    }

    /**
     * 判断存储空间是否存在
     *
     * @param param oss操作常用参数
     * @return 结果
     */
    @Override
    public Boolean doesBucketExist(OssCommonParam param){
        MinioClient minioClient =isNull(param);
        try {
            return minioClient.bucketExists(BucketExistsArgs.builder()
                    .bucket(param.getBucketName())
                    .build());
        }  catch (Exception e){
            throw new RuntimeException("minio操作异常",e);
        }
    }

    /**
     * 删除存储空间
     *
     * @param param oss操作常用参数
     */
    @Override
    public void deleteBucket(OssCommonParam param) {
        MinioClient minioClient =isNull(param);
        try {
            boolean found = minioClient.bucketExists(BucketExistsArgs.builder()
                    .bucket(param.getBucketName())
                    .build());
            if (found){
                logger.info("mybucket already exists,bucketName:{}",param.getBucketName());
            }else {
                minioClient.removeBucket(RemoveBucketArgs.builder()
                        .bucket(param.getBucketName())
                        .build());
                logger.info("bucket is remove successfully,bucketName : {}",param.getBucketName());
            }
        } catch (Exception e){
            throw new RuntimeException("minio操作异常",e);
        }
    }

    /**
     * 创建oss客户端，基本使用的情况下无需手动创建客户端 --简单创建
     *
     * @param config oss地址配置信息
     * @return 客户端
     */
    @Override
    public <T> DewOssHandleClient<T> buildOssClient(OssConfigProperties config) {
        DewOssHandleClient ossHandleClient = new DewOssHandleClient<>();
        try {
            Optional.ofNullable(config).orElseThrow(() -> new NullPointerException("参数不能为空"));
            MinioClient minioClient = (MinioClient) OssClientUtil.getOssClient();
            if (ObjectUtils.isEmpty(minioClient)) {
                minioClient = MinioClient.builder()
                        .endpoint(config.getEndpoint())
                        .credentials(config.getAccessKeyId(), config.getAccessKeyIdSecret())
                        .build();
                ossHandleClient.setOssClient(minioClient);
                OssClientUtil.setOssClient(minioClient);
            } else {
                ossHandleClient.setOssClient(minioClient);
            }
        } catch (Exception e){
            logger.error("捕获的异常：{}",e.getMessage());
            throw e;
        }
        return ossHandleClient;
    }

    /**
     * 关闭oss客户端，基本使用的情况下无需手动关闭客户端
     */
    @Override
    public void closeClient() {
        MinioClient minioClient =(MinioClient)OssClientUtil.getOssClient();
        if(!ObjectUtils.isEmpty(minioClient)){
            OssClientUtil.removeOssClient();
        }
    }

    /**
     * 文件上传
     *
     * @param param oss操作常用参数
     */
    @Override
    public void uploadObject(OssCommonParam param) {
        MinioClient minioClient = isNull(param);
        try {
            minioClient.uploadObject(UploadObjectArgs.builder()
                    .bucket(param.getBucketName())
                    .object(param.getObjectName())
                    .filename(param.getPath()).build());
        } catch (Exception e){
            throw new RuntimeException("minio操作异常",e);
        }
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
        MinioClient minioClient = isNull(param);
        try {
            minioClient.putObject(PutObjectArgs.builder()
                    .bucket(param.getBucketName())
                    .object(param.getObjectName())
                    .stream(inputStream,-1,1024*1024*10)
                    .build());
        } catch (Exception e){
            throw new RuntimeException("minio操作异常",e);
        }
    }

    /**
     * 下载文件
     *
     * @param param oss操作常用参数
     * @return 文件流
     */
    @Override
    public InputStream downloadFile(OssCommonParam param) {
        MinioClient minioClient = isNull(param);
        try {
            return minioClient.getObject(GetObjectArgs.builder()
                    .bucket(param.getBucketName())
                    .object(param.getObjectName())
                    .build());
        } catch (Exception e){
            throw new RuntimeException("minio操作异常",e);
        }
    }

    /**
     * 下载文件到本地
     *
     * @param param oss操作常用参数
     */
    @Override
    public void downloadFileLocal(OssCommonParam param) {
        MinioClient minioClient = isNull(param);
        try {
            minioClient.downloadObject(DownloadObjectArgs.builder()
                    .bucket(param.getBucketName())
                    .object(param.getObjectName())
                    .filename(param.getPath())
                    .build());
        } catch (Exception e){
            logger.info("下载文件异常：{}",e.getMessage());
            throw new RuntimeException("minio操作异常",e);
        }
    }

    /**
     * 判断文件是否存在
     *
     * @param param oss操作常用参数
     * @return 结果
     */
    @Override
    public Boolean doesObjectExist(OssCommonParam param) {
        MinioClient minioClient = isNull(param);
        try {
            GetObjectResponse response = minioClient.getObject(GetObjectArgs.builder()
                    .bucket(param.getBucketName())
                    .object(param.getObjectName())
                    .build());
            return StringUtils.hasLength(response.object());
        }catch (Exception e){
            throw new RuntimeException("minio操作异常",e);
        }
    }

    /**
     * 删除文件
     *
     * @param param oss操作常用参数
     */
    @Override
    public void deleteObject(OssCommonParam param) {
        MinioClient minioClient = isNull(param);
        try {
            minioClient.removeObject(RemoveObjectArgs.builder()
                    .bucket(param.getBucketName())
                    .object(param.getObjectName())
                    .build());
        } catch (Exception e){
            throw new RuntimeException("minio操作异常",e);
        }

    }

    /**
     * 获取签名上传url --临时授权
     *
     * @param param oss操作常用参数
     * @return url
     */
    @Override
    public String temporaryUploadUrl(OssCommonParam param) {
        MinioClient minioClient = isExpirationNull(param);
        try {
            return minioClient.getPresignedObjectUrl(GetPresignedObjectUrlArgs.builder()
                    .method(Method.PUT)
                    .bucket(param.getBucketName())
                    .object(param.getObjectName())
                    .expiry(Integer.parseInt(param.getExpiration().toString()), TimeUnit.MILLISECONDS)
                    .build());
        } catch (Exception e){
            throw new RuntimeException("minio操作异常",e);
        }
    }

    /**
     * 获取签名删除URL --临时授权
     *
     * @param param oss操作常用参数
     * @return url
     */
    @Override
    public String temporaryDeleteUrl(OssCommonParam param) {
        MinioClient minioClient = isExpirationNull(param);
        try {
            return minioClient.getPresignedObjectUrl(GetPresignedObjectUrlArgs.builder()
                    .method(Method.DELETE)
                    .bucket(param.getBucketName())
                    .object(param.getObjectName())
                    .expiry(Integer.parseInt(param.getExpiration().toString()), TimeUnit.MILLISECONDS)
                    .build());
        } catch (Exception e){
            throw new RuntimeException("minio操作异常",e);
        }
    }

    /**
     * 获取签名查询访问URL --临时授权
     *
     * @param param oss操作常用参数
     * @return url
     */
    @Override
    public String temporaryUrl(OssCommonParam param) {
        MinioClient minioClient = isExpirationNull(param);
        try {
            return minioClient.getPresignedObjectUrl(GetPresignedObjectUrlArgs.builder()
                    .method(Method.GET)
                    .bucket(param.getBucketName())
                    .object(param.getObjectName())
                    .expiry(Integer.parseInt(param.getExpiration().toString()), TimeUnit.MILLISECONDS)
                    .build());
        } catch (Exception e){
            throw new RuntimeException("minio操作异常",e);
        }
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
        return null;
    }

    private MinioClient isNull(OssCommonParam param) {
        if (param == null) {
            throw new IllegalArgumentException("param必要参数不能为空");
        }
        if (!StringUtils.hasLength(param.getBucketName()) || !StringUtils.hasLength(param.getObjectName())){
            throw new IllegalArgumentException("操作对象存储服务器必要参数不能为空");
        }
        return creatClient();
    }

    private MinioClient isExpirationNull(OssCommonParam param) {
        isNull(param);
        if (param.getExpiration() == null || param.getExpiration() <= 0) {
            throw new IllegalArgumentException("expiration不能为空");
        }
        return creatClient();
    }

    private MinioClient creatClient(){
        Object object = OssClientUtil.getOssClient();
        if (object == null) {
            return (MinioClient) this.buildOssClient(ossConfigProperties).getOssClient();
        }
        return (MinioClient) object;
    }
}
