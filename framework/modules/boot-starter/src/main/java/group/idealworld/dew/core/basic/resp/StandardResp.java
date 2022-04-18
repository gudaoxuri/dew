/*
 * Copyright 2020. the original author or authors.
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

package group.idealworld.dew.core.basic.resp;

import com.ecfront.dew.common.$;
import com.ecfront.dew.common.Resp;
import com.ecfront.dew.common.StandardCode;
import com.ecfront.dew.common.exception.RTException;
import group.idealworld.dew.Dew;
import group.idealworld.dew.core.web.error.ErrorController;
import group.idealworld.dew.core.basic.utils.TraceIdUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Standard.
 *
 * @author gudaoxuri
 */
public class StandardResp {

    private static final Logger logger = LoggerFactory.getLogger(StandardResp.class);
    /**
     * Success.
     *
     * @param <E>  the type parameter
     * @param body the body
     * @return the resp
     */
    public static <E> Resp<E> success(E body) {
        return Resp.success(body);
    }

    /**
     * Error.
     *
     * @param <E>  the type parameter
     * @param resp the resp
     * @return the resp
     */
    public static <E> Resp<E> error(Resp<?> resp) {
        return new Resp<>(resp.getCode(), resp.getMessage(), null);
    }

    /**
     * 统一异常处理.
     * <p>
     * 返回Http状态码为200
     *
     * @param resp 统一返回对象
     * @return 上抛的异常对象
     */
    public static RTException e(Resp<?> resp) {
        var ex = new RTException(resp.getMessage());
        $.bean.setValue(ex, "detailMessage", $.json.createObjectNode()
                .put("code", resp.getCode())
                .put("message", resp.getMessage())
                .put("customHttpCode", 200)
                .toString());
        return ex;
    }

    /**
     * Custom.
     *
     * @param <E>          the type parameter
     * @param code         the code
     * @param businessFlag the business flag
     * @param content      the content
     * @param args         the args
     * @return the resp
     */
    public static <E> Resp<E> custom(String code, String businessFlag, String content, Object... args) {
        return packageResp(code, businessFlag, String.format(content, args));
    }

    /**
     * Not found resource.
     *
     * @param <E>          the type parameter
     * @param businessFlag the business flag
     * @param resource     the resource
     * @return the resp
     */
    public static <E> Resp<E> notFoundResource(String businessFlag, String resource) {
        return packageResp(StandardCode.NOT_FOUND.toString(),businessFlag, "找不到[" + resource + "],请检查权限");
    }

    /**
     * Not found.
     *
     * @param <E>          the type parameter
     * @param businessFlag the business flag
     * @param content      the content
     * @param args         the args
     * @return the resp
     */
    public static <E> Resp<E> notFound(String businessFlag, String content, Object... args) {
        return packageResp(StandardCode.NOT_FOUND.toString(),businessFlag, String.format(content, args));
    }

    /**
     * Bad request.
     *
     * @param <E>          the type parameter
     * @param businessFlag the business flag
     * @param content      the content
     * @param args         the args
     * @return the resp
     */
    public static <E> Resp<E> badRequest(String businessFlag, String content, Object... args) {
        return packageResp(StandardCode.BAD_REQUEST.toString(),businessFlag, String.format(content, args));
    }

    /**
     * Un authorized operate.
     *
     * @param <E>          the type parameter
     * @param businessFlag the business flag
     * @param operate      the operate
     * @return the resp
     */
    public static <E> Resp<E> unAuthorizedOperate(String businessFlag, String operate) {
        return packageResp(StandardCode.UNAUTHORIZED.toString(),businessFlag, "操作[" + operate + "]没有权限");
    }

    /**
     * Un authorized resource.
     *
     * @param <E>          the type parameter
     * @param businessFlag the business flag
     * @param resource     the resource
     * @return the resp
     */
    public static <E> Resp<E> unAuthorizedResource(String businessFlag, String resource) {
        return packageResp(StandardCode.UNAUTHORIZED.toString(),businessFlag, "资源[" + resource + "]没有权限");
    }

    /**
     * Un authorized.
     *
     * @param <E>          the type parameter
     * @param businessFlag the business flag
     * @param content      the content
     * @param args         the args
     * @return the resp
     */
    public static <E> Resp<E> unAuthorized(String businessFlag, String content, Object... args) {
        return packageResp(StandardCode.UNAUTHORIZED.toString(), businessFlag, String.format(content, args));
    }

    /**
     * Conflict.
     *
     * @param <E>          the type parameter
     * @param businessFlag the business flag
     * @param content      the content
     * @param args         the args
     * @return the resp
     */
    public static <E> Resp<E> conflict(String businessFlag, String content, Object... args) {
        return packageResp(StandardCode.CONFLICT.toString(), businessFlag, String.format(content, args));
    }

    /**
     * Locked resource.
     *
     * @param <E>          the type parameter
     * @param businessFlag the business flag
     * @param resource     the resource
     * @return the resp
     */
    public static <E> Resp<E> lockedResource(String businessFlag, String resource) {
        return packageResp(StandardCode.LOCKED.toString(),businessFlag, "资源[" + resource + "]被锁定");
    }

    /**
     * Locked.
     *
     * @param <E>          the type parameter
     * @param businessFlag the business flag
     * @param content      the content
     * @param args         the args
     * @return the resp
     */
    public static <E> Resp<E> locked(String businessFlag, String content, Object... args) {
        return packageResp(StandardCode.LOCKED.toString(), businessFlag, String.format(content, args));
    }

    /**
     * Unsupported media type.
     *
     * @param <E>          the type parameter
     * @param businessFlag the business flag
     * @param request      the request
     * @return the resp
     */
    public static <E> Resp<E> unsupportedMediaType(String businessFlag, String request) {
        return packageResp(StandardCode.UNSUPPORTED_MEDIA_TYPE.toString(),businessFlag, "请求[" + request + "]类型不支持");
    }

    /**
     * Unsupported media type.
     *
     * @param <E>          the type parameter
     * @param businessFlag the business flag
     * @param content      the content
     * @param args         the args
     * @return the resp
     */
    public static <E> Resp<E> unsupportedMediaType(String businessFlag, String content, Object... args) {
        return packageResp(StandardCode.UNSUPPORTED_MEDIA_TYPE.toString(), businessFlag, String.format(content, args));
    }

    /**
     * Server error.
     *
     * @param <E>          the type parameter
     * @param businessFlag the business flag
     * @param e            the e
     * @return the resp
     */
    public static <E> Resp<E> serverError(String businessFlag, Throwable e) {
        return packageResp(StandardCode.INTERNAL_SERVER_ERROR.toString(), businessFlag, "服务错误:" + e.getMessage());
    }

    /**
     * Server error.
     *
     * @param <E>          the type parameter
     * @param businessFlag the business flag
     * @param content      the content
     * @param args         the args
     * @return the resp
     */
    public static <E> Resp<E> serverError(String businessFlag, String content, Object... args) {
        return packageResp(StandardCode.INTERNAL_SERVER_ERROR.toString(), businessFlag, String.format(content, args));
    }

    /**
     * Not implemented method.
     *
     * @param <E>          the type parameter
     * @param businessFlag the business flag
     * @param method       the method
     * @return the resp
     */
    public static <E> Resp<E> notImplementedMethod(String businessFlag, String method) {
        return packageResp(StandardCode.NOT_IMPLEMENTED.toString(), businessFlag, "方法[" + method + "]未实现");
    }

    /**
     * Not implemented.
     *
     * @param <E>          the type parameter
     * @param businessFlag the business flag
     * @param content      the content
     * @param args         the args
     * @return the resp
     */
    public static <E> Resp<E> notImplemented(String businessFlag, String content, Object... args) {
        return packageResp(StandardCode.NOT_IMPLEMENTED.toString(), businessFlag, String.format(content, args));
    }

    /**
     * Server unavailable.
     *
     * @param <E>          the type parameter
     * @param businessFlag the business flag
     * @return the resp
     */
    public static <E> Resp<E> serverUnavailable(String businessFlag) {
        return packageResp(StandardCode.SERVICE_UNAVAILABLE.toString(), businessFlag, "服务不可用，请稍后再试");
    }

    /**
     * Server unavailable.
     *
     * @param <E>          the type parameter
     * @param businessFlag the business flag
     * @param content      the content
     * @param args         the args
     * @return the resp
     */
    public static <E> Resp<E> serverUnavailable(String businessFlag, String content, Object... args) {
        return packageResp(StandardCode.SERVICE_UNAVAILABLE.toString(), businessFlag, String.format(content, args));
    }

    private static <E> Resp<E> packageResp(String statusCode, String businessFlag, String content) {
        String code =TraceIdUtil.createResponseCode(statusCode,businessFlag);
        logger.trace("RESP:[{}] {}", code, content);
        return Resp.custom(code, content);
    }

}
