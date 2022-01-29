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

package group.idealworld.dew.core.web.error;

import com.ecfront.dew.common.$;
import com.ecfront.dew.common.Resp;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import group.idealworld.dew.Dew;
import group.idealworld.dew.core.DewConfig;
import io.swagger.v3.oas.annotations.Hidden;
import org.apache.catalina.connector.RequestFacade;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.web.servlet.error.AbstractErrorController;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.ObjectUtils;
import org.springframework.validation.beanvalidation.MethodValidationPostProcessor;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.ServletRequestWrapper;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.ConstraintViolationException;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Error controller.
 *
 * @author gudaoxuri
 */
@RestController
@Hidden
@ConditionalOnWebApplication
@RequestMapping(value = "${server.error.path:${error.path:/error}}")
public class ErrorController extends AbstractErrorController {

    private static final Logger logger = LoggerFactory.getLogger(ErrorController.class);

    private static final int FALL_BACK_STATUS = 500;

    // TODO 可能有风险
    private static final Pattern MESSAGE_CHECK = Pattern.compile("^\\{\"code\":\".*?\",\"message\":\".*?\",\"customHttpCode\":.*?\\}$");

    private static final String SPECIAL_ERROR_FLAG = "org.springframework.boot.web.servlet.error.DefaultErrorAttributes.ERROR";

    private static final String DETAIL_FLAG = "\tDetail:";

    /**
     * Instantiates a new Error controller.
     *
     * @param errorAttributes the error attributes
     */
    public ErrorController(ErrorAttributes errorAttributes) {
        super(errorAttributes);
    }

    /**
     * Error object.
     *
     * @param request the request
     * @return the object
     */
    @RequestMapping()
    @ResponseBody
    public Object error(HttpServletRequest request) {
        Object specialError = request.getAttribute(SPECIAL_ERROR_FLAG);
        if (specialError instanceof Resp.FallbackException) {
            return ResponseEntity
                    .status(FALL_BACK_STATUS)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(((Resp.FallbackException) specialError).getMessage());
        }
        Map<String, Object> error = getErrorAttributes(request,
                ErrorAttributeOptions.of(ErrorAttributeOptions.Include.STACK_TRACE,
                        ErrorAttributeOptions.Include.BINDING_ERRORS,
                        ErrorAttributeOptions.Include.EXCEPTION,
                        ErrorAttributeOptions.Include.MESSAGE));
        String path;
        String exClass = "";
        if (error.containsKey("path")) {
            path = (String) error.getOrDefault("path", Dew.context().getRequestUri());
        } else {
            path = ((RequestFacade) ((ServletRequestWrapper) request).getRequest()).getRequestURI();
        }
        int httpCode = (int) error.getOrDefault("status", -1);
        String message = error.getOrDefault("message", "").toString();
        String exMsg = (String) error.getOrDefault("error", "");
        List exDetail = null;
        if (error.containsKey("errors") && !((List) error.get("errors")).isEmpty()) {
            exDetail = (List) error.get("errors");
        }
        if (specialError == null) {
            specialError = new Exception(message);
        } else {
            exClass = specialError.getClass().getName();
        }
        Object[] result = error(request, path, httpCode, message, exClass, exMsg, exDetail, (Throwable) specialError);
        httpCode = (int) result[0];
        if (httpCode > 499) {
            // 服务错误才通知
            Dew.notify.sendAsync(Dew.dewConfig.getBasic().getFormat().getErrorFlag(),
                    (Throwable) specialError, ((Throwable) specialError).getMessage());
        }
        return ResponseEntity.status(httpCode).contentType(MediaType.APPLICATION_JSON).body(result[1]);
    }

    private static Object[] error(HttpServletRequest request,
                                  String path, int httpCode, String msg, String exClass, String exMsg,
                                  List exDetail, Throwable specialError) {
        String message = msg;
        String busCode = String.valueOf(httpCode);
        int customHttpCode = -1;
        if (!ObjectUtils.isEmpty(exClass) && Dew.dewConfig.getBasic().getErrorMapping().containsKey(exClass)) {
            // Found Error Mapping
            DewConfig.Basic.ErrorMapping errorMapping = Dew.dewConfig.getBasic().getErrorMapping().get(exClass);
            if (!ObjectUtils.isEmpty(errorMapping.getHttpCode())) {
                customHttpCode = errorMapping.getHttpCode();
            }
            if (!ObjectUtils.isEmpty(errorMapping.getBusinessCode())) {
                busCode = errorMapping.getBusinessCode();
            }
            if (!ObjectUtils.isEmpty(errorMapping.getMessage())) {
                message = errorMapping.getMessage();
            }
        }
        if (MESSAGE_CHECK.matcher(message).matches()) {
            JsonNode detail = $.json.toJson(message);
            busCode = detail.get("code").asText();
            message = detail.get("message").asText();
            if (detail.has("customHttpCode") && detail.get("customHttpCode").asInt() != -1) {
                // 使用自定义http状态码
                customHttpCode = detail.get("customHttpCode").asInt();
            }
        }
        if (specialError instanceof ConstraintViolationException) {
            ArrayNode errorExt = $.json.createArrayNode();
            ((ConstraintViolationException) specialError).getConstraintViolations()
                    .forEach(cv ->
                            errorExt.add($.json.createObjectNode()
                                    .put("field", "")
                                    .put("reason", cv.getConstraintDescriptor()
                                            .getAnnotation().annotationType().getSimpleName())
                                    .put("msg", cv.getMessage()))
                    );
            message += DETAIL_FLAG + $.json.toJsonString(errorExt);
        }
        if (specialError instanceof MethodArgumentNotValidException && exDetail != null && !exDetail.isEmpty()) {
            ArrayNode errorExt = $.json.createArrayNode();
            for (JsonNode json : $.json.toJson(exDetail)) {
                errorExt.add($.json.createObjectNode()
                        .put("field", json.get("field").asText(""))
                        .put("reason", json.get("codes").get(0).asText().split("\\.")[0])
                        .put("msg", json.get("defaultMessage").asText("")));
            }
            message += DETAIL_FLAG + $.json.toJsonString(errorExt);
        }
        if (specialError instanceof ConstraintViolationException) {
            busCode = "400";
            httpCode = 400;
        }
        if (customHttpCode != -1) {
            httpCode = customHttpCode;
        } else if (httpCode >= 500 && httpCode < 600) {
            httpCode = FALL_BACK_STATUS;
        } else {
            httpCode = 200;
        }
        logger.error("Request [{}-{}] {} , error {} : {}", request.getMethod(), path, Dew.context().getSourceIP(), busCode, message);
        var resp = Resp.customFail(busCode + "", "[" + exMsg + "]" + message);
        String body = $.json.toJsonString(resp);
        return new Object[]{httpCode, body};
    }

    /**
     * Error.
     *
     * @param request    the request
     * @param response   the response
     * @param statusCode the status code
     * @param message    the message
     * @param exClass    the ex class
     * @throws IOException the io exception
     */
    public static void error(HttpServletRequest request, HttpServletResponse response,
                             int statusCode, String message, String exClass)
            throws IOException {
        Object[] confirmedError = error(request, request.getRequestURI(), statusCode, message, exClass, "", null, null);
        response.setStatus((Integer) confirmedError[0]);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.getWriter().write((String) confirmedError[1]);
        response.getWriter().flush();
        response.getWriter().close();
    }

    /**
     * Method validation post processor method validation post processor.
     *
     * @return the method validation post processor
     */
    @Bean
    public MethodValidationPostProcessor methodValidationPostProcessor() {
        return new MethodValidationPostProcessor();
    }

}
