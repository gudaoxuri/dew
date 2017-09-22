package com.ecfront.dew.core.controller;

import com.ecfront.dew.common.$;
import com.ecfront.dew.common.Resp;
import com.ecfront.dew.core.Dew;
import com.ecfront.dew.core.DewConfig;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import org.hibernate.validator.internal.metadata.descriptor.ConstraintDescriptorImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.web.servlet.error.AbstractErrorController;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.context.annotation.Bean;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.validation.beanvalidation.MethodValidationPostProcessor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolationException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

@RestController
@RequestMapping("${error.path:/error}")
public class ErrorController extends AbstractErrorController {

    private static final Logger logger = LoggerFactory.getLogger(ErrorController.class);

    private static final Pattern MESSAGE_CHECK = Pattern.compile("^\\{\"code\":\"\\w*\",\"message\":\".*\",\"customHttpCode\":.*}$");

    private static final String SPECIAL_ERROR_FLAG = "org.springframework.boot.autoconfigure.web.DefaultErrorAttributes.ERROR";
    private static final String DETAIL_FLAG = " Detail:";

    @Value("${error.path:/error}")
    private String errorPath;

    public ErrorController(ErrorAttributes errorAttributes) {
        super(errorAttributes);
    }

    @Bean
    public MethodValidationPostProcessor methodValidationPostProcessor() {
        return new MethodValidationPostProcessor();
    }

    @Override
    public String getErrorPath() {
        return errorPath;
    }


    @RequestMapping()
    @ResponseBody
    public Object error(HttpServletRequest request) {
        Map<String, Object> error = getErrorAttributes(request, false);
        String requestFrom = request.getHeader(Dew.Constant.HTTP_REQUEST_FROM_FLAG);
        String path = (String) error.getOrDefault("path", Dew.context().getRequestUri());
        String busCode = (int) error.getOrDefault("status", -1) + "";
        int httpCode = (int) error.getOrDefault("status", -1);
        String err = (String) error.getOrDefault("error", "");
        String message = error.getOrDefault("message", "").toString();
        String exception = (String) error.getOrDefault("exception", "");
        if (!StringUtils.isEmpty(exception) && Dew.dewConfig.getBasic().getErrorMapping().containsKey(exception)) {
            // Found Error Mapping
            DewConfig.Basic.ErrorMapping errorMapping = Dew.dewConfig.getBasic().getErrorMapping().get(exception);
            if (!StringUtils.isEmpty(errorMapping.getHttpCode())) {
                httpCode = errorMapping.getHttpCode();
            }
            if (!StringUtils.isEmpty(errorMapping.getBusinessCode())) {
                busCode = errorMapping.getBusinessCode();
            }
            if (!StringUtils.isEmpty(errorMapping.getMessage())) {
                message = errorMapping.getMessage();
            }
        }
        if (MESSAGE_CHECK.matcher(message).matches()) {
            JsonNode detail = $.json.toJson(message);
            busCode = detail.get("code").asText();
            message = detail.get("message").asText();
            if (detail.has("customHttpCode") && detail.get("customHttpCode").asInt() != -1) {
                // 使用自定义http状态码
                httpCode = detail.get("customHttpCode").asInt();
            }
        }
        Object specialError = request.getAttribute(SPECIAL_ERROR_FLAG);
        if (specialError != null) {
            if (specialError instanceof ConstraintViolationException) {
                ArrayNode errorExt = $.json.createArrayNode();
                ((ConstraintViolationException) specialError).getConstraintViolations()
                        .forEach(cv ->
                                errorExt.add($.json.createObjectNode()
                                        .put("field", "")
                                        .put("reason", ((ConstraintDescriptorImpl<?>) cv.getConstraintDescriptor())
                                                .getAnnotationType().getSimpleName())
                                        .put("msg", cv.getMessage()))
                        );
                message += DETAIL_FLAG + $.json.toJsonString(errorExt);
            } else {
                if (error.containsKey("errors") && !((List) error.get("errors")).isEmpty()) {
                    ArrayNode errorExt = $.json.createArrayNode();
                    Iterator<JsonNode> errorExtIt = $.json.toJson(error.get("errors")).iterator();
                    while (errorExtIt.hasNext()) {
                        JsonNode json = errorExtIt.next();
                        errorExt.add($.json.createObjectNode()
                                .put("field", json.get("field").asText(""))
                                .put("reason", json.get("codes").get(0).asText().split("\\.")[0])
                                .put("msg", json.get("defaultMessage").asText("")));
                    }
                    message += DETAIL_FLAG + $.json.toJsonString(errorExt);
                }
            }
        }
        logger.error("Request [{}] from [{}] {} , error {} : {}", path, requestFrom, Dew.context().getSourceIP(), busCode, message);
        if (!Dew.dewConfig.getBasic().getFormat().isReuseHttpState()) {
            Resp resp = Resp.customFail(busCode + "", "[" + err + "]" + message);
            return ResponseEntity.status(200).body($.json.toJsonString(resp));
        } else {
            JsonNode jsonNode = $.json.createObjectNode()
                    .set("error", $.json.createObjectNode()
                            .put("code", busCode)
                            .put("message", message));
            return ResponseEntity.status(httpCode).body(jsonNode.toString());
        }
    }

}
