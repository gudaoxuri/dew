package com.ecfront.dew.core.controller;

import com.ecfront.dew.common.Resp;
import com.ecfront.dew.core.Dew;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.web.AbstractErrorController;
import org.springframework.boot.autoconfigure.web.ErrorAttributes;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@RestController
@RequestMapping("${error.path:/error}")
public class ErrorController extends AbstractErrorController {

    protected static final Logger logger = LoggerFactory.getLogger(ErrorController.class);

    @Value("${error.path:/error}")
    private String errorPath;

    public ErrorController(ErrorAttributes errorAttributes) {
        super(errorAttributes);
    }

    @Override
    public String getErrorPath() {
        return errorPath;
    }

    @RequestMapping()
    @ResponseBody
    public Resp<Void> error(HttpServletRequest request) {
        Map<String, Object> error = getErrorAttributes(request, false);
        String path = (String) error.getOrDefault("path", Dew.context().getRequestUri());
        String code = error.getOrDefault("status", -1)+"";
        String err = (String) error.getOrDefault("error", "");
        String message = "[" + err + "]" + error.getOrDefault("message", "");
        logger.error("Request [{}] from {} , error {} : {}", path, Dew.context().getSourceIP(), code, message);
        return Resp.customFail(code, message);
    }

}
