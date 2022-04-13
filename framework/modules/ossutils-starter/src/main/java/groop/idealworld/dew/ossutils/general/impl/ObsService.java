package groop.idealworld.dew.ossutils.general.impl;

import com.obs.services.ObsClient;
import com.obs.services.ObsConfiguration;
import com.obs.services.exception.ObsException;
import com.obs.services.model.*;
import groop.idealworld.dew.ossutils.Utils.OssClientUtil;
import groop.idealworld.dew.ossutils.bean.ImageProcessParam;
import groop.idealworld.dew.ossutils.bean.OssCommonParam;
import groop.idealworld.dew.ossutils.general.ObsSpecialExecutor;
import groop.idealworld.dew.ossutils.handle.DewOssHandleClient;
import groop.idealworld.dew.ossutils.config.OssConfigProperties;
import groop.idealworld.dew.ossutils.handle.OssHandleTool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * @author yiye
 * @date 2022/4/1
 * @description
 **/
@Service("obs")
public class ObsService implements ObsSpecialExecutor {
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
        ObsClient obsClient = isNull(param);
        try {
            // 创建桶成功
            ObsBucket bucket = obsClient.createBucket(param.getBucketName());
        } catch (ObsException e) {
            logger.error("creat bucket fail,response for some reason：HTTP Code:{},Error Code:{},Error Message:{},Request ID:{},Host ID:{}"
                    , e.getResponseCode(), e.getErrorCode(), e.getErrorMessage(), e.getErrorRequestId(), e.getErrorHostId());
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
        ObsClient obsClient = isNull(param);
        try {
            return obsClient.headBucket(param.getBucketName());
        } catch (ObsException e) {
            logger.error("creat bucket fail,response for some reason：HTTP Code:{},Error Code:{},Error Message:{},Request ID:{},Host ID:{}"
                    , e.getResponseCode(), e.getErrorCode(), e.getErrorMessage(), e.getErrorRequestId(), e.getErrorHostId());

        }
        return false;

    }

    /**
     * 删除存储空间
     *
     * @param param oss操作常用参数
     */
    @Override
    public void deleteBucket(OssCommonParam param) {
        ObsClient obsClient = isNull(param);
        try {
            obsClient.deleteBucket(param.getBucketName());
        } catch (ObsException e) {
            logger.error("creat bucket fail,response for some reason：HTTP Code:{},Error Code:{},Error Message:{},Request ID:{},Host ID:{}"
                    , e.getResponseCode(), e.getErrorCode(), e.getErrorMessage(), e.getErrorRequestId(), e.getErrorHostId());

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
        Optional.ofNullable(config).orElseThrow(() -> new NullPointerException("参数不能为空"));
        ObsClient obsClient = (ObsClient) OssClientUtil.getOssClient();
        if (ObjectUtils.isEmpty(obsClient)) {
            if (StringUtils.hasText(config.getSecurityToken())) {
                obsClient = new ObsClient(config.getAccessKeyId(), config.getAccessKeyIdSecret(), config.getEndpoint());
            } else {
                obsClient = new ObsClient(config.getAccessKeyId(), config.getAccessKeyIdSecret(), config.getSecurityToken(), config.getEndpoint());
            }
            OssClientUtil.setOssClient(obsClient);
        }
        DewOssHandleClient ossHandleClient = new DewOssHandleClient<>();
        ossHandleClient.setOssClient(obsClient);
        return ossHandleClient;
    }

    /**
     * 创建oss客户端,基本使用的情况下无需手动创建客户端
     *
     * @param properties               oss地址配置信息
     * @param config 拓展额外配置信息
     * @return 客户端
     */
    @Override
    public <T> DewOssHandleClient<T> buildOssClient(OssConfigProperties properties, ObsConfiguration config) {
        Optional.ofNullable(properties).orElseThrow(() -> new NullPointerException("参数不能为空"));
        ObsClient obsClient = (ObsClient) OssClientUtil.getOssClient();
        if (ObjectUtils.isEmpty(obsClient) && ObjectUtils.isEmpty(config)) {
            this.buildOssClient(properties);
        }else if (ObjectUtils.isEmpty(obsClient) && !ObjectUtils.isEmpty(config)) {
            if (StringUtils.hasText(properties.getSecurityToken())) {
                config.setEndPoint(properties.getEndpoint());
                obsClient = new ObsClient(properties.getAccessKeyId(), properties.getAccessKeyIdSecret(),config);
            } else {
                obsClient = new ObsClient(properties.getAccessKeyId(), properties.getAccessKeyIdSecret(), properties.getSecurityToken(), config);
            }
            OssClientUtil.setOssClient(obsClient);
        }
        DewOssHandleClient ossHandleClient = new DewOssHandleClient<>();
        ossHandleClient.setOssClient(obsClient);
        return ossHandleClient;
    }

    /**
     * 关闭oss客户端，基本使用的情况下无需手动关闭客户端
     */
    @Override
    public void closeClient() {
        ObsClient obsClient = (ObsClient) OssClientUtil.getOssClient();
        if (obsClient != null) {
            try {
                obsClient.close();
            }catch (IOException e){
                logger.error(e.getMessage());
            }

            OssClientUtil.removeOssClient();
        }
    }


    /**
     * 文件上传
     * 使用minio的时候上传除字符对象外的影像文件需传头部信息 content-type：例如：文件拓展名为avi,对应的content-type是video/mp4
     * @param param       oss操作常用参数
     */
    @Override
    public void uploadObject(OssCommonParam param) {
        ObsClient obsClient = isNull(param);
        try {
            obsClient.putObject(param.getBucketName(), param.getObjectName(), new File(param.getPath()));
        }catch (ObsException e){
            logger.error("creat bucket fail,response for some reason：HTTP Code:{},Error Code:{},Error Message:{},Request ID:{},Host ID:{}"
                    , e.getResponseCode(), e.getErrorCode(), e.getErrorMessage(), e.getErrorRequestId(), e.getErrorHostId());

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
        ObsClient obsClient = isNull(param);
        try {
            obsClient.putObject(param.getBucketName(), param.getObjectName(),inputStream);
        }catch (ObsException e){
            logger.error("creat bucket fail,response for some reason：HTTP Code:{},Error Code:{},Error Message:{},Request ID:{},Host ID:{}"
                    , e.getResponseCode(), e.getErrorCode(), e.getErrorMessage(), e.getErrorRequestId(), e.getErrorHostId());

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
        ObsClient obsClient = isNull(param);
        try {
            ObsObject obsObject = obsClient.getObject(param.getBucketName(), param.getObjectName());
            return obsObject.getObjectContent();
        }catch (ObsException e){
            logger.error("creat bucket fail,response for some reason：HTTP Code:{},Error Code:{},Error Message:{},Request ID:{},Host ID:{}"
                    , e.getResponseCode(), e.getErrorCode(), e.getErrorMessage(), e.getErrorRequestId(), e.getErrorHostId());

        }
        return null;
    }

    /**
     * 下载文件到本地
     *
     * @param param oss操作常用参数
     */
    @Override
    public void downloadFileLocal(OssCommonParam param) {
        ObsClient obsClient = isNull(param);
        try {
            ObsObject obsObject = obsClient.getObject(param.getBucketName(), param.getObjectName());
            InputStream inputStream = obsObject.getObjectContent();
            File file = new File(param.getPath());
            if (file.exists()) {
                file.createNewFile();
            }
            OutputStream os = new FileOutputStream(file);
            int read = 0;
            byte[] bytes = new byte[1024 * 1024];
            //先读后写
            while ((read = inputStream.read(bytes)) > 0){
                byte[] wBytes = new byte[read];
                System.arraycopy(bytes, 0, wBytes, 0, read);
                os.write(wBytes);
            }
            os.flush();
            os.close();
            inputStream.close();
        }catch (ObsException e){
            logger.error("creat bucket fail,response for some reason：HTTP Code:{},Error Code:{},Error Message:{},Request ID:{},Host ID:{}"
                    , e.getResponseCode(), e.getErrorCode(), e.getErrorMessage(), e.getErrorRequestId(), e.getErrorHostId());

        }catch (IOException e){
            logger.error(e.getMessage());
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
        ObsClient obsClient = isNull(param);
        try {
           return obsClient.doesObjectExist(param.getBucketName(), param.getObjectName());
        }catch (ObsException e){
            logger.error("creat bucket fail,response for some reason：HTTP Code:{},Error Code:{},Error Message:{},Request ID:{},Host ID:{}"
                    , e.getResponseCode(), e.getErrorCode(), e.getErrorMessage(), e.getErrorRequestId(), e.getErrorHostId());

        }
        return false;
    }

    /**
     * 删除文件
     *
     * @param param oss操作常用参数
     */
    @Override
    public void deleteObject(OssCommonParam param) {
        ObsClient obsClient = isNull(param);
        try {
            obsClient.deleteObject(param.getBucketName(), param.getObjectName());
        }catch (ObsException e){
            logger.error("creat bucket fail,response for some reason：HTTP Code:{},Error Code:{},Error Message:{},Request ID:{},Host ID:{}"
                    , e.getResponseCode(), e.getErrorCode(), e.getErrorMessage(), e.getErrorRequestId(), e.getErrorHostId());

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
        ObsClient obsClient = isExpirationNull(param);
        try{

            TemporarySignatureRequest request = new TemporarySignatureRequest(HttpMethodEnum.PUT,param.getExpiration()/1000);
            request.setBucketName(param.getBucketName());
            request.setObjectKey(param.getObjectName());
            request.setHeaders(param.getCustomHeaders());
            TemporarySignatureResponse response = obsClient.createTemporarySignature(request);
            return response.getSignedUrl();
        }catch (ObsException e){
            logger.error("creat bucket fail,response for some reason：HTTP Code:{},Error Code:{},Error Message:{},Request ID:{},Host ID:{}"
                    , e.getResponseCode(), e.getErrorCode(), e.getErrorMessage(), e.getErrorRequestId(), e.getErrorHostId());

        }
        return null;
    }

    /**
     * 获取签名删除URL --临时授权
     *
     * @param param oss操作常用参数
     * @return url
     */
    @Override
    public String temporaryDeleteUrl(OssCommonParam param) {
        ObsClient obsClient = isExpirationNull(param);
        try{

            TemporarySignatureRequest request = new TemporarySignatureRequest(HttpMethodEnum.DELETE,param.getExpiration()/1000);
            request.setBucketName(param.getBucketName());
            request.setObjectKey(param.getObjectName());
            request.setHeaders(param.getCustomHeaders());
            TemporarySignatureResponse response = obsClient.createTemporarySignature(request);
            return response.getSignedUrl();
        }catch (ObsException e){
            logger.error("creat bucket fail,response for some reason：HTTP Code:{},Error Code:{},Error Message:{},Request ID:{},Host ID:{}"
                    , e.getResponseCode(), e.getErrorCode(), e.getErrorMessage(), e.getErrorRequestId(), e.getErrorHostId());
            throw e ;

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
        ObsClient obsClient = isExpirationNull(param);
        try{

            TemporarySignatureRequest request = new TemporarySignatureRequest(HttpMethodEnum.GET,param.getExpiration()/1000);
            request.setBucketName(param.getBucketName());
            request.setObjectKey(param.getObjectName());
            request.setHeaders(param.getCustomHeaders());
            TemporarySignatureResponse response = obsClient.createTemporarySignature(request);
            return response.getSignedUrl();
        }catch (ObsException e){
            logger.error("creat bucket fail,response for some reason：HTTP Code:{},Error Code:{},Error Message:{},Request ID:{},Host ID:{}"
                    , e.getResponseCode(), e.getErrorCode(), e.getErrorMessage(), e.getErrorRequestId(), e.getErrorHostId());
            throw e;

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
        try {
            ObsClient obsClient = isExpirationNull(param);
            TemporarySignatureRequest request = new TemporarySignatureRequest(HttpMethodEnum.GET, param.getExpiration() / 1000);
            request.setBucketName(param.getBucketName());
            request.setObjectKey(param.getObjectName());
            Map<String, Object> queryParams = new HashMap<>();
            queryParams.put("x-image-process", OssHandleTool.imageProcess(process));
            request.setQueryParams(queryParams);
            TemporarySignatureResponse response = obsClient.createTemporarySignature(request);
            return response.getSignedUrl();
        }catch (ObsException e){
            logger.error("creat bucket fail,response for some reason：HTTP Code:{},Error Code:{},Error Message:{},Request ID:{},Host ID:{}"
                    , e.getResponseCode(), e.getErrorCode(), e.getErrorMessage(), e.getErrorRequestId(), e.getErrorHostId());
            throw e;
        }catch (Exception e){
            throw new RuntimeException("服务内部异常:"+e.getMessage());
        }
    }

    private ObsClient isNull(OssCommonParam param) {
        if (param == null) {
            throw new IllegalArgumentException("param必要参数不能为空");
        }
        if (!StringUtils.hasLength(param.getBucketName()) || !StringUtils.hasLength(param.getObjectName())){
            throw new IllegalArgumentException("操作对象存储服务器必要参数不能为空");
        }
        return creatClient();
    }

    private ObsClient isExpirationNull(OssCommonParam param) {
        isNull(param);
        if (param.getExpiration() == null || param.getExpiration() <= 0) {
            throw new IllegalArgumentException("expiration不能为空");
        }
        return creatClient();
    }

    private ObsClient creatClient(){
        Object object = OssClientUtil.getOssClient();
        if (object == null) {
            return (ObsClient) this.buildOssClient(ossConfigProperties).getOssClient();
        }
        return (ObsClient) object;
    }
}
