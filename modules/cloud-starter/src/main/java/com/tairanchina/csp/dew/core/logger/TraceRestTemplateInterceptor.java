package com.tairanchina.csp.dew.core.logger;

import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

import java.io.IOException;

public class TraceRestTemplateInterceptor implements ClientHttpRequestInterceptor {

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
        TraceLogWrap.request("RestTemplate", request.getMethod().name(), request.getURI().toURL().toString());
        ClientHttpResponse response = execution.execute(request, body);
        TraceLogWrap.response("RestTemplate", response.getRawStatusCode(), request.getMethod().name(), request.getURI().toURL().toString());
        return response;
    }

}
