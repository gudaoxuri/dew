package groop.idealworld.dew.ossutils.general.impl;

import com.aliyun.oss.*;
import com.aliyun.oss.common.utils.HttpHeaders;
import com.aliyun.oss.model.*;
import com.ecfront.dew.common.$;
import com.fasterxml.jackson.databind.JsonNode;
import com.obs.services.ObsConfiguration;
import groop.idealworld.dew.ossutils.utils.OssClientUtil;
import groop.idealworld.dew.ossutils.bean.ImageProcessParam;
import groop.idealworld.dew.ossutils.bean.OssCommonParam;
import groop.idealworld.dew.ossutils.general.OssClientInitProcess;
import groop.idealworld.dew.ossutils.general.OssClientOptProcess;
import groop.idealworld.dew.ossutils.handle.DewOssHandleClient;
import groop.idealworld.dew.ossutils.config.OssConfigProperties;
import groop.idealworld.dew.ossutils.utils.OssHandleTool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.io.*;
import java.net.URL;
import java.util.Date;
import java.util.Map;
import java.util.Optional;

/**
 * @author yiye
 **/
@Service("oss")
public class OssService implements OssClientOptProcess, OssClientInitProcess {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    /**
     * oss 客户端最大连接数限制
     */
    private final Integer MAX_THREAD_NUM = 500;

    private OssConfigProperties ossConfigProperties;

    /**
     * 创建存储空间，简单创建
     *
     * @param param oss存储空间参数
     */
    @Override
    public Bucket createBucket(OssCommonParam param) {
        OSS ossClient = isNull(param);
        // 创建CreateBucketRequest对象。
        try {
            // 创建存储空间。Ï
            return ossClient.createBucket(param.getBucketName());
        } catch (OSSException oe) {
            logger.error("Caught an OSSException, which means your request made it to OSS," +
                    "but was rejected with an error response for some reason." +
                    "Error Message:{},Error Code:{},Request ID:{},Host ID:{}" +
                    oe.getErrorMessage(), oe.getErrorCode(), oe.getRequestId(), oe.getHostId());
            throw oe;
        } catch (ClientException ce) {
            logger.error("Caught an ClientException, which means the client encountered "
                    + "a serious internal problem while trying to communicate with OSS, "
                    + "such as not being able to access the network.Error Message:" + ce.getMessage());
            throw ce;
        } finally {
            if (ossClient != null) {
                closeClient();
            }
        }
    }

    /**
     * 判断存储空间是否存在
     *
     * @param param oss操作常用参数
     * @return 结果
     */
    @Override
    public Boolean doesBucketExist(OssCommonParam param) {
        OSS ossClient = isNull(param);
        try {
            return ossClient.doesBucketExist(param.getBucketName());
        } catch (OSSException oe) {
            logger.error("Caught an OSSException, which means your request made it to OSS, "
                    + "but was rejected with an error response for some reason." +
                    "Error Message:{}," +
                    "Error Code:{}," +
                    "Request ID:{}," +
                    "Host ID:{}", oe.getErrorMessage(), oe.getErrorCode(), oe.getRequestId(), oe.getHostId());
        } catch (ClientException ce) {
            logger.error("Caught an ClientException, which means the client encountered "
                    + "a serious internal problem while trying to communicate with OSS, "
                    + "such as not being able to access the network.Error Message:{}", ce.getMessage());
        } finally {
            if (ossClient != null) {
                closeClient();
            }
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
        OSS ossClient = isNull(param);
        try {
            ossClient.deleteBucket(param.getBucketName());
        } catch (OSSException oe) {
            logger.error("Caught an OSSException, which means your request made it to OSS, "
                    + "but was rejected with an error response for some reason." +
                    "Error Message:{}," +
                    "Error Code:{}," +
                    "Request ID:{}," +
                    "Host ID:{}", oe.getErrorMessage(), oe.getErrorCode(), oe.getRequestId(), oe.getHostId());
            throw oe;
        } catch (ClientException ce) {
            logger.error("Caught an ClientException, which means the client encountered "
                    + "a serious internal problem while trying to communicate with OSS, "
                    + "such as not being able to access the network.Error Message:{}", ce.getMessage());
            throw ce;
        } finally {
            if (ossClient != null) {
                closeClient();
            }
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
        DewOssHandleClient ossHandleClient = new DewOssHandleClient<>();
        OSS ossClient = (OSS) OssClientUtil.getOssClient();
        if (ObjectUtils.isEmpty(ossClient)) {
            if (StringUtils.hasText(config.getSecurityToken())) {
                ossClient = new OSSClientBuilder().build(config.getEndpoint(), config.getAccessKeyId(), config.getAccessKeyIdSecret());
            } else {
                ossClient = new OSSClientBuilder().build(config.getEndpoint(), config.getAccessKeyId(), config.getAccessKeyIdSecret(), config.getSecurityToken());
            }
            OssClientUtil.setOssClient(ossClient);
        }

        ossHandleClient.setOssClient(ossClient);
        return ossHandleClient;
    }

    /**
     * 创建oss客户端
     *
     * @param properties oss地址配置信息
     * @param config     拓展额外配置信息
     * @return 客户端
     */
    @Override
    public <T> DewOssHandleClient<T> buildOssClient(OssConfigProperties properties, ClientBuilderConfiguration config) {
        Optional.ofNullable(properties).orElseThrow(() -> new NullPointerException("参数不能为空"));
        OSS ossClient = (OSS) OssClientUtil.getOssClient();
        if (ObjectUtils.isEmpty(ossClient) && ObjectUtils.isEmpty(config)) {
            return this.buildOssClient(properties);
        } else if (ObjectUtils.isEmpty(ossClient) && !ObjectUtils.isEmpty(config)) {
            if (StringUtils.hasText(properties.getSecurityToken())) {
                ossClient = new OSSClientBuilder().build(properties.getEndpoint(), properties.getAccessKeyId(), properties.getAccessKeyIdSecret(), config);
            } else {
                ossClient = new OSSClientBuilder().build(properties.getEndpoint(),
                        properties.getAccessKeyId(), properties.getAccessKeyIdSecret(), properties.getSecurityToken(), config);
            }
            OssClientUtil.setOssClient(ossClient);
        }
        DewOssHandleClient ossHandleClient = new DewOssHandleClient<>();
        ossHandleClient.setOssClient(ossClient);
        return ossHandleClient;
    }

    /**
     * 关闭oss客户端
     */
    @Override
    public void closeClient() {
        OSS ossClient = (OSS) OssClientUtil.getOssClient();
        if (ossClient != null) {
            String stats = ossClient.getConnectionPoolStats();
            stats = stats.replaceAll("\\[", "{").replaceAll("]", "}").replaceAll(";", ",");
            JsonNode jsonNode = $.json.toJson(stats);
            logger.info("ossClient连接池状态：{}", stats);
            if (jsonNode.get("available").asInt() > MAX_THREAD_NUM) {
                logger.info("ossClient连接池中有{}个空闲连接，不关闭", jsonNode.get("available").asInt());
                ossClient.shutdown();
                OssClientUtil.removeOssClient();
            }
        }
    }

    /**
     * 文件上传
     *
     * @param param oss操作常用参数
     */
    @Override
    public void uploadObject(OssCommonParam param) {
        OSS ossClient = isNull(param);
        try {
            //上传文件
            ossClient.putObject(param.getBucketName(), param.getObjectName(), new File(param.getPath()));
        } catch (OSSException oe) {
            logger.error("Caught an OSSException, which means your request made it to OSS, "
                    + "but was rejected with an error response for some reason." +
                    "Error Message:{}," +
                    "Error Code:{}," +
                    "Request ID:{}," +
                    "Host ID:{}", oe.getErrorMessage(), oe.getErrorCode(), oe.getRequestId(), oe.getHostId());
            throw oe;
        } catch (ClientException ce) {
            logger.error("Caught an ClientException, which means the client encountered "
                    + "a serious internal problem while trying to communicate with OSS, "
                    + "such as not being able to access the network.Error Message:{}", ce.getMessage());
            throw ce;
        } finally {
            if (ossClient != null) {
                closeClient();
            }
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
        OSS ossClient = isNull(param);
        try {
            //上传文件
            ossClient.putObject(param.getBucketName(), param.getObjectName(), inputStream);
        } catch (OSSException oe) {
            logger.error("Caught an OSSException, which means your request made it to OSS, "
                    + "but was rejected with an error response for some reason." +
                    "Error Message:{}," +
                    "Error Code:{}," +
                    "Request ID:{}," +
                    "Host ID:{}", oe.getErrorMessage(), oe.getErrorCode(), oe.getRequestId(), oe.getHostId());
            throw oe;
        } catch (ClientException ce) {
            logger.error("Caught an ClientException, which means the client encountered "
                    + "a serious internal problem while trying to communicate with OSS, "
                    + "such as not being able to access the network.Error Message:{}", ce.getMessage());
            throw ce;
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
        OSS ossClient = isNull(param);
        try {
            // 调用ossClient.getObject返回一个OSSObject实例，该实例包含文件内容及文件元信息。
            OSSObject ossObject = ossClient.getObject(param.getBucketName(), param.getObjectName());
            // 调用ossObject.getObjectContent获取文件输入流，可读取此输入流获取其内容。
            return ossObject.getObjectContent();
        } catch (OSSException oe) {
            logger.error("Caught an OSSException, which means your request made it to OSS, "
                    + "but was rejected with an error response for some reason." +
                    "Error Message:{}," +
                    "Error Code:{}," +
                    "Request ID:{}," +
                    "Host ID:{}", oe.getErrorMessage(), oe.getErrorCode(), oe.getRequestId(), oe.getHostId());
            throw oe;
        } catch (ClientException ce) {
            logger.error("Caught an ClientException, which means the client encountered "
                    + "a serious internal problem while trying to communicate with OSS, "
                    + "such as not being able to access the network.Error Message:{}", ce.getMessage());
            throw ce;
        } catch (Exception e) {
            logger.error("处理文件异常{}===>{}", e.getMessage(), e);
            throw e;
        } finally {
            if (ossClient != null) {
                closeClient();
            }
        }
    }

    /**
     * 下载文件到本地
     *
     * @param param oss操作常用参数
     */
    @Override
    public void downloadFileLocal(OssCommonParam param) {
        OSS ossClient = isNull(param);
        try {
            // 下载Object到本地文件，并保存到指定的本地路径中。如果指定的本地文件存在会覆盖，不存在则新建。
            // 如果未指定本地路径，则下载后的文件默认保存到示例程序所属项目对应本地路径中。
            GetObjectRequest getObjectRequest = new GetObjectRequest(param.getBucketName(), param.getObjectName());
            File file = new File(param.getPath());
            if (!file.getParentFile().exists() && !file.isDirectory()) {
                file.getParentFile().mkdirs();
                file.createNewFile();
            } else if (!file.exists()) {
                file.createNewFile();
            }
            ossClient.getObject(getObjectRequest, file);
        } catch (OSSException oe) {
            logger.error("Caught an OSSException, which means your request made it to OSS, "
                    + "but was rejected with an error response for some reason." +
                    "Error Message:{}," +
                    "Error Code:{}," +
                    "Request ID:{}," +
                    "Host ID:{}", oe.getErrorMessage(), oe.getErrorCode(), oe.getRequestId(), oe.getHostId());
            throw oe;
        } catch (ClientException ce) {
            logger.error("Caught an ClientException, which means the client encountered "
                    + "a serious internal problem while trying to communicate with OSS, "
                    + "such as not being able to access the network.Error Message: {}" + ce.getMessage());
            throw ce;
        } catch (Exception e) {
            logger.error("内部处理异常：{}", e.getMessage());
            throw new RuntimeException(e.getMessage());
        } finally {
            if (ossClient != null) {
                closeClient();
            }
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
        OSS ossClient = isNull(param);
        try {
            // 判断文件是否存在。如果返回值为true，则文件存在，否则存储空间或者文件不存在。
            // 设置是否进行重定向或者镜像回源。默认值为true，表示忽略302重定向和镜像回源；如果设置isINoss为false，则进行302重定向或者镜像回源。
            // boolean isINoss = true;
            // boolean found = ossClient.doesObjectExist(bucketName, objectName, isINoss);
            return ossClient.doesObjectExist(param.getBucketName(), param.getObjectName());
        } catch (OSSException oe) {
            logger.error("Caught an OSSException, which means your request made it to OSS, "
                    + "but was rejected with an error response for some reason." +
                    "Error Message:{}," +
                    "Error Code:{}," +
                    "Request ID:{}," +
                    "Host ID:{}", oe.getErrorMessage(), oe.getErrorCode(), oe.getRequestId(), oe.getHostId());
        } catch (ClientException ce) {
            logger.error("Caught an ClientException, which means the client encountered "
                    + "a serious internal problem while trying to communicate with OSS, "
                    + "such as not being able to access the network.Error Message: {}" + ce.getMessage());
        } finally {
            if (ossClient != null) {
                closeClient();
            }
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
        OSS ossClient = isNull(param);
        try {
            // 删除文件或目录。如果要删除目录，目录必须为空。
            ossClient.deleteObject(param.getBucketName(), param.getObjectName());
        } catch (OSSException oe) {
            logger.error("Caught an OSSException, which means your request made it to OSS, "
                    + "but was rejected with an error response for some reason." +
                    "Error Message:{}," +
                    "Error Code:{}," +
                    "Request ID:{}," +
                    "Host ID:{}", oe.getErrorMessage(), oe.getErrorCode(), oe.getRequestId(), oe.getHostId());
            throw oe;
        } catch (ClientException ce) {
            logger.error("Caught an ClientException, which means the client encountered "
                    + "a serious internal problem while trying to communicate with OSS, "
                    + "such as not being able to access the network.Error Message: {}" + ce.getMessage());
            throw ce;
        } finally {
            if (ossClient != null) {
                closeClient();
            }
        }
    }

    /**
     * 使用签名URL上传文件 --临时授权
     *
     * @param param oss操作常用参数
     */
    @Override
    public void temporaryUploadFile(OssCommonParam param, FileInputStream inputStream) {
        OSS ossClient = isExpirationNull(param);
        try {
            // 生成签名URL。
            GeneratePresignedUrlRequest request = new GeneratePresignedUrlRequest(param.getBucketName(), param.getObjectName(), HttpMethod.PUT);
            Map<String, String> custom = buildRequest(param, request);
            // 通过HTTP PUT请求生成签名URL。
            URL signedUrl = ossClient.generatePresignedUrl(request);
            logger.info("signed url for putObject: {}", signedUrl);
            FileInputStream fin = null;
            if (StringUtils.hasText(param.getPath())) {
                // 使用签名URL发送请求。
                File f = new File(param.getPath());
                fin = new FileInputStream(f);
            } else {
                fin = inputStream;
            }

            PutObjectResult result = ossClient.putObject(signedUrl, fin, -1, custom);
        } catch (OSSException oe) {
            logger.error("Caught an OSSException, which means your request made it to OSS, "
                    + "but was rejected with an error response for some reason." +
                    "Error Message:{}," +
                    "Error Code:{}," +
                    "Request ID:{}," +
                    "Host ID:{}", oe.getErrorMessage(), oe.getErrorCode(), oe.getRequestId(), oe.getHostId());
            throw oe;
        } catch (ClientException ce) {
            logger.error("Caught an ClientException, which means the client encountered "
                    + "a serious internal problem while trying to communicate with OSS, "
                    + "such as not being able to access the network.Error Message: {}" + ce.getMessage());
            throw ce;
        } catch (FileNotFoundException e) {
            logger.error("file not find");
        } finally {
            if (ossClient != null) {
                closeClient();
            }
        }


    }

    @Override
    public <T> DewOssHandleClient<T> buildOssClient(OssConfigProperties properties, ObsConfiguration config) {
        throw new UnsupportedOperationException("not support,please use buildOssClient(OssCommonParam param)");
    }

    /**
     * 获取签名上传url --临时授权
     *
     * @param param oss操作常用参数
     * @return url
     */
    @Override
    public String temporaryUploadUrl(OssCommonParam param) {
        OSS ossClient = isExpirationNull(param);

        try {
            // 生成签名URL。
            GeneratePresignedUrlRequest request = new GeneratePresignedUrlRequest(param.getBucketName(), param.getObjectName(), HttpMethod.PUT);
            Map<String, String> custom = buildRequest(param, request);
            URL url = ossClient.generatePresignedUrl(request);
            // 通过HTTP PUT请求生成签名URL。
            return url.toString();
        } catch (OSSException oe) {
            logger.error("Caught an OSSException, which means your request made it to OSS, "
                    + "but was rejected with an error response for some reason." +
                    "Error Message:{}," +
                    "Error Code:{}," +
                    "Request ID:{}," +
                    "Host ID:{}", oe.getErrorMessage(), oe.getErrorCode(), oe.getRequestId(), oe.getHostId());
            throw oe;
        } catch (ClientException ce) {
            logger.error("Caught an ClientException, which means the client encountered "
                    + "a serious internal problem while trying to communicate with OSS, "
                    + "such as not being able to access the network.Error Message: {}" + ce.getMessage());
            throw ce;
        } finally {
            if (ossClient != null) {
                closeClient();
            }
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
        OSS ossClient = isExpirationNull(param);

        try {
            // 生成签名URL。
            GeneratePresignedUrlRequest request = new GeneratePresignedUrlRequest(param.getBucketName(), param.getObjectName(), HttpMethod.DELETE);
            Map<String, String> custom = buildRequest(param, request);
            URL url = ossClient.generatePresignedUrl(request);
            // 通过HTTP PUT请求生成签名URL。
            return url.toString();
        } catch (OSSException oe) {
            logger.error("Caught an OSSException, which means your request made it to OSS, "
                    + "but was rejected with an error response for some reason." +
                    "Error Message:{}," +
                    "Error Code:{}," +
                    "Request ID:{}," +
                    "Host ID:{}", oe.getErrorMessage(), oe.getErrorCode(), oe.getRequestId(), oe.getHostId());
            throw oe;
        } catch (ClientException ce) {
            logger.error("Caught an ClientException, which means the client encountered "
                    + "a serious internal problem while trying to communicate with OSS, "
                    + "such as not being able to access the network.Error Message: {}" + ce.getMessage());
            throw ce;
        } finally {
            if (ossClient != null) {
                closeClient();
            }
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
        OSS ossClient = isExpirationNull(param);

        try {
            long expiration = System.currentTimeMillis() + param.getExpiration();
            // 通过HTTP GET请求生成签名URL。
            URL url = ossClient.generatePresignedUrl(param.getBucketName(), param.getObjectName(), new Date(expiration));
            return url.toString();
        } catch (OSSException oe) {
            logger.error("Caught an OSSException, which means your request made it to OSS, "
                    + "but was rejected with an error response for some reason." +
                    "Error Message:{}," +
                    "Error Code:{}," +
                    "Request ID:{}," +
                    "Host ID:{}", oe.getErrorMessage(), oe.getErrorCode(), oe.getRequestId(), oe.getHostId());
            throw oe;
        } catch (ClientException ce) {
            logger.error("Caught an ClientException, which means the client encountered "
                    + "a serious internal problem while trying to communicate with OSS, "
                    + "such as not being able to access the network.Error Message: {}" + ce.getMessage());
            throw ce;
        } finally {
            if (ossClient != null) {
                closeClient();
            }
        }
    }

    /**
     * 简单创建存储空间,个性化需求，createBucketRequest属于oss包中的类
     *
     * @param createBucketRequest createBucketRequest
     */
    @Override
    public void createBucket(CreateBucketRequest createBucketRequest) {
        OSS ossClient = Optional.ofNullable((OSS) OssClientUtil.getOssClient())
                .orElse((OSS) this.buildOssClient(ossConfigProperties).getOssClient());
        // 创建CreateBucketRequest对象。
        try {
            // 创建存储空间。
            ossClient.createBucket(createBucketRequest);
        } catch (OSSException oe) {
            logger.error("Caught an OSSException, which means your request made it to OSS," +
                    "but was rejected with an error response for some reason." +
                    "Error Message:{},Error Code:{},Request ID:{},Host ID:{}" +
                    oe.getErrorMessage(), oe.getErrorCode(), oe.getRequestId(), oe.getHostId());
            throw oe;
        } catch (ClientException ce) {
            logger.error("Caught an ClientException, which means the client encountered "
                    + "a serious internal problem while trying to communicate with OSS, "
                    + "such as not being able to access the network.Error Message:" + ce.getMessage());
            throw ce;
        } finally {
            if (ossClient != null) {
                closeClient();
            }
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
        OSS ossClient = isExpirationNull(param);
        try {
            String style = OssHandleTool.imageProcess(process);
            GeneratePresignedUrlRequest request = new GeneratePresignedUrlRequest(param.getBucketName(), param.getObjectName(), HttpMethod.GET);
            request.setExpiration(new Date(System.currentTimeMillis() + param.getExpiration()));
            request.setProcess(style);
            URL url = ossClient.generatePresignedUrl(request);
            return url.toString();
        } catch (OSSException oe) {
            logger.error("Caught an OSSException, which means your request made it to OSS," +
                    "but was rejected with an error response for some reason." +
                    "Error Message:{},Error Code:{},Request ID:{},Host ID:{}" +
                    oe.getErrorMessage(), oe.getErrorCode(), oe.getRequestId(), oe.getHostId());
            throw oe;
        } catch (ClientException ce) {
            logger.error("Caught an ClientException, which means the client encountered "
                    + "a serious internal problem while trying to communicate with OSS, "
                    + "such as not being able to access the network.Error Message:" + ce.getMessage());
            throw ce;
        } catch (Exception e) {
            logger.error("服务内部异常:{}", e.getMessage());
            throw e;
        } finally {
            if (ossClient != null) {
                closeClient();
            }
        }
    }

    private OSS isNull(OssCommonParam param) {
        if (param == null) {
            throw new IllegalArgumentException("param必要参数不能为空");
        }
        if (!StringUtils.hasLength(param.getBucketName()) || !StringUtils.hasLength(param.getObjectName())) {
            throw new IllegalArgumentException("操作对象存储服务器必要参数不能为空");
        }
        return creatClient();
    }

    private OSS isExpirationNull(OssCommonParam param) {
        isNull(param);
        if (param.getExpiration() == null || param.getExpiration() <= 0) {
            throw new IllegalArgumentException("expiration不能为空");
        }
        return creatClient();
    }

    private OSS creatClient() {
        Object object = OssClientUtil.getOssClient();
        if (object == null) {
            return (OSS) this.buildOssClient(ossConfigProperties).getOssClient();
        }
        return (OSS) object;
    }

    private Map<String, String> buildRequest(OssCommonParam param, GeneratePresignedUrlRequest request) {
        request.setExpiration(new Date((System.currentTimeMillis() + param.getExpiration())));
        Map<String, String> custom = param.getCustomHeaders();
        if (custom != null) {
            for (Map.Entry<String, String> entry : custom.entrySet()) {
                if (HttpHeaders.CONTENT_TYPE.equals(entry.getKey())) {
                    request.setContentType(entry.getValue());
                } else if (HttpHeaders.CONTENT_MD5.equals(entry.getKey())) {
                    request.setContentMD5(entry.getValue());
                } else {
                    request.addUserMetadata(entry.getKey(), entry.getValue());
                }
            }
        }
        return custom;
    }

    /**
     * 初始化原始客户端
     *
     * @param config oss配置
     * @return 是否初始化成功
     */
    @Override
    public boolean initClient(OssConfigProperties config) {
        ossConfigProperties = config;
        return false;
    }
}
