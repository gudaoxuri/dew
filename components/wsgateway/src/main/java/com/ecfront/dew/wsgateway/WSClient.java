package com.ecfront.dew.wsgateway;

import com.ecfront.dew.common.$;
import com.ecfront.dew.common.Resp;
import com.ecfront.dew.core.Dew;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;

@Component
public class WSClient {

    @Autowired
    private WSConfig wsConfig;

    @HystrixCommand(fallbackMethod = "serviceFallback")
    public String ws(String token, String message) {
        if (Dew.dewConfig.getSecurity().isTokenInHeader()) {
            return Dew.EB.post("http://" + wsConfig.getWsServiceName() + "/ws", message, new HashMap<String, String>() {{
                put(Dew.dewConfig.getSecurity().getTokenFlag(), token);
            }}, String.class).getBody();
        } else {
            return Dew.EB.post("http://" + wsConfig.getWsServiceName() + "/ws?" + Dew.dewConfig.getSecurity().getTokenFlag() + "=" + token, message, String.class).getBody();
        }
    }

    public String serviceFallback(String token, String message) {
        return $.json.toJsonString(Resp.serverUnavailable(""));
    }

}
