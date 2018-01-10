package com.ecfront.dew.feign.fallback;

import com.ecfront.dew.feign.PerformanceServiceClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.ecfront.dew.feign.PerformanceServiceClient;

/**
 * PerformanceServiceFallback
 *
 * @author hzzjb
 * @date 2017/9/27
 */
@Component
public class PerformanceServiceFallback implements PerformanceServiceClient {
    @Override
    public String getAtOnce() {
        return "error";
    }

    @Override
    public String getWithDelay(@PathVariable("time") String delayTime) {
        return "error";
    }

    @Override
    public String postAtOnce() {
        return "error";
    }

    @Override
    public String postWithDelay(String time) {
        return "error";
    }

    @Override
    public Integer doWithLargeTime() {
        return -1;
    }

    @Override
    public Integer doMix() {
        return -1;
    }
}
