package com.ecfront.dew.core.controller;

import com.ecfront.dew.common.Resp;
import com.ecfront.dew.core.Dew;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.web.AbstractErrorController;
import org.springframework.boot.autoconfigure.web.ErrorAttributes;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

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
        HttpStatus status = getStatus(request);
        Throwable error = getError(request);
        logger.error("Request [{}] from {} , error {}:{}",
                Dew.context().getRequestUri(), Dew.context().getSourceIP(), status.toString(), error.getMessage(), error);
        return Resp.customFail(status.toString(), error.getMessage());
    }

    private Throwable getError(HttpServletRequest request) {
        final Throwable exc = (Throwable) request.getAttribute("javax.servlet.error.exception");
        return exc != null ? exc : new Exception("Unexpected error occurred");
    }
}
