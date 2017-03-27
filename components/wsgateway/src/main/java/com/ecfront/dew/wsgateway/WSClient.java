package com.ecfront.dew.wsgateway;

import com.ecfront.dew.common.JsonHelper;
import com.ecfront.dew.common.Resp;
import com.ecfront.dew.core.Dew;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class WSClient {

    @Autowired
    RestTemplate restTemplate;
    @Autowired
    private WSConfig wsConfig;

    @HystrixCommand(fallbackMethod = "serviceFallback")
    public String ws(String token, String message) {
        return restTemplate.postForEntity(Dew.EB.buildUrl(wsConfig.getWsServiceName(),"ws",token), message, String.class).getBody();
    }

    public String serviceFallback(String token, String message) {
        return JsonHelper.toJsonString(Resp.serverUnavailable(""));
    }


}
