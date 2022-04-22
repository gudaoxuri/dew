/*
 * Copyright 2022. the original author or authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package group.idealworld.dew.example.oss;

import com.ecfront.dew.common.Resp;
import groop.idealworld.dew.ossutils.Utils.OssClientUtil;
import groop.idealworld.dew.ossutils.bean.ImageProcessParam;
import groop.idealworld.dew.ossutils.bean.OssCommonParam;
import groop.idealworld.dew.ossutils.config.OssConfigProperties;
import groop.idealworld.dew.ossutils.general.DewOssClient;
import groop.idealworld.dew.ossutils.handle.DewOssHandleClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import com.alibaba.fastjson.JSONObject;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;


import java.io.*;
import java.nio.charset.Charset;
import java.util.Base64;
import java.util.Collections;

/**
 * Auth example controller.
 *
 * @author gudaoxuri
 */
@RestController
@RequestMapping("/oss")

public class OssExampleController {

    private static final Logger logger = LoggerFactory.getLogger(OssExampleController.class);

    @Resource
    private OssConfigProperties ossConfigProperties;

    @Resource
    private DewOssClient dewOssClient ;

    /**
     * 创建客户端
     * @return the resp
     */
    @GetMapping(value = "/build")
    public Resp<String> buildOssClient() {
        DewOssHandleClient<Object> ossClient= dewOssClient.buildOssClient(ossConfigProperties);
        return Resp.success(ossClient.getOssClient().getClass().getName());
    }

    /**
     * 创建桶
     * @return the resp
     */
    @GetMapping(value = "/createBucket")
    public Resp<String> createBucket(HttpServletRequest request, @RequestBody OssCommonParam ossCommonParam) {
        dewOssClient.createBucket(ossCommonParam);
        return Resp.success("创建成功");
    }

    /**
     * 查询doesBucketExist
     * @return the resp
     */
    @GetMapping(value = "/doesBucketExist")
    public Resp<Boolean> doesBucketExist(HttpServletRequest request, @RequestBody OssCommonParam ossCommonParam) {
        boolean flag = dewOssClient.doesBucketExist(ossCommonParam);
        return Resp.success(flag);
    }

    /**
     * 流式上传文件
     * @return the resp
     */
    @PutMapping(value = "/uploadObject")
    public Resp<String> uploadObject(HttpServletRequest request, @RequestBody OssCommonParam ossCommonParam) {
        try {
            dewOssClient.uploadObject(ossCommonParam,new FileInputStream("/Users/yiye/projectSpace/other/dew/examples/oss-example/file/iShot2021-06-18 12.21.34.png"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return Resp.success("上传成功");
    }

    /**
     * 查询doesObjectExist
     * @return the resp
     */
    @GetMapping(value = "/doesObjectExist")
    public Resp<Boolean> doesObjectExist(HttpServletRequest request, @RequestBody OssCommonParam ossCommonParam) {
        boolean flag = dewOssClient.doesObjectExist(ossCommonParam);
        return Resp.success(flag);
    }

    /**
     * 删除文件
     * @return the resp
     */
    @GetMapping(value = "/deleteObject")
    public Resp<String> deleteObject(HttpServletRequest request, @RequestBody OssCommonParam ossCommonParam) {
        dewOssClient.deleteObject(ossCommonParam);
        return Resp.success("删除成功");
    }

    /**
     * 获取文件缩略图
     *
     * @return the resp
     */
    @GetMapping(value = "/imageProcess")
    public Resp<String> imageProcess(HttpServletRequest request, @RequestBody OssCommonParam ossCommonParam) {
        ImageProcessParam imageProcessParam = new ImageProcessParam();
        imageProcessParam.setHeight(500);
        imageProcessParam.setWidth(500);
        return Resp.success(dewOssClient.imageProcess(ossCommonParam,imageProcessParam));
    }

    /**
     * 临时删除url
     * @return the resp
     */
    @GetMapping(value = "/temporaryDeleteUrl")
    public Resp<String> temporaryDeleteUrl(HttpServletRequest request, @RequestBody OssCommonParam ossCommonParam) {
        ImageProcessParam imageProcessParam = new ImageProcessParam();
        imageProcessParam.setHeight(500);
        imageProcessParam.setWidth(500);
        return Resp.success(dewOssClient.temporaryDeleteUrl(ossCommonParam));
    }

    /**
     * 临时上传url
     * @return the resp
     */
    @GetMapping(value = "/temporaryUploadUrl")
    public Resp<String> temporaryUploadUrl(HttpServletRequest request, @RequestBody OssCommonParam ossCommonParam) {
        return Resp.success(dewOssClient.temporaryUploadUrl(ossCommonParam));
    }

    /**
     * 临时下载查看url
     * @return the resp
     */
    @GetMapping(value = "/temporaryUrl")
    public Resp<String> temporaryUrl(HttpServletRequest request, @RequestBody OssCommonParam ossCommonParam) {
        return Resp.success(dewOssClient.temporaryUrl(ossCommonParam));
    }

    /**
     * 删除bucket
     * @return the resp
     */
    @GetMapping(value = "/deleteBucket")
    public Resp<String> deleteBucket(HttpServletRequest request, @RequestBody OssCommonParam ossCommonParam) {
        dewOssClient.deleteBucket(ossCommonParam);
        return Resp.success("删除成功");
    }

    /**
     * 上传
     * @return the resp
     */
    @PostMapping(value = "/upload")
    public Resp<String> upload(HttpServletRequest request, @RequestBody OssCommonParam ossCommonParam) {
        dewOssClient.uploadObject(ossCommonParam);
        return Resp.success("上传成功");
    }

    /**
     * 下载
     * @return the resp
     */
    @GetMapping(value = "/download")
    public Resp<String> download(HttpServletRequest request, @RequestBody OssCommonParam ossCommonParam) {
        dewOssClient.downloadFileLocal(ossCommonParam);
        return Resp.success("下载成功");
    }

    /**
     * 查询文件
     * @return the resp
     */
    @GetMapping(value = "/doesExit")
    public Resp<Boolean> doesExit(HttpServletRequest request, @RequestBody OssCommonParam ossCommonParam) {
        return Resp.success(dewOssClient.doesObjectExist(ossCommonParam));
    }

    /**
     * 下载文件流
     * @return the resp
     */
    @GetMapping(value = "/inputstream")
    public Resp<String> downloadFile(HttpServletRequest request, @RequestBody OssCommonParam ossCommonParam) {
        InputStream inputStream = dewOssClient.downloadFile(ossCommonParam);
        try {
            int aa = inputStream.read();
            logger.info(aa+"");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Resp.success("下载成功");
    }

    /**
     * 关闭客户端
     * @return the resp
     */
    @GetMapping(value = "/shutdownOssClient")
    public Resp<String> shutdownOssClient(HttpServletRequest request, @RequestBody OssCommonParam ossCommonParam) {
        DewOssHandleClient ossHandleClient = dewOssClient.buildOssClient(ossConfigProperties);
        dewOssClient.closeClient();
        Object p = OssClientUtil.getOssClient();
        logger.info(getClass().getName());
        return Resp.success("下载成功");
    }

    /**
     * 关闭客户端
     * @return the resp
     */
    @GetMapping(value = "/test")
    public Resp<?> test(HttpServletRequest request, String url,String path) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        HttpEntity<String> httpEntityCount = new HttpEntity<>(new HttpHeaders() {{
            set(AUTHORIZATION, "Basic NjAzOGQwZDFjNDVkZTFhZDFjNGJjODgwYThjZDA0MmY3YjViYzg1Mzo=");
        }});
        String responseEntity = restTemplate.exchange(url+path, HttpMethod.GET, httpEntityCount, String.class).getBody();
        JSONObject jsonObject = JSONObject.parseObject(responseEntity);
        return Resp.success(jsonObject);
    }


}
